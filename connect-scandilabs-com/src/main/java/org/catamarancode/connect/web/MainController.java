package org.catamarancode.connect.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.catamarancode.connect.entity.Person;
import org.catamarancode.connect.entity.User;
import org.catamarancode.connect.entity.support.Repository;
import org.catamarancode.connect.service.IdentifiableListing;
import org.catamarancode.connect.service.MessageContext;
import org.catamarancode.connect.service.UserContext;
import org.catamarancode.util.CollectionUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@Scope("request")
public class MainController {

    private Logger logger = LoggerFactory.getLogger(MainController.class);

    @Resource
    private Repository repository;
    
    @Autowired
	private IdentifiableListing personListing;
    
	@Autowired
	private UserContext userContext;
	
	@Autowired
	private MessageContext messageContext;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String slash(Map<String,Object> model, HttpServletRequest request) {
    	return index(model, request);
    }

	@RequestMapping(value = "/login", method = RequestMethod.GET)	
	public String logInGet(Map<String,Object> model) {
		messageContext.addPendingToModel(model);
		return "login";
	}
    
	@RequestMapping(value = "/login", method = RequestMethod.POST)	
	public String logInPost(HttpServletResponse response, Map<String,Object> model, @RequestParam("email") String email, @RequestParam("password") String password) {

		List<User> users = User.objects.filter(Restrictions.eq("email", email));
		// TODO: Use generics for this util method
		User user = (User) CollectionUtils.findOne(users);
		if (user == null) {
			MessageContext.addToModel(model, "Invalid email", false);
			return "login";
		}
		if (!user.passwordMatches(password)) {
			MessageContext.addToModel(model, "Invalid email or password", false);
			return "login";
		}
		
		// Success
		userContext.setUserId(user.getId());
		Cookie cookie = new Cookie(UserContext.USERID_COOKIE_NAME, String.valueOf(user.getId()));
		cookie.setMaxAge(2592000); // 30 days
		response.addCookie(cookie);
		
		return "redirect:/index";
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)	
	public String logOut(Map<String,Object> model) {

		userContext.setUserId(null);
		return "redirect:/login";
	}
    
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Map<String,Object> model, HttpServletRequest request) {
    	
    	if (!userContext.isLoggedIn(request)) {
    		return "redirect:/login";
    	}
    	userContext.prepareModel(model);
    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d");
    	
    	Set<Criterion> criteria = new HashSet<Criterion>();
    	criteria.add(Restrictions.isNotNull("nextCallDate"));
    	criteria.add(Restrictions.eq("deleted", false));
    	criteria.add(Restrictions.eq("user", userContext.getUser()));
    	
    	List<Order> orders = new ArrayList<Order>();
		orders.add(Order.asc("nextCallDate"));
		orders.add(Order.asc("lastName"));
		orders.add(Order.asc("firstName"));
		List<Person> list = Person.objects.filter(criteria, orders);
    	
    	logger.debug("Got people: " + list.size());
		// Save to HttpSession so that we can support previous/next navigation on view screens
		personListing.reset(list, "Home", "");
		
		// Group people by next call date
		List<String> dateStrList = new ArrayList<String>();
		Map<String, List<Person>> byDateMap = new HashMap<String, List<Person>>();
		for (Person person : list) {
			String dateStr = dateFormat.format(person.getNextCallDate());			
			if (!dateStrList.contains(dateStr)) {
				dateStrList.add(dateStr);
			}
			
			List<Person> personsForDateStr = byDateMap.get(dateStr);
			if (personsForDateStr == null) {
				personsForDateStr = new ArrayList<Person>();
				byDateMap.put(dateStr, personsForDateStr);
			}
			personsForDateStr.add(person);
		}
    	
    	model.put("persons", list);
    	model.put("personsByDate", byDateMap);
    	model.put("dateGroups", dateStrList);
    	
    	return "index";
    }

}
