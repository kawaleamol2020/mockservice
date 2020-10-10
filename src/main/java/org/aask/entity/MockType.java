package org.aask.entity;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table
public class MockType {

	@Id
	private Long id;

	private String name;
	
	private Timestamp insertedDate;
	
	@OneToMany(mappedBy = "mockType", cascade = CascadeType.ALL)
	private Set<GlobalConfiguration> globalConfigurations;
	
	@OneToMany(mappedBy = "configurationPropertyType", cascade = CascadeType.ALL)
	private Set<PropertyLevelConfiguration> propertyLevelConfigurations;

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

	public Timestamp getInsertedDate() {
		return insertedDate;
	}

	public void setInsertedDate(Timestamp insertedDate) {
		this.insertedDate = insertedDate;
	}

	public Set<GlobalConfiguration> getGlobalConfigurations() {
		return globalConfigurations;
	}

	public void setGlobalConfigurations(Set<GlobalConfiguration> globalConfigurations) {
		this.globalConfigurations = globalConfigurations;
	}

	public Set<PropertyLevelConfiguration> getPropertyLevelConfigurations() {
		return propertyLevelConfigurations;
	}

	public void setPropertyLevelConfigurations(Set<PropertyLevelConfiguration> propertyLevelConfigurations) {
		this.propertyLevelConfigurations = propertyLevelConfigurations;
	}
}	
