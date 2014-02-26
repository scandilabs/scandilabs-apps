package com.scandilabs.apps.zohocrm.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.scandilabs.apps.zohocrm.entity.User;
import com.scandilabs.apps.zohocrm.entity.support.Repository;
import com.scandilabs.apps.zohocrm.service.EmailComposer;
import com.scandilabs.apps.zohocrm.service.PageMessageContext;
import com.scandilabs.apps.zohocrm.service.RecordType;
import com.scandilabs.apps.zohocrm.service.Row;
import com.scandilabs.apps.zohocrm.service.UserContext;
import com.scandilabs.apps.zohocrm.service.ZohoCrmApiService;
import com.scandilabs.apps.zohocrm.service.ZohoHttpCaller;
import com.scandilabs.apps.zohocrm.util.ApplicationUtils;


@Controller
@Scope("request")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private ZohoCrmApiService zohoCrmApiService;
    
    @Autowired
	private UserContext userContext;
    
    @Autowired
	private EmailComposer emailComposer;

    @Autowired
	private Repository repository;

    @Autowired
	private PageMessageContext pageMessageContext;   
    
	@RequestMapping(value = "/contacts/view/{contactKey}", method = RequestMethod.GET)	
	public String contactView(Map<String,Object> model, @RequestParam(value="user", required=false) String userApiToken, @PathVariable("contactKey") String contactKey) {

		if (!userContext.isLoggedIn(repository, userApiToken)) {
    		return "redirect:/signin";
    	}

		Row row = this.zohoCrmApiService.loadContactFields(userContext.getUser(repository).getZohoAuthToken(), contactKey);
		for (String fieldName : row.getNoSpaceFieldMap().keySet()) {
			model.put(fieldName, row.getNoSpaceFieldMap().get(fieldName));
		}
		
		pageMessageContext.addPendingToModel(model);
		userContext.prepareModel(repository, model);		
        return "contact-view";		
	}
	
	@RequestMapping(value = "/signin", method = RequestMethod.GET)	
	public String signinGet(Map<String,Object> model) {
		pageMessageContext.addPendingToModel(model);
		userContext.prepareModel(repository, model);		
        return "signin";
	}	
	
	@RequestMapping(value = "/signin", method = RequestMethod.POST)	
	public String signinPost(Map<String,Object> model, @RequestParam(value="email", required=true) String email, @RequestParam(value="password", required=true) String password) {
		
		User user = this.repository.loadUserByEmail(email);
		userContext.setUserKey(user.getKey());
		// TODO: Check password some day
		
		if (user != null) {
			return "redirect:/contacts/list";
		}
		
		pageMessageContext.setMessage("Invalid email or password", false);				
		return "redirect:/signin";
	}	
	
	@RequestMapping(value = "/contacts/postpone/{contactKey}", method = RequestMethod.GET)	
	public String contactPostpone(Map<String,Object> model, @RequestParam(value="user", required=false) String userApiToken, @PathVariable("contactKey") String contactKey) {

		if (!userContext.isLoggedIn(repository, userApiToken)) {
    		return "redirect:/signin";
    	}

		this.zohoCrmApiService.postponeContactNextCallDate(userContext.getUser(repository).getZohoAuthToken(), contactKey);
        return "redirect:/contacts/view/" + contactKey;		
	}		
	
	@RequestMapping(value = "/contacts/note-add", method = RequestMethod.POST)	
	public String contactNoteAdd(Map<String,Object> model, @RequestParam(value="user", required=false) String userApiToken, HttpServletRequest request) {

		if (!userContext.isLoggedIn(repository, userApiToken)) {
    		return "redirect:/signin";
    	}

		String recordId = request.getParameter("recordId");
		String note = request.getParameter("note");
		if (note.equals("Enter a new note")) {
			logger.debug("Skipping note add, nothing was entered.");
		} else {
			logger.debug("Adding note to contact " + recordId + ": " + note);
			this.zohoCrmApiService.addContactNote(userContext.getUser(repository).getZohoAuthToken(), recordId, note);
			pageMessageContext.setMessage("Note was saved.", true);
		}		
        return "redirect:/contacts/view/" + recordId;		
	}	
	
	@RequestMapping(value = "/contacts/cancel/{contactKey}", method = RequestMethod.GET)	
	public String contactCancel(Map<String,Object> model, @RequestParam(value="user", required=false) String userApiToken, @PathVariable("contactKey") String contactKey) {

		if (!userContext.isLoggedIn(repository, userApiToken)) {
    		return "redirect:/signin";
    	}

		this.zohoCrmApiService.cancelContactNextCallDate(userContext.getUser(repository).getZohoAuthToken(), contactKey);
		
        return "redirect:/contacts/view/" + contactKey;		
	}	
	
	@RequestMapping(value = "/contacts/list", method = RequestMethod.GET)	
	public String contactsList(Map<String,Object> model, @RequestParam(value="user", required=false) String userApiToken) {
		pageMessageContext.addPendingToModel(model);
		if (!userContext.isLoggedIn(repository, userApiToken)) {
    		return "redirect:/signin";
    	}
		userContext.prepareModel(repository, model);
		
		List<JSONObject> list = this.zohoCrmApiService.listContactsWithNextCallDate(userContext.getUser(repository).getZohoAuthToken());
		List<Row> rows = ApplicationUtils.toRowList(list);
		List<Map<String, Object>> rowMap = ApplicationUtils.toRowFieldMap(rows);
		model.put("contacts", rowMap);		
        return "contacts-list";		
	}	
	
	@RequestMapping(value = "/contacts/history", method = RequestMethod.GET)	
	public String contactsHistory(Map<String,Object> model, @RequestParam(value="user", required=false) String userApiToken) {
		pageMessageContext.addPendingToModel(model);
		if (!userContext.isLoggedIn(repository, userApiToken)) {
    		return "redirect:/signin";
    	}
		userContext.prepareModel(repository, model);
		
		User user = userContext.getUser(repository);
		List<JSONObject> contacts = zohoCrmApiService.listContactsWithNextCallDate(user.getZohoAuthToken());
		String historyBodyHtml = emailComposer.composeContactsHistoryBodyHtml(user, contacts);
		model.put("historyBodyHtml", historyBodyHtml);		
        return "contacts-history";		
	}	
 
}
