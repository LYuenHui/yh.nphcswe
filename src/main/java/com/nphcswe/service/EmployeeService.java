package com.nphcswe.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.nphcswe.model.Employee;

public interface EmployeeService {
	public Employee getEmployee(String id);

	public void updateEmployee(Employee employee);

	public void saveEmployee(Employee employee);

	public void deleteEmployee(String id);

	public List<Employee> searchEmployeeList(Map<String, String> searchCriteria);

	public Boolean uploadAndSaveEmployee(MultipartFile file);
}
