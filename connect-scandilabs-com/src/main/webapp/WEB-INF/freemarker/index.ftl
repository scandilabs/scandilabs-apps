<#import "/spring.ftl" as spring />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">

<head>
	<#include "includes/head.ftl" />
</head>
<body>
	
        <div data-role="page" data-url="<@spring.url '/index' />" id="page1">
		        
			<div data-role="header">				
				<h3>Home</h3>
				<a data-role="button" class="ui-btn-right" data-transition="fade" href="<@spring.url '/logout' />" >
		            Log out ${user.firstName}
		        </a>
			</div>
	        
            <div data-role="content" style="padding: 15px">
            	
            	<#if !(dateGroups?has_content)>
            		No contacts, <a href="<@spring.url '/persons/create' />">add a new one</a> now!
            	</#if>
            	
                <ul data-role="listview" data-inset="true" data-filter="true">
				<#list dateGroups as dateGroup> 
			       <li data-role="list-divider" role="heading">
	                    ${dateGroup}
	                </li>
					<#list personsByDate[dateGroup] as person>
						<li>
							<a href="<@spring.url '/persons/${person.id?c}' />"  data-transition="slide">
								${person.displayName!"N/A"}
							</a>
						</li>
					</#list>                    
	            </#list>
				</ul>                	
                	
                <div data-role="navbar" data-iconpos="top">
                    <ul>
                        <li>
                            <a href="<@spring.url '/' />" data-theme="" data-icon="home" class="ui-btn-active">
                                Home
                            </a>
                        </li>
                        <li>
                            <a href="<@spring.url '/persons' />" data-theme="" data-icon="grid">
                                Everyone
                            </a>
                        </li>
                        <li>
                            <a href="<@spring.url '/persons/create' />" data-theme="" data-icon="plus">
                                New
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
	
</body>
</html>