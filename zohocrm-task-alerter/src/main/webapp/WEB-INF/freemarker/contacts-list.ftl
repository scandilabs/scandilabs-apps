<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
    "http://www.w3.org/TR/html4/strict.dtd" >
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <#include "includes/head.ftl" />
	<title>ScandiLabs : CRM : Contact List</title>
</head>
<body>
	
	<div id="content">
	
		    <#if message??>
		        <#if messageSuccess>
		            <p class="successMessage">${message}</p>
		        <#else>
		            <p class="failureMessage">${message}</p>
		        </#if>
		    </#if>

        <h3>LIST | <a href="/contacts/history">HISTORY</a></h3>
		    
		    <table>		    
		    <tr>
		      <th style="text-align: left">Name</th>
		      <th style="text-align: left">Company</th>
		      <th>Last Activity</th>
		      <th>Next Call</th>
		    </tr>
		    <#list contacts as contact>
		      <tr>
		        <td><a href="/contacts/view/${contact.CONTACTID}">${contact.First_Name} ${contact.Last_Name}</a></td>
		        <td>${contact.Account_Name!}</td>
		        <td>${contact.Last_Activity_Time_Display!}</td>
		        <td>${contact.Next_Call_Date_Display!}</td>
		      </tr>
			  </#list>
			  </table>
			
				
	</div> <!-- content -->
</body>
</html>
