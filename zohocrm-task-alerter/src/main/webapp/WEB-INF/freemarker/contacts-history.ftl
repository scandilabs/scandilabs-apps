<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
    "http://www.w3.org/TR/html4/strict.dtd" >
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
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
		    
        <h3><a href="/contacts/list">LIST</a> | HISTORY</h3>
        
        ${historyBodyHtml}        		    
				
	</div> <!-- content -->
</body>
</html>
