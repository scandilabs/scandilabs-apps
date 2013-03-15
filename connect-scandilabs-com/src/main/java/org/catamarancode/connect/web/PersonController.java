package org.catamarancode.connect.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.RandomStringUtils;
import org.catamarancode.connect.entity.Note;
import org.catamarancode.connect.entity.Person;
import org.catamarancode.connect.entity.User;
import org.catamarancode.connect.entity.support.Repository;
import org.catamarancode.connect.service.IdentifiableListing;
import org.catamarancode.connect.service.UserContext;
import org.catamarancode.connect.web.support.DatePropertyEditor;
import org.catamarancode.spring.mvc.DisplayMessage;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Scope("request")
@RequestMapping("/persons")
public class PersonController {

	private Logger logger = LoggerFactory.getLogger(PersonController.class);

	/**
	 * TODO: Find out if @Resource will also work
	 */
	@Autowired
	private IdentifiableListing personListing;
	
	@Autowired
	private UserContext userContext;	
	
	@Resource
	private Repository repository;

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create(Map<String, Object> model) {
		Person person = new Person();
		model.put("person", person);
		
		// Generate a list of alternative next call dates for buttons
		ListOrderedMap dates = this.upcomingDates();
		model.put("dateAlternativeValues", dates);
		model.put("dateAlternativeKeys", dates.asList());
		
		return "person-edit";
	}
	
	/**
	 * Generate a list of alternative next call dates for buttons/drop-downs
	 * @return
	 */
	private ListOrderedMap upcomingDates() {
		
		ListOrderedMap dates = new ListOrderedMap();
		Calendar cal = new GregorianCalendar();		
		resetTime(cal);
		
		cal.add(Calendar.DATE, 1);		
		dates.put("Tomorrow", cal.getTime());
		
		cal.add(Calendar.DATE, -1);
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		dates.put("Next week", cal.getTime());
		
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		dates.put("In two weeks", cal.getTime());
		
		resetTime(cal);
		cal.add(Calendar.MONTH, 1);
		dates.put("In one month", cal.getTime());
		
		cal.add(Calendar.MONTH, 2);
		dates.put("In three months", cal.getTime());
		
		cal.add(Calendar.MONTH, 3);
		dates.put("In six months", cal.getTime());

		return dates;
	}

	@RequestMapping(value = "/{personId}", method = RequestMethod.GET)
	public String view(@PathVariable long personId,
			Map<String, Object> model) {
		Person person = (Person) Person.objects.load(personId);
		model.put("person", person);
		
		// Prev/next
		personListing.setCurrent(personId);
		model.put("lastListView", personListing);
		
		// Generate a list of alternative next call dates for buttons
		ListOrderedMap dates = this.upcomingDates();
		model.put("dateAlternativeValues", dates);
		model.put("dateAlternativeKeys", dates.asList());
		
		return "person-view";
	}
	
	private void resetTime(Calendar cal) {
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.HOUR_OF_DAY, 6);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	@RequestMapping(value = "/{personId}/edit", method = RequestMethod.GET)
	public String edit(@PathVariable long personId,
			Map<String, Object> model) {
		Person person = (Person) Person.objects.load(personId);
		model.put("person", person);
		
		// Generate a list of alternative next call dates for buttons
		ListOrderedMap dates = this.upcomingDates();
		model.put("dateAlternativeValues", dates);
		model.put("dateAlternativeKeys", dates.asList());
		
		return "person-edit";
	}
	
	@RequestMapping(value = "/{personId}/delete", method = RequestMethod.GET)
	public String delete(@PathVariable long personId,
			Map<String, Object> model) {
		Person person = (Person) Person.objects.load(personId);
		person.setDeleted(true);
		person.save();
		Long nextId = this.personListing.getNext();
		this.personListing.remove(personId);
		if (nextId == null) {
			nextId = this.personListing.getFirst();
			if (nextId == null) {
				this.personListing.setCurrent(null);
				return "redirect:/";
			}
		}	
		
		return "redirect:/persons/" + nextId.toString();
	}
	
