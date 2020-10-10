package org.aask.service;

import java.util.List;

import org.aask.bean.MockConfigPropertyType;
import org.aask.bean.MockGlobalConfiguration;
import org.aask.bean.MockPropertyLevelConfig;
import org.aask.util.MOCK_TYPE;


public interface ConfigurationService {

	public void updateConfiguration(MockGlobalConfiguration configuration) throws Exception;
	
	public MockGlobalConfiguration getGlobalConfiguration() throws Exception;
	
	public void clearCache();

	public String getEndPointURL() throws Exception;

	public List<MOCK_TYPE> getMockTypes() throws Exception;

	public List<MockPropertyLevelConfig> getPropertyLevelConfiguration() throws Exception;

	public List<MockConfigPropertyType> getConfigurationPropertyTypes() throws Exception;

	public void addPropertyLevelConfiguration(MockPropertyLevelConfig propLevelConfiguration) throws Exception;

	public MOCK_TYPE getMockTypeFromPropLevelConfig(String propertyName, String propertyValue);

	public void deletePropertyLevelConfiguration(Long id);

	public void updatePropertyLevelConfiguration(MockPropertyLevelConfig propLevelConfiguration) throws Exception;

}
