<#import "/spring.ftl" as spring />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">

<head>
	<#include "includes/head.ftl" />
</head>
<body>

	<div data-role="page" data-url="<@spring.url '/persons/${person.id?c}' />">
		<div data-role="header">
			<h3>${person.displayName}</h3>
			<#if (lastListView.path)??>
			<a data-role="button" data-ajax="false" data-transition="slide" data-direction="reverse" href="<@spring.url '/${lastListView.path}' />" >
                ${lastListView.pathName}
            </a>
            </#if>
		</div>
		<div data-role="content" style="padding: 15px">
		
			<#if person.email1??>
				<a data-role="button" data-theme="c" href="mailto:${person.displayName} <${person.email1}>">${person.email1}</a>
			</#if>

			<#if person.phone1??>
				<a data-role="button" data-theme="c" href="tel:${person.phone1}">${person.phone1}</a>
			</#if>
			<#if person.phone2??>
				<a data-role="button" data-theme="c" href="tel:${person.phone2}">${person.phone2}</a>
			</#if>			

			<div data-role="collapsible" data-theme="b" data-collapsed="false">	
				<h3>Notes (${person.notes?size}) </h3>
				
				<#list person.notes as note>
					<#if (note_index > 0)>
					<div data-role="collapsible" data-collapsed="true">
					<#else>
					<div data-role="collapsible" data-collapsed="false">
					</#if>
						<h4>${note.lastModifiedTime?date?string("MMMM d, yyyy")}</h4>
						<div>${note.body?replace("\n", "<br/>")}</div>
					</div>
				</#list>
				
				<div data-role="collapsible">
					<h3>Enter new note</h3>
					<form action="<@spring.url '/persons/save-note' />" method="post">
						<input type="hidden" name="id" id="id" value="${person.id?c}" />
						
						<!-- new note text -->
						<div data-role="fieldcontain">			
					        <textarea id="body" name="body"></textarea>		        
						</div>
	
						<!-- next call date -->
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
	
						<input id="button" data-theme="a" name="button" type="submit" value="Save" />
					</form>
				</div>		
			</div>
	      	
			<div data-role="collapsible" data-theme="b" >
   				<h3>Details</h3>
   				<div class="ui-grid-a">

		            <div class="ui-block-a">
		            	Name
		            </div>
		            <div class="ui-block-b">
		            	${person.displayName}
		            </div>
		            
		            <#if person.jobTitle??>
		            <div class="ui-block-a">
		            	Job title
		            </div>
		            <div class="ui-block-b">
		            	${person.jobTitle}
		            </div>
		            </#if>
		            
		            <#if person.company??>
		            <div class="ui-block-a">
		            	Company
		            </div>
		            <div class="ui-block-b">
		            	${person.company}
		            </div>
		            </#if>
   				
   					<!-- Emails -->
		            <div class="ui-block-a">
		            	Primary email
		            </div>
		            <div class="ui-block-b">
		            	${person.email1!}
		            </div>
		            
		            <#if person.email2??>
		            <div class="ui-block-a">
		            	Second email
		            </div>		            
		            <div class="ui-block-b">
		            	${person.email2}
		            </div>
		            </#if>
		            
		            <#if person.email3??>
		            <div class="ui-block-a">
		            	Third email
		            </div>
		            <div class="ui-block-b">
		            	${person.email3!}
		            </div>
		            </#if>
		            
		            <!-- Phones -->
		            <div class="ui-block-a">
		            	Primary phone
		            </div>
		            <div class="ui-block-b">
		            	${person.phone1!}
		            </div>
		            
		            <#if person.phone2??>
		            <div class="ui-block-a">
		            	Second phone
		            </div>
		            <div class="ui-block-b">
		            	${person.phone2}
		            </div>
		            </#if>
		            		            
		            <#if person.phone3??>
		            <div class="ui-block-a">
		            	Third phone
		            </div>
		            <div class="ui-block-b">
		            	${person.phone3}
		            </div>
		            </#if>

		            <#if person.phone4??>
		            <div class="ui-block-a">
		            	Fourth phone
		            </div>
		            <div class="ui-block-b">
		            	${person.phone4}
		            </div>
		            </#if>
		            
		            <!-- Fax -->
		            <#if person.fax1??>
		            <div class="ui-block-a">
		            	First fax
		            </div>
		            <div class="ui-block-b">
		            	${person.fax1}
		            </div>
		            </#if>

		            <#if person.fax2??>
		            <div class="ui-block-a">
		            	Second fax
		            </div>
		            <div class="ui-block-b">
		            	${person.fax2}
		            </div>
		            </#if>

		            <#if person.fax3??>
		            <div class="ui-block-a">
		            	Third fax
		            </div>
		            <div class="ui-block-b">
		            	${person.fax3}
		            </div>
		            </#if>

					<!-- More personal details -->
		            <#if person.nickname??>
		            <div class="ui-block-a">
		            	nickname
		            </div>
		            <div class="ui-block-b">
		            	${person.nickname}
		            </div>
		            </#if>

		            <#if person.shortName??>
		            <div class="ui-block-a">
		            	shortName
		            </div>
		            <div class="ui-block-b">
		            	${person.shortName}
		            </div>
		            </#if>

		            <#if person.maidenName??>
		            <div class="ui-block-a">
		            	maidenName
		            </div>
		            <div class="ui-block-b">
		            	${person.maidenName}
		            </div>
		            </#if>

		            <#if person.gender??>
		            <div class="ui-block-a">
		            	gender
		            </div>
		            <div class="ui-block-b">
		            	${person.gender}
		            </div>
		            </#if>

		            <#if person.birthday??>
		            <div class="ui-block-a">
		            	birthday
		            </div>
		            <div class="ui-block-b">
		            	${person.birthday}
		            </div>
		            </#if>

		            <#if person.occupation??>
		            <div class="ui-block-a">
		            	occupation
		            </div>
		            <div class="ui-block-b">
		            	${person.occupation}
		            </div>
		            </#if>

		            <#if person.hobby??>
		            <div class="ui-block-a">
		            	hobby
		            </div>
		            <div class="ui-block-b">
		            	${person.hobby}
		            </div>
		            </#if>

					<!-- misc metadata -->
		            <#if person.sensitivity??>
		            <div class="ui-block-a">
		            	sensitivity
		            </div>
		            <div class="ui-block-b">
		            	${person.sensitivity}
		            </div>
		            </#if>

		            <#if person.priority??>
		            <div class="ui-block-a">
		            	priority
		            </div>
		            <div class="ui-block-b">
		            	${person.priority}
		            </div>
		            </#if>

		            <#if person.subject??>
		            <div class="ui-block-a">
		            	subject
		            </div>
		            <div class="ui-block-b">
		            	${person.subject}
		            </div>
		            </#if>

		            <#if person.tags??>
		            <div class="ui-block-a">
		            	tags
		            </div>
		            <div class="ui-block-b">
		            	${person.tags}
		            </div>
		            </#if>
		            
					<#if person.website??>
		            <div class="ui-block-a">
		            	website
		            </div>
		            <div class="ui-block-b">
		            	${person.website}
		            </div>
		            </#if>
		            
					<#if person.type??>
		            <div class="ui-block-a">
		            	Contact type
		            </div>
		            <div class="ui-block-b">
		            	${person.type}
		            </div>
		            </#if>

					<#if person.spouse??>
		            <div class="ui-block-a">
		            	Spouse
		            </div>
		            <div class="ui-block-b">
		            	${person.spouse}
		            </div>
		            </#if>

					<#if person.kidNames??>
		            <div class="ui-block-a">
		            	Kid Names
		            </div>
		            <div class="ui-block-b">
		            	${person.kidNames}
		            </div>
		            </#if>

					<#if person.linkedInProfile??>
		            <div class="ui-block-a">
		            	LinkedIn Profile
		            </div>
		            <div class="ui-block-b">
		            	${person.linkedInProfile}
		            </div>
		            </#if>

					<#if person.twitterHandle??>
		            <div class="ui-block-a">
		            	Twitter Handle
		            </div>
		            <div class="ui-block-b">
		            	${person.twitterHandle}
		            </div>
		            </#if>

					<#if person.comments??>
		            <div class="ui-block-a">
		            	Comments
		            </div>
		            <div class="ui-block-b">
		            	${person.comments}
		            </div>
		            </#if>
				</div>   					
			</div>
	      	
			<div data-role="navbar" data-iconpos="bottom">
				<ul>
	            <li>
	            	<#if lastListView.previous??>
		       		<a data-mini="true" data-role="button" data-inline="true" data-transition="slide" data-direction="reverse" href="<@spring.url '/persons/${lastListView.previous?c}' />">
		                Prev
		            </a>
		            <#else>
	            	<a data-mini="true" data-role="button" data-inline="true" data-transition="slide" data-direction="reverse" href="#">
	                	&nbsp;
	            	</a>
		            </#if>
	            </li>
	            <li>
		       	 	<a data-mini="true" data-role="button" data-inline="true" data-transition="fade" href="${person.id?c}/edit">
                		Edit
            		</a>
	            </li>
	            <li>
		            <#if lastListView.next??>
		            <a data-mini="true" data-role="button" dada-icon="arrow-r" data-inline="true" data-transition="slide" href="<@spring.url '/persons/${lastListView.next?c}' />">
		                Next
		            </a>
		            <#else>
		            <a data-mini="true" data-role="button" data-inline="true" data-transition="slide" data-direction="reverse" href="#">
	                	&nbsp;
	            	</a>
		            </#if>
	            </li>
	            </ul>
        	</div>
	            
		</div>

	</div>

</body>
</html>