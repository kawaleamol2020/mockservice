package org.aask.bean;

import java.io.Serializable;

public class MockConfigPropertyType implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;
	
	private String pathReference;
	
	private Integer priority;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPathReference() {
		return pathReference;
	}

	public void setPathReference(String pathReference) {
		this.pathReference = pathReference;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
}
