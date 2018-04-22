package org.smart.admin.controller;

import org.smart.admin.repository.DepartmentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

	@RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
	public String home(Model model) {
		model.addAttribute("department", DepartmentRepository.getDepartment("01"));
		return "index";
	}

	@RequestMapping(value = { "/department/info/{departmentCode}" }, method = RequestMethod.GET)
	public String getDepartmentInfo(@PathVariable String departmentCode, Model model) {
		model.addAttribute("department", DepartmentRepository.getDepartment(departmentCode));
		return "widget/departmentInfoWidget";
	}
	
	@RequestMapping(value = { "/department/communes/{departmentCode}" }, method = RequestMethod.GET)
	public String getCommunesWidget(@PathVariable String departmentCode, Model model) {
		model.addAttribute("department", DepartmentRepository.getDepartment(departmentCode));
		return "widget/communesWidget";
	}
	
	@RequestMapping(value = { "/selection/{departmentCode}/{inseeCommune}" }, method = RequestMethod.GET)
	public String selection(@PathVariable String departmentCode, @PathVariable String inseeCommune, Model model) {
		return "selection";
	}
}
