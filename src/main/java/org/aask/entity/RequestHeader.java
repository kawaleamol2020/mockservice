package org.aask.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class RequestHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private String value;

	public RequestHeader() {
	}

	public RequestHeader(String name, String value) {
		this.name = name;
		this.value = value;
	
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
