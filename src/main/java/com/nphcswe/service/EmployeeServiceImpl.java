package com.nphcswe.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nphcswe.helper.CsvReader;
import com.nphcswe.helper.ResponseMessage;
import com.nphcswe.helper.exception.BadInputException;
import com.nphcswe.model.Employee;
import com.nphcswe.repository.EmployeeRepository;

@Service
public class EmployeeServiceImpl implements EmployeeService {
	private static final Logger logger = LogManager.getLogger();

	private static long FILE_SIZE_LIMIT_10MB = 10485760;

	@Autowired
	private EmployeeRepository employeeRepository;

	// create employee
	@Override
	public void saveEmployee(Employee employee) {

		validateCreateEmployee(employee);
		createOrUpdateEmployee(employee);

	}

	@Override
	public Employee getEmployee(String id) {
		Optional<Employee> employee = employeeRepository.findById(id);
		if (!employee.isPresent()) {
			throw new BadInputException(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE);
		}
		logger.info("employee Unit test : {}", employee.get());
		return employee.get();
	}

	@Override
	public void updateEmployee(Employee employee) {
		validateUpdateEmployee(employee);
		createOrUpdateEmployee(employee);

	}

	@Override
	public void deleteEmployee(String id) {
		Employee employee = new Employee();
		employee.setId(id);
		validateNonExistingEmployeeID(employee);
		employeeRepository.deleteById(id);

	}

	@Override
	public List<Employee> searchEmployeeList(Map<String, String> searchCriteria) {
		double minSalary = searchCriteria.get("minSalary") != null
				? Double.parseDouble(searchCriteria.get("minSalary").toString())
				: 0;
		double maxSalary = searchCriteria.get("maxSalary") != null
				? Double.parseDouble(searchCriteria.get("maxSalary").toString())
				: 4000;
		logger.info("min salary: {}", minSalary);
		logger.info("max salary: {}", maxSalary);
		List<Employee> employeeList = employeeRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
		List<Employee> result = new ArrayList();
		for (int i = 0; i < employeeList.size(); i++) {
			if (employeeList.get(i).getSalary() > minSalary && employeeList.get(i).getSalary() < maxSalary) {
				result.add(employeeList.get(i));

			}
		}
		return result;

	}

	@Override
	public Boolean uploadAndSaveEmployee(MultipartFile file) {
		boolean uploadSuccess = false;
		// validateCsvSize(file);

		List<Employee> employeeFile = new ArrayList<>();

		String csvContent = readAndFilterCsvData(file);

		logger.info("filtered content: \n{}\n", csvContent);

		employeeFile = CsvReader.toList(Employee.class, csvContent.getBytes());

		validateUserList(employeeFile);

		for (Employee employee : employeeFile) {

			Optional<Employee> optEmployee = employeeRepository.findById(employee.getId());
			if (optEmployee.isPresent()) {
				Employee employeeDb = optEmployee.get();

				employeeDb.employeeUpdate(employee);

				Employee employeeUpdate = employeeRepository.save(employeeDb);
				if (!employeeUpdate.equals(employee)) {
					uploadSuccess = true;
				}
			} else {
				employeeRepository.save(employee);
				uploadSuccess = true;
			}

		}
		return uploadSuccess;

	}

