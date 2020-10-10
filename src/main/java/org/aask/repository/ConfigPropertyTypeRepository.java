package org.aask.repository;

import java.util.List;

import org.aask.entity.ConfigurationPropertyType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigPropertyTypeRepository extends CrudRepository<ConfigurationPropertyType, Long> {

	List<ConfigurationPropertyType> findAllByOrderByPriorityAsc();

}
