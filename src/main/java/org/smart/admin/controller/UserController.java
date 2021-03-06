package org.smart.admin.controller;

import java.util.Locale;

import org.smart.admin.common.validator.UserValidator;
import org.smart.admin.model.entity.User;
import org.smart.admin.service.SecurityService;
import org.smart.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private MessageSource messageSource;

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("userForm", new User());
		return "signup";
	}

	@PostMapping("/signup")
	public String signup(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
		userValidator.validate(userForm, bindingResult);
		if (bindingResult.hasErrors()) {
			return "signup";
		}
		userService.save(userForm);
		securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());
		return "redirect:/";
	}

	@GetMapping("/signin")
	public String login(Model model, String error, String logout, Locale locale) {
		Object[] args = new Object[] {};
		if (error != null) {
			String errorMessageCode = error.equals("oauth") ? "common.login.failure"
					: "common.login.invalid.credentials";
			String loginFailureMessage = messageSource.getMessage(errorMessageCode, args, locale);
			model.addAttribute("error", loginFailureMessage);
		}
		if (logout != null) {
			String logoutSuccessMessage = messageSource.getMessage("common.logout.success", args, locale);
			model.addAttribute("message", logoutSuccessMessage);
		}
		return "signin";
	}

	@GetMapping("/forgot-password")
	public String forgotPassword(Model model) {
		return "forgot-password";
	}

}
