package org.drip.controller;

import org.drip.DripUtils;
import org.drip.model.Customer;
import org.drip.model.User;
import org.drip.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CustomerController {
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	@Qualifier("webUserValidator")
	Validator validator;
	
	@RequestMapping(value="/")
	public String root() {
		return "redirect:index";
	}
	
	@RequestMapping(value="/index")
	public String index() {
		return "index";
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String registerUser(Model model) {
		model.addAttribute("user", new WebUser());
		return "register";
	}
	
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public String saveUser(@ModelAttribute("user") WebUser webUser, BindingResult result, Model model) {
		validator.validate(webUser, result);
		if (result.hasErrors()) {
			return "register";
		} else {
			Customer customer = customerService.getCustomer(webUser.getEmail());
			String encodedPassword = DripUtils.encryptPassword(webUser.getPassword());
			User user = new User(webUser.getEmail(),encodedPassword);
			customer.setUser(user);
			customerService.saveCustomer(customer);
			model.addAttribute("success", "User details saved!");
			return "redirect:login";
		}		
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login() {
		return "login";
	}
}
