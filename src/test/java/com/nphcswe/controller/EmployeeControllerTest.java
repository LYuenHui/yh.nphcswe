package com.nphcswe.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nphcswe.helper.ResponseMessage;
import com.nphcswe.helper.exception.BadInputException;
import com.nphcswe.model.Employee;
import com.nphcswe.model.ResponseObject;
import com.nphcswe.model.SearchResult;
import com.nphcswe.service.EmployeeServiceImpl;

@WebMvcTest
public class EmployeeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EmployeeServiceImpl employeeServiceImpl;

	@Autowired
	private ObjectMapper objectMapper;

	/************ get one user ***************/
	@Test
	public void getEmployeeSuccess() throws Exception {

		Employee employee = new Employee();

		when(employeeServiceImpl.getEmployee("e0001")).thenReturn(employee);

		this.mockMvc.perform(get("/users/e0001")).andExpect(status().isOk())
				.andExpect(content().string(containsString(objectMapper.writeValueAsString(employee))));
	}

	@Test
	public void GetEmployeeFail() throws Exception {

		doThrow(new BadInputException(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE)).when(employeeServiceImpl)
				.getEmployee("e0001");

		this.mockMvc.perform(get("/users/e0001")).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString(objectMapper
						.writeValueAsString(new ResponseObject(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE)))));
	}

	/************ create user ***************/

	@Test
	public void createEmployeeSuccess() throws Exception {

		Employee employee = new Employee();

		doNothing().when(employeeServiceImpl).saveEmployee(employee);

		this.mockMvc
				.perform(post("/users").contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(objectMapper.writeValueAsString(employee)))
				.andExpect(status().isCreated()).andExpect(content()
						.string(objectMapper.writeValueAsString(new ResponseObject(ResponseMessage.MSG_SUC_CREATED))));
	}

	/************ update user ***************/

	@Test
	public void putEmployeeSuccess() throws Exception {

		Employee employee = new Employee();

		doNothing().when(employeeServiceImpl).updateEmployee(employee);

		this.mockMvc
				.perform(put("/users/e0001").contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(objectMapper.writeValueAsString(employee)))
				.andExpect(status().isOk()).andExpect(content()
						.string(objectMapper.writeValueAsString(new ResponseObject(ResponseMessage.MSG_SUC_UPDATED))));
	}

	@Test
	public void patchEmployeeSuccess() throws Exception {

		Employee employee = new Employee();

		doNothing().when(employeeServiceImpl).updateEmployee(employee);

		this.mockMvc
				.perform(patch("/users/e0001").contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(objectMapper.writeValueAsString(employee)))
				.andExpect(status().isOk()).andExpect(content()
						.string(objectMapper.writeValueAsString(new ResponseObject(ResponseMessage.MSG_SUC_UPDATED))));
	}

	/************ delete user ***************/
	@Test
	public void deleteEmployeeSuccess() throws Exception {

		Employee employee = new Employee();
		when(employeeServiceImpl.getEmployee("e0001")).thenReturn(employee);

		this.mockMvc.perform(delete("/users/e0001")).andExpect(status().isOk()).andExpect(content().string(
				containsString(objectMapper.writeValueAsString(new ResponseObject(ResponseMessage.MSG_SUC_DELETED)))));
	}

	@Test
	public void deleteEmployeeFail() throws Exception {

		Employee employee = new Employee();
		doThrow(new BadInputException(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE)).when(employeeServiceImpl)
				.deleteEmployee("e0001");

		this.mockMvc.perform(delete("/users/e0001")).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString(objectMapper
						.writeValueAsString(new ResponseObject(ResponseMessage.MSG_ERR_NO_SUCH_EMPLOYEE)))));
	}

	/************ fetch all users ***************/
	@Test
	public void fetchAllEmployeeSuccess() throws Exception {

		when(employeeServiceImpl.searchEmployeeList(anyMap())).thenReturn(null);

		this.mockMvc.perform(get("/users/")).andExpect(status().isOk())
				.andExpect(content().string(containsString(objectMapper.writeValueAsString(new SearchResult(null)))));

	}

	/************ CSV Unit Test ***************/
	@Test
	public void csvUploadUserSuccessNoData() throws Exception {

		MockMultipartFile multipartFile = new MockMultipartFile("file", "testData.csv", MediaType.TEXT_PLAIN_VALUE,
				"testing".getBytes());

		when(employeeServiceImpl.uploadAndSaveEmployee(multipartFile)).thenReturn(false);

		this.mockMvc.perform(multipart("/users/upload").file(multipartFile)).andExpect(status().isOk())
				.andExpect(content().string(containsString(
						objectMapper.writeValueAsString(new ResponseObject(ResponseMessage.MSG_SUC_NO_CREATE)))));

	}

	@Test
	public void csvUploadUserSuccessWithData() throws Exception {

		MockMultipartFile multipartFile = new MockMultipartFile("file", "testData.csv", MediaType.TEXT_PLAIN_VALUE,
				"testing".getBytes());

		when(employeeServiceImpl.uploadAndSaveEmployee(multipartFile)).thenReturn(true);

		this.mockMvc.perform(multipart("/users/upload").file(multipartFile)).andExpect(status().isCreated())
				.andExpect(content().string(containsString(
						objectMapper.writeValueAsString(new ResponseObject(ResponseMessage.MSG_SUC_CREATE_UPDATE)))));

	}

	@Test
	public void csvUploadUserFail() throws Exception {

		MockMultipartFile multipartFile = new MockMultipartFile("file", "testData.csv", MediaType.TEXT_PLAIN_VALUE,
				"testing".getBytes());

		doThrow(new BadInputException(ResponseMessage.MSG_ERR_CSV_COLUMN_FORMAT)).when(employeeServiceImpl)
				.uploadAndSaveEmployee(multipartFile);

		this.mockMvc.perform(multipart("/users/upload").file(multipartFile)).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString(objectMapper
						.writeValueAsString(new ResponseObject(ResponseMessage.MSG_ERR_CSV_COLUMN_FORMAT)))));

	}

}
