<#import "spring.ftl" as spring />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
    "http://www.w3.org/TR/html4/strict.dtd" >
<html xmlns="http://www.w3.org/1999/xhtml">
<#assign page = "notFound">
<head>
    <#include "includes/head.ftl" />
	<title>ScandiLabs : CRM : Signin</title>
</head>
<#flush>
<body>

  <div>
	    
			<form action="signin" method="post">
			
				<#if message??>
					<#if messageSuccess>
						<p class="successMessage">${message}</p>
					<#else>
						<p class="failureMessage">${message}</p>
					</#if>
				</#if>
			
				<div>			
			        <label for="email">Email</label>
			        <input type="text" name="email" />
				</div>
				<div>
			        <label for="password">Password</label>
			        <input type="password" name="password" />
				</div>
				<div>
					<label for="button"></label>
					<input type="submit" value="Sign in" />
				</div>
				
  </div>      

</body>
</html>