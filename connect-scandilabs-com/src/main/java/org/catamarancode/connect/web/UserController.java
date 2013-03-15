package org.catamarancode.connect.web;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.catamarancode.connect.entity.User;
import org.catamarancode.connect.entity.support.Repository;
import org.catamarancode.connect.service.MessageContext;
import org.catamarancode.connect.service.UserContext;
import org.catamarancode.spring.mvc.DisplayMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Scope("request")
@RequestMapping("/users")
public class UserController {

	private Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserContext userContext;
	
	@Autowired
	private MessageContext messageContext;
	
	@Resource
	private Repository repository;

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create(Map<String, Object> model) {
		User user = new User();
		model.put("user", user);
		
		return "user-edit";
	}
	

	@RequestMapping(value = "/logged-in/edit", method = RequestMethod.GET)
	public String edit(@PathVariable long userId,
			Map<String, Object> model) {
		User user = User.objects.load(userContext.getUserId());
		if (user == null) {
			throw new RuntimeException("User not logged in");
		}
		model.put("user", user);
		
		return "user-edit";
	}

	/**
	 * Validation: @see
	 * http://static.springsource.org/spring/docs/3.1.x/spring-framework
	 * -reference/html/validation.html#validation-beanvalidation
	 * 
	 * @param person
	 * @param errors
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@ModelAttribute("user") @Valid User user,
			BindingResult errors, Map<String, Object> model) {

		if (errors.hasErrors()) {
			return "user-edit";
		}
		
		Date now = new Date();
		user.setLastModifiedTime(now);
		messageContext.setMessage("Updated user", true);
		boolean update = true;
		if (user.getId() == null || user.getId().longValue() == 0) {
			// create operation
			user.setCreatedTime(now);			
			messageContext.setMessage("Created user", true);
			update = false;
		}
		
		long id = user.save();
		
		// If registration, log user in
		if (!update) {
			userContext.setUserId(id);
		}
		return "redirect:/index";
	}

	/**
	 * The StringTrimmerEditor is required in order to convert empty parameter
	 * inputs into null. And that is, in turn, required for the JSR-303
	 * validation framework to use @Pattern and other validator annotations
	 * while still allowing blank input values
	 * 
	 * In Spring Framework 3.2 there will be a better way to configure this globally, see https://jira.springsource.org/browse/SPR-9112
	 * @see 
	 * 
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		//binder.registerCustomEditor(Date.class, new DatePropertyEditor("MMM d, yyyy"));		
	}

}