	private String readAndFilterCsvData(MultipartFile file) {
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));) {

			return fileReader.lines().filter(line -> !line.startsWith("#"))
					.collect(Collectors.joining(System.lineSeparator()));
		} catch (IOException ex) {
			throw new BadInputException(ResponseMessage.MSG_ERR_FILE_READING);
		}
	}

	private void validateCsvSize(MultipartFile file) {

		if (file.getSize() > FILE_SIZE_LIMIT_10MB) {
			throw new BadInputException(ResponseMessage.MSG_ERR_FILE_SIZE_LIMIT_10MB);
		}

	}

	public void validateUserList(List<Employee> employeeList) {

		List<String> errorList = new ArrayList<>();

		errorList.addAll(validateDuplicateUserList(employeeList));

		for (int i = 0; i < employeeList.size(); i++) {
			StringJoiner sj = validateMandatoryField(employeeList.get(i));
			if (sj.length() > 0) {

				errorList.add(String.format(ResponseMessage.MSG_ERR_MANDATORY_ROW_FIELD, i + 2, sj.toString()));
			} else {

				sj = validateFormat(employeeList.get(i));
				// i + 2 to show row number in CSV file
				// sj.toString return the error field
				if (sj.length() > 0) {
					errorList.add(String.format(ResponseMessage.MSG_ERR_INVALID_FORMAT_FIELD, i + 2, sj.toString()));

				}
			}
		}

		if (errorList.size() > 0) {
			throw new BadInputException(errorList.toString());
		}

		// validate database records with CSV records
		for (int a = 0; a < employeeList.size(); a++) {
			Employee validateEmployee = employeeList.get(a);
			if (employeeRepository.existsByLoginAndIdNot(validateEmployee.getLogin(), validateEmployee.getId())) {
				errorList.add(String.format(ResponseMessage.MSG_ERR_NOT_UNIQUE_ROW_FIELD, a + 2,
						validateEmployee.getLogin()));
			}
		}

		if (errorList.size() > 0) {
			throw new BadInputException(errorList.toString());
		}
	}

	private List<String> validateDuplicateUserList(List<Employee> employeeList) {

		Map<String, Integer> duplicateIdList = new HashMap<>();
		Map<String, Integer> duplicateLoginList = new HashMap<>();
		List<String> errorList = new ArrayList<>();

		employeeList.stream().forEach(item -> {
			// check duplicate ID , if val = null , then map val = 1 else val +1
			duplicateIdList.compute(item.getId().toLowerCase(), (key, val) -> val == null ? 1 : val + 1);
			logger.info("duplicateIdList : {}", duplicateIdList);
			// check duplicate Login , if val = null , then map val = 1 else val +1
			duplicateLoginList.compute(item.getLogin().toLowerCase(), (key, val) -> val == null ? 1 : val + 1);
			logger.info("duplicateLoginList : {}", duplicateLoginList);
		});

		String duplicateList = duplicateIdList.entrySet().stream().filter(map -> map.getValue() > 1)
				.map(map -> map.getKey()).collect(Collectors.joining(","));
		logger.info("duplicateList : {}", duplicateList);

		if (!duplicateList.trim().isEmpty()) {
			errorList.add(String.format(ResponseMessage.MSG_ERR_NOT_UNIQUE_IDS, duplicateList));
		}

		String duplicateLogin = duplicateLoginList.entrySet().stream().filter(map -> map.getValue() > 1)
				.map(map -> map.getKey()).collect(Collectors.joining(","));
		logger.info("duplicateLogin : {}", duplicateLogin);

		if (!duplicateLogin.trim().isEmpty()) {
			errorList.add(String.format(ResponseMessage.MSG_ERR_NOT_UNIQUE_LOGINS, duplicateLogin));
		}

		return errorList;

	}

	private void createOrUpdateEmployee(Employee employee) {

		StringJoiner errorMessage = new StringJoiner(",");

		StringJoiner sj = validateMandatoryField(employee);

		logger.info("request object: {}", sj);
		if (sj.length() > 0) {
			errorMessage.add(String.format(ResponseMessage.MSG_ERR_MANDATORY_FIELD, sj.toString()));
		}

		sj = validateFormat(employee);

		if (sj.length() > 0) {
			errorMessage.add(String.format(ResponseMessage.MSG_ERR_INVALID_FIELD, sj.toString()));
		}

		if (errorMessage.length() > 0) {
			throw new BadInputException(errorMessage.toString());
		}

		Optional<Employee> employeeDb = employeeRepository.findById(employee.getId());

		Employee updateEmployee = employee;

		if (employeeDb.isPresent()) {
			updateEmployee = employeeDb.get().employeeUpdate(employee);
		}
		employeeRepository.save(employee);
	}

	private void validateCreateEmployee(Employee employee) {
		validateExistingEmployeeID(employee);
		validateDuplicateLogin(employee);

	}

	private void validateUpdateEmployee(Employee employee) {
		validateNonExistingEmployeeID(employee);
		validateDuplicateLogin(employee);

	}

	// Validation
	private void validateExistingEmployeeID(Employee employee) {
		if (employeeRepository.existsById(employee.getId())) {
			throw new BadInputException(ResponseMessage.MSG_ERR_EMPLOYEE_EXIST);
		}

	}

	private void validateNonExistingEmployeeID(Employee employee) {
		if (!employeeRepository.existsById(employee.getId())) {
			throw new BadInputException(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE);
		}

	}

	private void validateDuplicateLogin(Employee employee) {
		if (employeeRepository.existsByLoginAndIdNot(employee.getLogin(), employee.getId())) {
			throw new BadInputException(ResponseMessage.MSG_ERR_NOT_UNIQUE_EMPLOYEE_LOGIN);
		}

	}

	public void validateFormatUnitTest(Employee employee) {
		StringJoiner sj = validateFormat(employee);

		if (sj.length() > 0) {
			throw new BadInputException(String.format(ResponseMessage.MSG_ERR_INVALID_FIELD, sj.toString()));
		}

	}

	public void validateMandatoryFieldUnitTest(Employee employee) {
		StringJoiner sj = validateMandatoryField(employee);

		if (sj.length() > 0) {
			throw new BadInputException(String.format(ResponseMessage.MSG_ERR_MANDATORY_FIELD, sj.toString()));
		}

	}

	private StringJoiner validateMandatoryField(Employee employee) {

		StringJoiner sj = new StringJoiner(",");

		if (employee.isNullId()) {
			sj.add("ID");
		}

		if (employee.isNullLogin()) {
			sj.add("Login");
		}

		if (employee.isNullName()) {
			sj.add("Name");
		}

		if (employee.isNullSalary()) {
			sj.add("Salary");
		}

		return sj;
	}

	private StringJoiner validateFormat(Employee employee) {

		StringJoiner invalidField = new StringJoiner(",");

		if (!employee.isValidSalary()) {
			invalidField.add("Salary");
		}

		if (!employee.isValidStartDate()) {
			invalidField.add("Start Date");
		}

		return invalidField;
	}

}
