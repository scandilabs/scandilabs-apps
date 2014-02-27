<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
    "http://www.w3.org/TR/html4/strict.dtd" >
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<#include "includes/head.ftl" />
	<title>Contact</title>
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
    
	    <div>
          <label>Name:</label>
          ${First_Name} ${Last_Name} 
      </div>
	    <div>
          <label>Company:</label>
          ${Account_Name!}
      </div>
	    <div>
          <label>Email:</label>
          ${Email!}
      </div>
	    <div>
          <label>Last Activity:</label>
          ${Last_Activity_Time_Display!}
      </div>      
	    <div>
          <label>Next Call Date:</label>
          ${Next_Call_Date_Display!}
      </div>      
	    <div>
          <form action="/contacts/note-add" method="POST">
            <input type="hidden" name="recordId" value="${CONTACTID}" />
            <textarea rows="4" cols="42" name="note">Enter a new note</textarea>
            <br/>
            <input type="submit" value="Save" />
	        </form>            
      </div>      

      <br/>
      <table>
      <tr>
        <td><a href="/contacts/postpone/${CONTACTID}">POSTPONE</a></td>
        <td>&nbsp;&nbsp;</td>
        <td><a href="/contacts/cancel/${CONTACTID}">CANCEL</a></td>
        <td>&nbsp;&nbsp;</td>
        <td><a href="/contacts/list">LIST</a></td>
        <td>&nbsp;&nbsp;</td>
        <td><a href="/contacts/history">HISTORY</a></td>
      </tr>
      </table>
      
	</div> <!-- content -->
</body>
</html>
