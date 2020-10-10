package org.aask.repository;

import org.aask.entity.PropertyLevelConfiguration;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyLevelConfigRepository extends CrudRepository<PropertyLevelConfiguration, Long> {

	@Query(value=" SELECT c from PropertyLevelConfiguration c INNER JOIN c.configurationPropertyType cp WHERE cp.id = :propertyTypeId AND c.value=:value")
	PropertyLevelConfiguration findByConfigurationPropertyTypeAndValue(@Param("propertyTypeId")Long propertyTypeId, @Param("value") String value);

	@Query(value=" SELECT c from PropertyLevelConfiguration c INNER JOIN c.configurationPropertyType cp INNER JOIN c.mockType mt WHERE cp.id = :propertyTypeId AND mt.id = :mockTypeId AND c.value=:value")
	PropertyLevelConfiguration findByConfigurationPropertyTypeAndMockTypeAndValue(@Param("propertyTypeId")Long propertyTypeId, @Param("value") String value,@Param("mockTypeId") Long mockTypeId);

}
