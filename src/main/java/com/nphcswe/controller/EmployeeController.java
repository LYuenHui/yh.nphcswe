package com.nphcswe.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nphcswe.helper.ResponseMessage;
import com.nphcswe.model.Employee;
import com.nphcswe.model.ResponseObject;
import com.nphcswe.model.SearchResult;
import com.nphcswe.service.EmployeeService;

@RestController
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	private static final Logger logger = LogManager.getLogger();

	// upload csv
	@RequestMapping(value = "/users/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<ResponseObject> uploadEmployees(@RequestParam("file") MultipartFile file) {

		ResponseObject response = new ResponseObject(ResponseMessage.MSG_SUC_NO_CREATE);

		logger.info("file name: {}, file size: {}", file.getOriginalFilename(), file.getSize());

		Boolean uploadSuccess = employeeService.uploadAndSaveEmployee(file);
		logger.info("uploadSuccesse: {}", uploadSuccess);
		if (uploadSuccess) {
			response.setMessage(ResponseMessage.MSG_SUC_CREATE_UPDATE);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}

		return ResponseEntity.ok().body(response);
	}

	// fetch employee list
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public @ResponseBody SearchResult searchUsers(@RequestParam Map<String, String> searchCriteria) {

		logger.info("search criteria: {}", searchCriteria);

		List<Employee> employeeList = new ArrayList<>();

		employeeList = employeeService.searchEmployeeList(searchCriteria);

		return new SearchResult(employeeList);
	}

	// Create employee
	@RequestMapping(value = "/users", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody ResponseObject createUser(@RequestBody Employee employee) {

		logger.info("request object: {}", employee);

		ResponseObject response = new ResponseObject(ResponseMessage.MSG_SUC_CREATED);

		employeeService.saveEmployee(employee);

		return response;
	}

	// get employee
	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	public @ResponseBody Employee getEmployeeById(@PathVariable String id) {

		logger.info("id: {}", id);

		Employee employee = employeeService.getEmployee(id);
		logger.info("employee unit test: {}", employee);
		return employee;
	}

	// delete employee
	@RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
	public @ResponseBody ResponseObject deleteUserById(@PathVariable String id) {

		logger.info("request object: {}", id);

		ResponseObject response = new ResponseObject(ResponseMessage.MSG_SUC_DELETED);

		employeeService.deleteEmployee(id);

		return response;
	}

	// update employee
	@RequestMapping(value = "/users/{id}", method = { RequestMethod.PUT, RequestMethod.PATCH })
	public @ResponseBody ResponseObject updateUserById(@PathVariable String id, @RequestBody Employee employee) {

		employee.setId(id);

		logger.info("id: {}, request object: {}", id, employee);

		ResponseObject response = new ResponseObject(ResponseMessage.MSG_SUC_UPDATED);

		employeeService.updateEmployee(employee);

		return response;
	}

}