	@RequestMapping(value = "/{personId}/delete-confirm", method = RequestMethod.GET)
	public String deleteConfirm(@PathVariable long personId,
			Map<String, Object> model) {
		Person person = (Person) Person.objects.load(personId);
		model.put("person", person);
		return "person-delete-confirm";
	}	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String root(Map<String, Object> model) {
		return all(model);
	}
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String slash(Map<String, Object> model) {
		return all(model);
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public String all(Map<String, Object> model) {

		Set<Criterion> criteria = new HashSet<Criterion>();
    	criteria.add(Restrictions.eq("deleted", false));
    	criteria.add(Restrictions.eq("user", userContext.getUser()));
    	
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.asc("lastName").ignoreCase());
		orders.add(Order.asc("firstName").ignoreCase());
		List<Person> list = Person.objects.filter(criteria, orders);
		logger.debug("Got people: " + list.size());
		
		// Save to HttpSession so that we can support previous/next navigation on view screens
		personListing.reset(list, "Everyone", "persons/all");
		
		// Put in a map indexed by first letter
		Map<String, List<Person>> byFirstLetter = new HashMap<String, List<Person>>();
		List<String> firstLetters = new ArrayList<String>();
		for (Person person : list) {
			if (person.getLastName() != null) {
				String firstLetter = person.getLastName().substring(0, 1).toUpperCase();
				
				if (!firstLetters.contains(firstLetter)) {
					firstLetters.add(firstLetter);
				}
				
				List<Person> personsForThisLetter = byFirstLetter.get(firstLetter);
				if (personsForThisLetter == null) {
					personsForThisLetter = new ArrayList<Person>();
					byFirstLetter.put(firstLetter, personsForThisLetter);
				}
				personsForThisLetter.add(person);
			}			
		}
		
		model.put("persons", list);
		model.put("personsByFirstLetter", byFirstLetter);
		model.put("firstLetterList", firstLetters);
		return "persons-all";
	}
	
	@RequestMapping(value = "/by-next-call-date", method = RequestMethod.GET)
	public String byNextCallDate(Map<String, Object> model) {

		Person person = new Person();
		// PersistableBase.setEntityServices(services)
		
		List<Person> list = Person.objects.order(Order.asc("nextCallDate"));
		logger.debug("Got people: " + list.size());
		model.put("persons", list);
		return "persons-all";
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
	public String save(@ModelAttribute("person") @Valid Person person, @RequestParam(value="personUserId", required=false) Long personUserId, @RequestParam(value="existingNextCallDate", required=false) Date existingNextCallDate,
			BindingResult errors, Map<String, Object> model) {

		if (errors.hasErrors()) {
			
			// Log them
			for (ObjectError error : errors.getAllErrors()) {
				logger.info("form error: " + error.getDefaultMessage());
			}

			// Generate a list of alternative next call dates for buttons
			ListOrderedMap dates = this.upcomingDates();
			model.put("dateAlternativeValues", dates);
			model.put("dateAlternativeKeys", dates.asList());			
			return "person-edit";
		}
		
		if (isNever(person.getNextCallDate())) {
			person.setNextCallDate(null);
		} else if (person.getNextCallDate() == null && existingNextCallDate != null) {
			
			// User did not change it
			person.setNextCallDate(existingNextCallDate);
		}
		
		if (person.getId() == null || person.getId().longValue() == 0) {
			
			// Create
			person.setUser(userContext.getUser());
		} else {
			
			// Update -- re-associate person object with a user because the spring form binding will have removed user
			User alreadyAssociatedUser = User.objects.load(personUserId);
			person.setUser(alreadyAssociatedUser);
		}
		
		long id = person.save();

		DisplayMessage.addToThisPage(model, "Saved with id " + id, true);
		return "redirect:" + id;
	}
	
	private boolean isNever(Date date) {
		if (date == null) {
			return false;
		}
	
		Calendar epochTestYear = new GregorianCalendar();
		epochTestYear.set(Calendar.YEAR, 1971);
		
		if (date.before(epochTestYear.getTime())) {
			return true;
		}
		
		return false;
	}

	@RequestMapping(value = "/save-note", method = RequestMethod.POST)	
	public String saveNote(@RequestParam("id") long personId, @RequestParam(value="nextCallDate", required=false) Date nextCallDate, @RequestParam("body") String noteBody) {
		
		Person person = Person.objects.load(personId);
		boolean changed = false;
		if (nextCallDate != null) {
			if (isNever(nextCallDate)) {
				person.setNextCallDate(null);
			} else {
				person.setNextCallDate(nextCallDate);
			}
			changed = true;
		}
		
		if (StringUtils.hasText(noteBody)) {
			Note note = new Note();
			note.setContact(person);
			note.setBody(noteBody);
			Date now = new Date();
			note.setCreatedTime(now);
			note.setLastModifiedTime(now);
			note.setSubject("Entered note");
			person.getNotes().add(note);
			changed = true;
		}
		
		if (changed) {
			person.save();
		}
		
		return "redirect:" + personId;
		//return "redirect:../index?rnd=" + RandomStringUtils.randomAlphanumeric(4);
	}

	@RequestMapping(value = "/set-call-date", method = RequestMethod.GET)	
	public String setCallDate(@RequestParam("id") long personId, @RequestParam("nextCallDate") Date nextCallDate) {
	// public String setCallDate(@RequestParam("id") long personId, @RequestParam("nextCallDate") @DateTimeFormat(iso=ISO.DATE) Date nextCallDate) {
		
		Person person = Person.objects.load(personId);
		person.setNextCallDate(nextCallDate);
		person.save();
		
		
		return "redirect:../index?rnd=" + RandomStringUtils.randomAlphanumeric(4);
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
		binder.registerCustomEditor(Date.class, new DatePropertyEditor("MMM d, yyyy"));		
	}

}
