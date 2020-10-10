package org.aask.controller;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import javax.validation.Valid;

import org.aask.bean.MockData;
import org.aask.constant.HttpResponseCodes;
import org.aask.service.MockService;
import org.aask.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MockDataController {

	private Logger logger = LoggerFactory.getLogger(MockDataController.class);

	@Autowired
	private MockService mockService;

	@GetMapping(value = "/mockservice/mockdata")
	public String loadMockedData(Model model) {

		logger.info("loadMockedData start ");
		try {
			model.addAttribute("mockdata", mockService.getMockedDataByPage());
		} catch (Exception e) {
			logger.error("loadMockedData error " + CommonUtils.stackTraceToString(e));
			model.addAttribute("mockdata", Collections.EMPTY_LIST);
		}

		logger.info("loadMockedData end ");

		return "mockdata";
	}

	@GetMapping(value = "/mockservice/deleteMockData")
	public String deleteMockedData(@RequestParam("id") String id) {

		logger.info("deleteMockedData start ");
		try {
			mockService.deleteMockData(id);
		} catch (Exception e) {
			logger.error("deleteMockedData error " + CommonUtils.stackTraceToString(e));
		}

		logger.info("deleteMockedData end ");

		return "redirect:/mockservice/mockdata";
	}

	@GetMapping(value = "/mockservice/editMockData")
	public String editMockedData(@RequestParam("id") String id, Model model) {

		logger.info("editMockedData start ");
		try {
			model.addAttribute("mockDataBean", mockService.getMockedDataById(id));
		} catch (Exception e) {
			logger.error("editMockedData error " + CommonUtils.stackTraceToString(e));
		}

		logger.info("editMockedData end ");

		return "mockdataform";
	}

	@PostMapping(value = "/mockservice/updatemockdata")
	public String updateMockedData(@Valid @ModelAttribute MockData mockDataBean, BindingResult result) {

		logger.info("updateMockedData start ");
		try {
			if (result.hasErrors())
				return "mockdataform";
			mockService.updateMockData(mockDataBean);
		} catch (Exception e) {
			logger.error("updateMockedData error " + CommonUtils.stackTraceToString(e));
		}

		logger.info("updateMockedData end ");

		return "redirect:/mockservice/mockdata";
	}

	@PostMapping(value = "/mockservice/searchmockdata")
	@ResponseBody
	public MockData searchMockedData(@RequestParam("requestData") String requestData) {

		MockData mockData = new MockData();

		logger.info("searchMockedData start ");
		try {
			mockData = mockService.searchMockData("NA", HttpResponseCodes.HTTP_OK, requestData);
		} catch (Exception e) {
			logger.error("searchMockedData error " + CommonUtils.stackTraceToString(e));
		}

		logger.info("searchMockedData end ");

		return mockData;
	}
	
	@GetMapping(value = "/mockservice/cleanmockdata/days/{days}")
	@ResponseBody
	public String cleanArchivedMockData(Model model,@PathVariable("days") Integer days) {

		logger.info("cleanArchivedMockData start ");
		try {
			Calendar calendar = Calendar.getInstance();
			Date date = null;
			if(days > 0){
				calendar.add(Calendar.DAY_OF_MONTH, -days);
				date = calendar.getTime();
			} else {
				date = calendar.getTime();
			}
			mockService.clearArchivedMockData(date);
		} catch (Exception e) {
			logger.error("cleanArchivedMockData error " + CommonUtils.stackTraceToString(e));
			return "Error";
		}

		logger.info("cleanArchivedMockData end ");

		return "Success";
	}
}
