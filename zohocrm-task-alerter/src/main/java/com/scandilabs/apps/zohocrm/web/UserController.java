package com.scandilabs.apps.zohocrm.web;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scandilabs.apps.zohocrm.service.PageMessageContext;
import com.scandilabs.apps.zohocrm.service.Row;
import com.scandilabs.apps.zohocrm.service.UserContext;
import com.scandilabs.apps.zohocrm.service.ZohoCrmApiService;


@Controller
@Scope("request")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private ZohoCrmApiService zohoCrmApiService;
    
    @Autowired
	private UserContext userContext;        

    @Autowired
	private PageMessageContext pageMessageContext;   
    
	@RequestMapping(value = "/contacts/view/{contactKey}", method = RequestMethod.GET)	
	public String contactView(Map<String,Object> model, @PathVariable("contactKey") String contactKey) {

		if (!userContext.isLoggedIn()) {
    		return "redirect:/signin";
    	}

		Row row = this.zohoCrmApiService.loadContactFields(contactKey);
		for (String fieldName : row.getNoSpaceFieldMap().keySet()) {
			model.put(fieldName, row.getNoSpaceFieldMap().get(fieldName));
		}
		
		pageMessageContext.addPendingToModel(model);
		userContext.prepareModel(model);		
        return "contact-view";		
	}
	
	@RequestMapping(value = "/contacts/postpone/{contactKey}", method = RequestMethod.GET)	
	public String contactPostpone(Map<String,Object> model, @PathVariable("contactKey") String contactKey) {

		if (!userContext.isLoggedIn()) {
    		return "redirect:/signin";
    	}

		this.zohoCrmApiService.postponeContactNextCallDate(contactKey);
		
		pageMessageContext.addPendingToModel(model);
		userContext.prepareModel(model);		
        return "redirect:/contacts/view/" + contactKey;		
	}	
	
	@RequestMapping(value = "/contacts/list", method = RequestMethod.GET)	
	public String contactsList(Map<String,Object> model) {
		pageMessageContext.addPendingToModel(model);
		if (!userContext.isLoggedIn()) {
    		return "redirect:/signin";
    	}
		userContext.prepareModel(model);
		
		List<JSONObject> list = this.zohoCrmApiService.listContactsWithNextCallDate();		
		model.put("contacts", list);		
        return "contacts-list";		
	}	
 
}
