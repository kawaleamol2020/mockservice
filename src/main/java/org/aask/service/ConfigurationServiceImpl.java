package org.aask.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.aask.bean.MockConfigPropertyType;
import org.aask.bean.MockGlobalConfiguration;
import org.aask.bean.MockPropertyLevelConfig;
import org.aask.entity.ConfigurationPropertyType;
import org.aask.entity.GlobalConfiguration;
import org.aask.entity.MockType;
import org.aask.entity.PropertyLevelConfiguration;
import org.aask.exception.DuplicatePropertyConfigurationException;
import org.aask.exception.MockConfigurationException;
import org.aask.repository.ConfigPropertyTypeRepository;
import org.aask.repository.GlobalConfigurationRepository;
import org.aask.repository.MockTypeRepository;
import org.aask.repository.PropertyLevelConfigRepository;
import org.aask.util.CommonUtils;
import org.aask.util.MOCK_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	@Autowired
	private GlobalConfigurationRepository configurationRepository;

	@Autowired
	private MockTypeRepository mockTypeRepository;

	@Autowired

	private PropertyLevelConfigRepository propertyLevelConfigRepository;
	@Autowired
	private ConfigPropertyTypeRepository configPropertyTypeRepository;

	@Autowired
	CacheManager cacheManager;

	Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

	@Cacheable(cacheNames = "globalConfiguration",key="'globalConfiguration'",sync=true)
	public MockGlobalConfiguration getGlobalConfiguration() throws Exception {
		logger.info("getConfiguration start");
		MockGlobalConfiguration configuration = new MockGlobalConfiguration();
		List<GlobalConfiguration> persistedEntityList = (List<GlobalConfiguration>) configurationRepository.findAll();
		if (!CollectionUtils.isEmpty(persistedEntityList)) {
			GlobalConfiguration persistedEntity = persistedEntityList.get(0);
			configuration.setEndPointURL(persistedEntity.getEndPointURL());
			if (persistedEntity.getMockType() != null && MOCK_TYPE.containsId(persistedEntity.getMockType().getId()))
				configuration.setMockType(MOCK_TYPE.getMockTypeById(persistedEntity.getMockType().getId()));
			configuration.setNoOfRowsDisplay(persistedEntity.getNoOfRowsDisplay());
		}
		logger.info("getConfiguration end");
		return configuration;
	}

	public void clearCache() {
		cacheManager.getCacheNames().stream().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
	}

	@Override
	@Cacheable(cacheNames = "EndPointURL",key="'EndPointURL'",sync=true)
	public String getEndPointURL() throws Exception {
		logger.info("getEndPointURL start");
		MockGlobalConfiguration configuration = getGlobalConfiguration();
		if (configuration.getEndPointURL() == null) {
			String errorMessage = "End Point URL not configured in system. Please try /configuration";
			logger.error("getEndPointURL error " + errorMessage);
			throw new MockConfigurationException(errorMessage);
		}
		logger.info("getEndPointURL end");
		return configuration.getEndPointURL();
	}

	@Cacheable(cacheNames = "mockType",key="'mockType'",sync=true)
	public List<MOCK_TYPE> getMockTypes() throws Exception {
		logger.info("getMockTypes start");
		List<MOCK_TYPE> mockTypeList = new ArrayList<>();
		Iterable<MockType> mockList = mockTypeRepository.findAll();

		mockList.forEach(mt -> {
			if (MOCK_TYPE.contains(mt.getName())) {
				MOCK_TYPE mockType = MOCK_TYPE.valueOf(mt.getName());
				mockType.setId(mt.getId());
				mockTypeList.add(mockType);
			}
		});
		logger.info("mockTypeList count " + mockTypeList.size());

		logger.info("getMockTypes end");
		return mockTypeList;
	}

	@Override
	@Cacheable(cacheNames = "propertyLevelConfiguration",key="'propertyLevelConfiguration'",sync=true)
	public List<MockPropertyLevelConfig> getPropertyLevelConfiguration() throws Exception {
		logger.info("getPropertyLevelConfiguration start");
		List<MockPropertyLevelConfig> propertyLevelConfigList = new ArrayList<>();
		List<PropertyLevelConfiguration> configurationList = (List<PropertyLevelConfiguration>) propertyLevelConfigRepository
				.findAll();
		configurationList.forEach(prop -> {
			MockPropertyLevelConfig bean = new MockPropertyLevelConfig();
			if (prop.getMockType() != null && MOCK_TYPE.containsId(prop.getMockType().getId()))
				bean.setMockType(MOCK_TYPE.getMockTypeById(prop.getMockType().getId()));
			bean.setId(prop.getId());
			bean.setValue(prop.getValue());
			bean.setUpdatedDate(prop.getUpdatedDate());
			MockConfigPropertyType configurationPropertyType = new MockConfigPropertyType();
			BeanUtils.copyProperties(prop.getConfigurationPropertyType(), configurationPropertyType);
			bean.setConfigurationPropertyType(configurationPropertyType);
			propertyLevelConfigList.add(bean);
		});
		logger.info("propertyLevelConfigList count " + propertyLevelConfigList.size());

		logger.info("getPropertyLevelConfiguration end");
		return propertyLevelConfigList;
	}

	@Override
	@Cacheable(cacheNames = "propertyTypes",key="'propertyTypes'",sync=true)
	public List<MockConfigPropertyType> getConfigurationPropertyTypes() throws Exception {
		logger.info("getConfigurationPropertyTypes start");
		List<MockConfigPropertyType> configPropertyTypes = new ArrayList<>();
		List<ConfigurationPropertyType> propertyTypes = configPropertyTypeRepository
				.findAllByOrderByPriorityAsc();
		propertyTypes.forEach(config -> {
			MockConfigPropertyType bean = new MockConfigPropertyType();
			BeanUtils.copyProperties(config, bean);
			configPropertyTypes.add(bean);
		});
		logger.info("configPropertyTypes count " + configPropertyTypes.size());

		logger.info("getConfigurationPropertyTypes start");
		return configPropertyTypes;
	}

	@Override
	public void updateConfiguration(MockGlobalConfiguration configuration) throws Exception {
		logger.info("updateConfiguration start");
		logger.info(configuration.toString());
		GlobalConfiguration entity = new GlobalConfiguration();
		entity.setEndPointURL(configuration.getEndPointURL());
		Optional<MockType> mockType = mockTypeRepository.findById(configuration.getMockType().getId());
		entity.setMockType(mockType.get());
		entity.setNoOfRowsDisplay(configuration.getNoOfRowsDisplay());
		List<GlobalConfiguration> persistedEntityList = (List<GlobalConfiguration>) configurationRepository.findAll();
		if (!CollectionUtils.isEmpty(persistedEntityList))
			entity.setId(persistedEntityList.get(0).getId());

		entity.setUpdatedDate(new Timestamp(new Date().getTime()));
		configurationRepository.save(entity);
		logger.info("updateConfiguration end");
	}

	@Override
	public void addPropertyLevelConfiguration(MockPropertyLevelConfig propLevelConfiguration) throws Exception {
		logger.info("addPropertyLevelConfiguration start");
		PropertyLevelConfiguration propertyLevelConfiguration = propertyLevelConfigRepository
				.findByConfigurationPropertyTypeAndValue(propLevelConfiguration.getConfigurationPropertyType().getId(),
						propLevelConfiguration.getValue());
		if (propertyLevelConfiguration != null)
			throw new DuplicatePropertyConfigurationException("Property Level configuration already exist in system");

		PropertyLevelConfiguration propertyConfigEntity = new PropertyLevelConfiguration();
		propertyConfigEntity.setValue(propLevelConfiguration.getValue());
		Optional<ConfigurationPropertyType> propertyType = configPropertyTypeRepository
				.findById(propLevelConfiguration.getConfigurationPropertyType().getId());
		propertyConfigEntity.setConfigurationPropertyType(propertyType.get());
		Optional<MockType> mockType = mockTypeRepository.findById(propLevelConfiguration.getMockType().getId());
		propertyConfigEntity.setMockType(mockType.get());
		propertyConfigEntity.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));

		propertyLevelConfigRepository.save(propertyConfigEntity);

		logger.info("addPropertyLevelConfiguration start");
	}

	@Override
	public MOCK_TYPE getMockTypeFromPropLevelConfig(String name, String headerValue) {
		
		try {
			List<MockPropertyLevelConfig> propertyLevelConfiguration = this.getPropertyLevelConfiguration();
			List<MockPropertyLevelConfig> matchedPropLvlConfig = propertyLevelConfiguration.stream()
			.filter(propConfig -> name.equalsIgnoreCase(propConfig.getConfigurationPropertyType().getName()) && propConfig.getValue().equals(headerValue))
			.collect(Collectors.toList());
			if(!CollectionUtils.isEmpty(matchedPropLvlConfig)){
				return matchedPropLvlConfig.get(0).getMockType();
			}
		} catch (Exception e) {
			logger.error("getMockTypeFromPropLevelConfig error " + CommonUtils.stackTraceToString(e));
		}
		return null;
	}

	@Override
	public void deletePropertyLevelConfiguration(Long id) {
		logger.info("deletePropertyLevelConfiguration start");
		propertyLevelConfigRepository.deleteById(id);
		logger.info("deletePropertyLevelConfiguration end");
		
	}

	@Override
	public void updatePropertyLevelConfiguration(MockPropertyLevelConfig propLevelConfiguration) throws Exception {
		
		logger.info("updatePropertyLevelConfiguration start");
		PropertyLevelConfiguration propertyLevelConfiguration = propertyLevelConfigRepository
				.findByConfigurationPropertyTypeAndMockTypeAndValue(propLevelConfiguration.getConfigurationPropertyType().getId(),
						propLevelConfiguration.getValue(),propLevelConfiguration.getMockType().getId());
		if (propertyLevelConfiguration != null)
			throw new DuplicatePropertyConfigurationException("Property Level configuration already exist in system");

		PropertyLevelConfiguration propertyConfigEntity = new PropertyLevelConfiguration();
		propertyConfigEntity.setId(propLevelConfiguration.getId());
		propertyConfigEntity.setValue(propLevelConfiguration.getValue());
		Optional<ConfigurationPropertyType> propertyType = configPropertyTypeRepository
				.findById(propLevelConfiguration.getConfigurationPropertyType().getId());
		propertyConfigEntity.setConfigurationPropertyType(propertyType.get());
		Optional<MockType> mockType = mockTypeRepository.findById(propLevelConfiguration.getMockType().getId());
		propertyConfigEntity.setMockType(mockType.get());
		propertyConfigEntity.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));

		propertyLevelConfigRepository.save(propertyConfigEntity);

		logger.info("updatePropertyLevelConfiguration start");
	}
}
