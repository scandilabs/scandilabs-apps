<#import "/spring.ftl" as spring />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">

<head>
	<#include "includes/head.ftl" />
</head>
<body>	
    <div data-role="page" id="page1">
    	
		<div data-role="header">
			<h3>Log in</h3>
		</div>        	
        <div data-role="content" style="padding: 15px">
        	
        	<#if message??>
        		<h3>${message}</h3>
        	</#if>
        	
        	<p>Want a better way to track contacts, leads and activity notes on your mobile?<br/>
        	  <a href="<@spring.url '/users/create' />">Register</a> for free today and try us out!</p>
        	
			<form action="<@spring.url '/login' />" method="post">
				<div data-role="fieldcontain">
			        <label for="email">Email</label>
			        <input type="text" name="email" id="email" />				        
				</div>
							
				<div data-role="fieldcontain">
			        <label for="password">Password</label>
			        <input type="password" name="password" id="password" />
				</div>
							
				<div>
					<label for="button"></label>
					<input type="submit" value="Log in" />
				</div>
			</form>
        </div>
    </div>
</body>
</html>