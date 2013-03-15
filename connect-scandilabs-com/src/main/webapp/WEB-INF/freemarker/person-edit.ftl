<#import "/spring.ftl" as spring />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">

<head>
	<#include "includes/head.ftl" />
</head>
<body>

<div data-role="page" >
	<div data-role="header">
		<#if (person.id > 0)>
		<h3>Edit ${person.displayName}</h3>
		<a data-role="button" data-transition="slide" data-direction="reverse" href="<@spring.url '/persons/${person.id?c}' />" >
            Back
        </a>
		<#else>
		<h3>New Contact</h3>
		<a data-role="button" data-ajax="false" data-transition="slide" data-direction="reverse" href="<@spring.url '/' />">
            Cancel
        </a>		
		</#if>
	</div>
	
	<div data-role="content" style="padding: 15px">	
		
		<#if person??>
			<@spring.bind "person" />
		</#if>		
		<#if spring.status??>	
			<#-- see http://code.google.com/p/itime-all/source/browse/trunk/inspector-time/src/main/webapp/WEB-INF/freemarker/www/inspector-time-macros.ftl -->
			<#-- see http://static.springsource.org/spring/docs/3.1.x/javadoc-api/org/springframework/web/servlet/support/BindStatus.html -->
			<#-- see http://static.springsource.org/spring/docs/3.1.x/javadoc-api/org/springframework/validation/Errors.html -->
			<div ui-shadow="b" class="errorBox">
			<#list spring.status.errorMessages as error> 
				<strong><em>${error}</em></strong> <br>
			</#list>
			<#list spring.status.errors.fieldErrors as fieldError>
		            <strong><em>Field ${fieldError.field}: <@spring.message fieldError /></em></strong><br/>
		        </#list>
		    <#list spring.status.errors.globalErrors as error>
		    	<strong><em><@spring.message error /></em></strong><br/>
		    </#list>
		    </div>
		</#if>		
		
		<!-- for help: http://mobile.tutsplus.com/tutorials/mobile-web-apps/jquery-mobile-forms/ -->
		<form action="<@spring.url '/persons/save' />" method="post">
			<#if person.id??>
				<@spring.formHiddenInput "person.id" />
			</#if>
			<#if (person.user)??>
				<input type="hidden" name="personUserId" id="personUserId" value="${person.user.id?c}" />
			</#if>
			
			<div data-role="fieldcontain">	
		        <label for="firstName">First Name</label>
		        <@spring.formInput "person.firstName"/>
			</div>
			<div data-role="fieldcontain">			
		        <label for="lastName">Last Name</label>		        
		        <@spring.formInput "person.lastName"/>
			</div>
			
			<div data-role="fieldcontain">
		        <label for="email1">Email 1</label>
		        <@spring.formInput "person.email1"/>
			</div>
						
			<div data-role="fieldcontain">
		        <label for="phone1">Phone 1</label>
		        <@spring.formInput "person.phone1"/>
			</div>

			<div data-role="fieldcontain">
		        <label for="jobTitle">Job Title</label>
		        <@spring.formInput "person.jobTitle"/>
			</div>
			
			<div data-role="fieldcontain">
		        <label for="jobTitle">Company</label>
		        <@spring.formInput "person.company"/>
			</div>			
					
			<div data-role="collapsible">
   				<h3>Additional details</h3>
   				
				<div data-role="fieldcontain">			
			        <label for="middleName">Middle Name</label>
			        <@spring.formInput "person.middleName"/>
				</div>
   				
				<div data-role="fieldcontain">
			        <label for="phone2">Phone 2</label>
			        <@spring.formInput "person.phone2"/>
				</div>
				<div data-role="fieldcontain">
			        <label for="phone3">Phone 3</label>
			        <@spring.formInput "person.phone3"/>
				</div>
				<div data-role="fieldcontain">
			        <label for="phone4">Phone 4</label>
			        <@spring.formInput "person.phone4"/>
				</div>
				
				<div data-role="fieldcontain">
			        <label for="email2">Email 2</label>
			        <@spring.formInput "person.email2"/>
				</div>
				<div data-role="fieldcontain">
			        <label for="email3">Email 3</label>
			        <@spring.formInput "person.email3"/>
				</div>
				
				<div data-role="fieldcontain">
			        <label for="fax1">Fax 1</label>
			        <@spring.formInput "person.fax1"/>
				</div>
				<div data-role="fieldcontain">
			        <label for="fax2">Fax 2</label>
			        <@spring.formInput "person.fax2"/>
				</div>
				<div data-role="fieldcontain">
			        <label for="fax3">Fax 3</label>
			        <@spring.formInput "person.fax3"/>
				</div>
	
				<div data-role="fieldcontain">
			        <label for="maidenName">Maiden Name</label>
			        <@spring.formInput "person.maidenName"/>
				</div>
				
				<div data-role="fieldcontain">
			        <label for="website">Web Site</label>
			        <@spring.formInput "person.website"/>
				</div>			
	
				<div data-role="fieldcontain">
			        <label for="spouse">Spouse</label>
			        <@spring.formInput "person.spouse"/>
				</div>			
	
				<div data-role="fieldcontain">
			        <label for="kidNames">Kid Names</label>
			        <@spring.formInput "person.kidNames"/>
				</div>	
				
				<div data-role="fieldcontain">
			        <label for="linkedInProfile">LinkedIn Profile</label>
			        <@spring.formInput "person.linkedInProfile"/>
				</div>	
				
				<div data-role="fieldcontain">
			        <label for="twitterHandle">Twitter Handle</label>
			        <@spring.formInput "person.twitterHandle"/>
				</div>	
				
				<div data-role="fieldcontain">
			        <label for="comments">Comments</label>
			        <@spring.formTextarea "person.comments"/>
				</div>	
				
			</div>	
			
			<!-- next call date -->
			<input type="hidden" name="existingNextCallDate" id="existingNextCallDate" value="${(person.nextCallDate?date?string("MMM d, yyyy"))!}" />
			
			<@spring.bind "person.nextCallDate" />
			<fieldset data-role="controlgroup" >
				<#if person.nextCallDate??>
				<legend>Follow up on <em>${person.nextCallDate?datetime?string("MMMM d")}</em> or:</legend>
				<#else>
				<legend>Follow up <em>never</em> or in:</legend>
				</#if>

				<input type="radio" name="nextCallDate" id="radio-choice-0" value="Jan 1, 1970" />
		     	<label for="radio-choice-0">Never</label>
		     	
				<#assign alternative="Tomorrow"/>
		     	<input type="radio" name="nextCallDate" id="radio-choice-1" value="${dateAlternativeValues[alternative]?date}" />
		     	<label for="radio-choice-1">In a day</label>
		
				<#assign alternative="Next week"/>
		     	<input type="radio" name="nextCallDate" id="radio-choice-2" value="${dateAlternativeValues[alternative]?date}" />
		     	<label for="radio-choice-2">In a week</label>

				<#assign alternative="In one month"/>
		     	<input type="radio" name="nextCallDate" id="radio-choice-3" value="${dateAlternativeValues[alternative]?date}" />
		     	<label for="radio-choice-3">In a month</label>

				<#assign alternative="In three months"/>
		     	<input type="radio" name="nextCallDate" id="radio-choice-4" value="${dateAlternativeValues[alternative]?date}" />
		     	<label for="radio-choice-4">In three months</label>					
			</fieldset>					

			<div>
				<label for="button"></label>
				<input type="submit" value="Save" />
			</div>	
			
			<#if (person.id > 0)>
			<div>			
	            <a data-role="button" data-rel="dialog" href="../${person.id?c}/delete-confirm">
                	Delete
            	</a>
            </div>
            </#if>
				
		</form>
	</div> <!-- content -->
		
</div> <!-- page -->
</body>
</html>