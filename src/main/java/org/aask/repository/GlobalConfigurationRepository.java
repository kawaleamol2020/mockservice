package org.aask.repository;

import org.aask.entity.GlobalConfiguration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalConfigurationRepository extends CrudRepository<GlobalConfiguration, Long> {
	
	public GlobalConfiguration getByEndPointURL(String endPointURL);

}
