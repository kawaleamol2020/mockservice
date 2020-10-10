package org.aask.controller;

import javax.validation.Valid;

import org.aask.bean.MockGlobalConfiguration;
import org.aask.bean.MockPropertyLevelConfig;
import org.aask.exception.DuplicatePropertyConfigurationException;
import org.aask.service.ConfigurationService;
import org.aask.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ConfigrationController {

	private Logger logger = LoggerFactory.getLogger(ConfigrationController.class);

	@Autowired
	private ConfigurationService configurationService;

	@GetMapping("/mockservice/configuration")
	public String renderMockConfiguration(Model model) {

		logger.info("renderMockConfiguration start ");
		try {
			configurationService.clearCache();
			loadConfiguration(model);
			model.addAttribute("globalConfiguration", configurationService.getGlobalConfiguration());
			model.addAttribute("propLevelConfiguration", new MockPropertyLevelConfig());
		} catch (Exception e) {
			logger.error("renderMockConfiguration error : " + CommonUtils.stackTraceToString(e));
		}
		logger.info("renderMockConfiguration end ");

		return "configuration";
	}

	@PostMapping("/mockservice/configuration")
	public String updateConfiguration(Model model,
			@Valid @ModelAttribute("globalConfiguration") MockGlobalConfiguration globalConfiguration,
			BindingResult result) {

		logger.info("updateConfiguration start ");

		try {

			if (!result.hasErrors()) {
				configurationService.updateConfiguration(globalConfiguration);
				logger.info("clear cache initiated");
				configurationService.clearCache();
			} else {
				logger.info("updateConfiguration validation failed : " + result.getAllErrors());
				loadConfiguration(model);
				model.addAttribute("propLevelConfiguration", new MockPropertyLevelConfig());
				return "configuration";
			}

		} catch (Exception e) {
			logger.error("updateConfiguration error : " + CommonUtils.stackTraceToString(e));
		}

		logger.info("updateConfiguration end ");

		return "redirect:/mockservice/configuration";
	}

	@PostMapping("/mockservice/addPropLvlConfiguration")
	public String addPropertyLevelConfiguration(Model model,
			@Valid @ModelAttribute("propLevelConfiguration") MockPropertyLevelConfig propLevelConfiguration,
			BindingResult result) {

		logger.info("addPropertyLevelConfiguration start ");

		try {

			if (!result.hasErrors()) {
				configurationService.addPropertyLevelConfiguration(propLevelConfiguration);
				logger.info("clear cache initiated");
				configurationService.clearCache();
			} else {
				logger.info("updateConfiguration validation failed : " + result.getAllErrors());
				model.addAttribute("globalConfiguration", configurationService.getGlobalConfiguration());
				loadConfiguration(model);
				return "configuration";
			}

		} catch (DuplicatePropertyConfigurationException duplicateError) {
			loadConfiguration(model);
			model.addAttribute("isDuplicateError", true);
			return "configuration";
		} catch (Exception e) {
			logger.error("addPropertyLevelConfiguration error : " + CommonUtils.stackTraceToString(e));
		}

		logger.info("addPropertyLevelConfiguration end ");

		return "redirect:/mockservice/configuration";
	}
	
	@PostMapping("/mockservice/savePropLvlConfiguration")
	@ResponseBody
	public String updatePropertyLevelConfiguration(@RequestBody MockPropertyLevelConfig propLevelConfiguration) {

		logger.info("updatePropertyLevelConfiguration start ");

		try {

			configurationService.updatePropertyLevelConfiguration(propLevelConfiguration);
			logger.info("clear cache initiated");
			configurationService.clearCache();
			
		} catch (DuplicatePropertyConfigurationException duplicateError) {
			return "duplicate";
		} catch (Exception e) {
			logger.error("addPropertyLevelConfiguration error : " + CommonUtils.stackTraceToString(e));
			return "error";
		}

		return "success";
	}
	
	@GetMapping(value = "/mockservice/deletePropLvlConfiguration")
	public String deletePropertyLevelConfiguration(@RequestParam("id") Long id) {

		logger.info("deletePropertyLevelConfiguration start ");
		try {
			configurationService.deletePropertyLevelConfiguration(id);
			logger.info("clear cache initiated");
			configurationService.clearCache();
		} catch (Exception e) {
			logger.error("deletePropertyLevelConfiguration error " + CommonUtils.stackTraceToString(e));
		}

		logger.info("deletePropertyLevelConfiguration end ");

		return "redirect:/mockservice/configuration";
	}

	private void loadConfiguration(Model model) {
		try {
			model.addAttribute("isDuplicateError", false);
			model.addAttribute("mockTypes", configurationService.getMockTypes());
			model.addAttribute("propertyTypes", configurationService.getConfigurationPropertyTypes());
			model.addAttribute("propertyLevelConfigList", configurationService.getPropertyLevelConfiguration());
		} catch (Exception e) {
			logger.error("loadConfiguration error : " + CommonUtils.stackTraceToString(e));
		}
	}
}
