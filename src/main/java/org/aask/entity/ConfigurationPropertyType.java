package org.aask.entity;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "CONFIG_PROPERTY_TYPE")
public class ConfigurationPropertyType {

	@Id
	private Long id;

	private String name;

	private String pathReference;

	private Integer priority;

	private Timestamp insertedDate;

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

	public Timestamp getInsertedDate() {
		return insertedDate;
	}

	public void setInsertedDate(Timestamp insertedDate) {
		this.insertedDate = insertedDate;
	}

	public Set<PropertyLevelConfiguration> getPropertyLevelConfigurations() {
		return propertyLevelConfigurations;
	}

	public void setPropertyLevelConfigurations(Set<PropertyLevelConfiguration> propertyLevelConfigurations) {
		this.propertyLevelConfigurations = propertyLevelConfigurations;
	}

}
