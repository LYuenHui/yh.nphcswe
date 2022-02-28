package com.nphcswe.model;

import java.util.List;

public class SearchResult {

	private List<Employee> results;

	public SearchResult(List<Employee> result) {
		this.results = result;
	}

	public List<Employee> getResults() {
		return results;
	}

	public void setResult(List<Employee> result) {
		this.results = result;
	}

}