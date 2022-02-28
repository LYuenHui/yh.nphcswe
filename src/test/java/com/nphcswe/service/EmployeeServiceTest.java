package com.nphcswe.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.nphcswe.helper.ResponseMessage;
import com.nphcswe.helper.exception.BadInputException;
import com.nphcswe.model.Employee;
import com.nphcswe.repository.EmployeeRepository;

@SpringBootTest
public class EmployeeServiceTest {

	@InjectMocks
	private EmployeeServiceImpl employeeService;

	@Mock
	private EmployeeRepository employeeRepository;

	private static final Logger logger = LogManager.getLogger();

	@Test
	public void TestCrud() {

		Throwable exception = null;

		Calendar today = Calendar.getInstance();

		Employee employee = new Employee();
		employee.setId("e00022");
		employee.setLogin("harry");
		employee.setName("Potter Party");
		employee.setSalary(3500.0);
		employee.setstartDate(today.getTime());

		// =========== get ============= //

		// employee doesn't exists
		Mockito.when(employeeRepository.findById(employee.getId())).thenReturn(Optional.empty());

		exception = assertThrows(BadInputException.class, () -> employeeService.getEmployee(employee.getId()));
		assertEquals(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE, exception.getMessage());

		// employee exists
		Mockito.when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

		assertDoesNotThrow(() -> employeeService.getEmployee(employee.getId()));

		Mockito.verify(employeeRepository, Mockito.times(2)).findById(employee.getId());

		// =========== create ============= //

		// employee exist

		Mockito.when(employeeRepository.existsById(employee.getId())).thenReturn(true);

		exception = assertThrows(BadInputException.class, () -> employeeService.saveEmployee(employee));

		assertEquals(ResponseMessage.MSG_ERR_EMPLOYEE_EXIST, exception.getMessage());

		// mandotry field missing

		Employee userForInvalid = new Employee();

		exception = assertThrows(BadInputException.class, () -> employeeService.saveEmployee(userForInvalid));

		assertEquals("[ID,Login,Name,Salary] is mandatory,Invalid Salary,Start Date", exception.getMessage());

		// success
		Mockito.when(employeeRepository.existsById(employee.getId())).thenReturn(false);

		assertDoesNotThrow(() -> employeeService.saveEmployee(employee));

		employeeService.saveEmployee(employee);

		// =========== update ============= //

		// employee doesn't exists
		Mockito.when(employeeRepository.existsById(employee.getId())).thenReturn(false);

		exception = assertThrows(BadInputException.class, () -> employeeService.updateEmployee(employee));

		assertEquals(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE, exception.getMessage());

		// employee exists
		Mockito.when(employeeRepository.existsById(employee.getId())).thenReturn(true);

		Mockito.when(employeeRepository.existsByLoginAndIdNot(employee.getLogin(), employee.getId())).thenReturn(true);

		exception = assertThrows(BadInputException.class, () -> employeeService.updateEmployee(employee));

		assertEquals(ResponseMessage.MSG_ERR_NOT_UNIQUE_EMPLOYEE_LOGIN, exception.getMessage());

		// invalid field

		Mockito.when(employeeRepository.existsByLoginAndIdNot(employee.getLogin(), employee.getId())).thenReturn(false);

		employee.setSalary(-1.0);

		employee.setstartDate(null);

		exception = assertThrows(BadInputException.class, () -> employeeService.updateEmployee(employee));

		assertEquals("Invalid Salary,Start Date", exception.getMessage());

		employee.setstartDate(today.getTime());

		exception = assertThrows(BadInputException.class, () -> employeeService.updateEmployee(employee));

		assertEquals("Invalid Salary", exception.getMessage());

		// success
		employee.setSalary(0.0);

		assertDoesNotThrow(() -> employeeService.updateEmployee(employee));

		Mockito.verify(employeeRepository, Mockito.times(3)).save(employee);

		// =========== delete ============= //

		// employee doesn't exist
		Mockito.when(employeeRepository.existsById(employee.getId())).thenReturn(false);

		exception = assertThrows(BadInputException.class, () -> employeeService.deleteEmployee(employee.getId()));

		assertEquals(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE, exception.getMessage());

		// success
		Mockito.when(employeeRepository.existsById(employee.getId())).thenReturn(true);

		assertDoesNotThrow(() -> employeeService.deleteEmployee(employee.getId()));

		Mockito.verify(employeeRepository, Mockito.times(1)).deleteById(employee.getId());

	}

	@Test
	public void TestValidation() {
		Throwable exception = null;
		Employee employee = new Employee();

		/******** Validate Mandatory Fields **************/
		exception = assertThrows(BadInputException.class,
				() -> employeeService.validateMandatoryFieldUnitTest(employee));

		assertEquals("[ID,Login,Name,Salary] is mandatory", exception.getMessage());

		/**********************/
		employee.setId("e001");
		exception = assertThrows(BadInputException.class,
				() -> employeeService.validateMandatoryFieldUnitTest(employee));
		assertEquals("[Login,Name,Salary] is mandatory", exception.getMessage());

		/**********************/
		employee.setName("Test");
		exception = assertThrows(BadInputException.class,
				() -> employeeService.validateMandatoryFieldUnitTest(employee));
		assertEquals("[Login,Salary] is mandatory", exception.getMessage());

		/**********************/
		employee.setLogin("Test123");
		exception = assertThrows(BadInputException.class,
				() -> employeeService.validateMandatoryFieldUnitTest(employee));
		assertEquals("[Salary] is mandatory", exception.getMessage());

		/**********************/
		employee.setSalary(-1.0);
		employee.setstartDate(new Date());
		assertDoesNotThrow(() -> employeeService.validateMandatoryFieldUnitTest(employee));

		/******** Validate Fields Format **************/
		exception = assertThrows(BadInputException.class, () -> employeeService.validateFormatUnitTest(employee));
		assertEquals("Invalid Salary", exception.getMessage());

		/**********************/
		employee.setstartDate(null);
		exception = assertThrows(BadInputException.class, () -> employeeService.validateFormatUnitTest(employee));
		assertEquals("Invalid Salary,Start Date", exception.getMessage());

		/**********************/
		employee.setSalary(100.00);
		exception = assertThrows(BadInputException.class, () -> employeeService.validateFormatUnitTest(employee));
		assertEquals("Invalid Start Date", exception.getMessage());

	}
}
