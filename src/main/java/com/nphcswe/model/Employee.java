package com.nphcswe.model;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nphcswe.helper.DateDeserialiser;

@Entity
@Table(name = "Employees_Table")
public class Employee {

	private static DecimalFormat df2 = new DecimalFormat("#.##");

	@Id
	@Column(unique = true)
	private String id;

	@Column(unique = true)
	private String login;

	private String name;

	@Column(precision = 2, scale = 11)
	private Double salary;

	@JsonDeserialize(using = DateDeserialiser.class)
	private Date startDate;

	public String getId() {
		return id;
	}

	public String getLogin() {
		return login;
	}

	public String getName() {
		return name;
	}

	public Double getSalary() {
		return salary;
	}

	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getstartDate() {
		return startDate;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSalary(Double salary) {

		if (salary != null) {
			df2.setRoundingMode(RoundingMode.DOWN);
			salary = Double.valueOf(df2.format(salary));
		}
		this.salary = salary;
	}

	public void setstartDate(Date startDate) {
		this.startDate = startDate;
	}

	@JsonIgnore
	public boolean isNullName() {
		if (name == null || name.trim().isEmpty()) {
			return true;
		}
		return false;
	}

	// Validate Null and Empty fields
	@JsonIgnore
	public boolean isNullId() {
		if (id == null || id.trim().isEmpty()) {
			return true;
		}
		return false;
	}

	@JsonIgnore
	public boolean isNullSalary() {
		if (salary == null) {
			return true;
		}

		return false;
	}

	@JsonIgnore
	public boolean isNullLogin() {
		if (login == null || login.trim().isEmpty()) {
			return true;
		}
		return false;
	}

	@JsonIgnore
	public boolean isNullstartDate() {
		if (startDate == null) {
			return true;
		}
		return false;
	}

	// Validate salary > 0
	@JsonIgnore
	public boolean isValidSalary() {
		if (isNullSalary() || salary < 0) {
			return false;
		}

		return true;
	}

	@JsonIgnore
	public boolean isValidStartDate() {
		if (startDate == null) {
			return false;
		}
		return true;
	}

	@Override
	@JsonIgnore
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getClass().getSimpleName());
		sb.append("[");
		sb.append("id=" + this.id + ",");
		sb.append("login=" + this.login + ",");
		sb.append("name=" + this.name + ",");
		sb.append("salary=" + this.salary + ",");
		sb.append("startDate=" + this.startDate);
		sb.append("]");
		return sb.toString();
	}

	@JsonIgnore
	public Employee employeeUpdate(Employee employee) {
		if (!employee.isNullLogin()) {
			this.setLogin(employee.getLogin());
		}

		if (!employee.isNullName()) {
			this.setName(employee.getName());
		}

		if (!employee.isNullSalary()) {
			this.setSalary(employee.getSalary());
		}

		if (employee.isValidStartDate()) {
			this.setstartDate(employee.getstartDate());
		}

		return this;
	}
}