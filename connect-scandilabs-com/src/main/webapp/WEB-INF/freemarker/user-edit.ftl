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
		<#if (user.id > 0)>
		<h3>Edit user ${user.displayName}</h3>
		<a data-role="button" data-transition="slide" data-direction="reverse" href="<@spring.url '/users/${user.id?c}' />" >
            Back
        </a>
		<#else>
		<h3>Register</h3>
		</#if>
	</div>
	
	<div data-role="content" style="padding: 15px">	
		
		<#if spring.status??>	
			<!-- see http://code.google.com/p/itime-all/source/browse/trunk/inspector-time/src/main/webapp/WEB-INF/freemarker/www/inspector-time-macros.ftl -->
			<!-- see http://static.springsource.org/spring/docs/3.1.x/javadoc-api/org/springframework/web/servlet/support/BindStatus.html -->
			<!-- see http://static.springsource.org/spring/docs/3.1.x/javadoc-api/org/springframework/validation/Errors.html -->
			<p>errors</p>
			<#-- list spring.status.errorMessages as error> <b>${error}</b> <br> </#list -->
			<#if user??>
				<@spring.bind "user" />
			</#if>		
			<#list spring.status.errors.fieldErrors as fieldError>
		            Field ${fieldError.field}: <@spring.message fieldError /><br/>
		        </#list>
		        <#list spring.status.errors.globalErrors as error>
		            <@spring.message error /><br/>
		        </#list>
		</#if>		
		
		<!-- for help: http://mobile.tutsplus.com/tutorials/mobile-web-apps/jquery-mobile-forms/ -->
		<form action="<@spring.url '/users/save' />" method="post">
			<#if user.id??>
				<@spring.formHiddenInput "user.id" />
			</#if>
			
			<div data-role="fieldcontain">	
		        <label for="firstName">First Name</label>
		        <@spring.formInput "user.firstName"/>
			</div>
			<div data-role="fieldcontain">			
		        <label for="lastName">Last Name</label>		        
		        <@spring.formInput "user.lastName"/>
			</div>
			
			<div data-role="fieldcontain">
		        <label for="email">Email</label>
		        <@spring.formInput "user.email"/>
			</div>
			
			<div data-role="fieldcontain">
		        <label for="cleartextPassword">Password</label>
		        <@spring.formPasswordInput  "user.cleartextPassword"/>
			</div>			
						
			<div>
				<label for="button"></label>
				<input type="submit" value="Save" />
			</div>	
			
		</form>
	</div> <!-- content -->
		
</div> <!-- page -->
</body>
</html>