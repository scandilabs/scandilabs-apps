<#import "/spring.ftl" as spring />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
	
<head>
	<#include "includes/head.ftl" />
</head>
<body>
<div id="bodyContent">
	
	<div data-role="page">
		<div data-role="header">
			<h3>Everyone</h3>
			<a data-role="button" data-ajax="false" data-transition="slide" data-direction="reverse" href="<@spring.url '/' />">
                Home
            </a>
		</div>
		<div data-role="content" style="padding: 15px">
		
			<ul data-role="listview" data-inset="true" data-filter="true">
			<#list firstLetterList as firstLetter> 
		       <li data-role="list-divider" role="heading">
                    ${firstLetter}
                </li>
				<#list personsByFirstLetter[firstLetter] as person>
					<li>
						<a href="<@spring.url '/persons/${person.id?c}' />"  data-transition="slide">
							${person.displayName!"N/A"}
						</a>
					</li>
				</#list>                    
            </#list>
			</ul>
		
			<!-- Bottom nav bar --> 
			
			
			<div data-role="navbar" data-iconpos="top">
                <ul>
                    <li>
                        <a href="<@spring.url '/' />" data-ajax="false" data-theme="" data-icon="home">
                            Home
                        </a>
                    </li>
                    <li>
                        <a href="<@spring.url '/persons' />" data-theme="" data-icon="grid" class="ui-btn-active">
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
	
</div> <!-- bodyContent -->
</body>
</html>