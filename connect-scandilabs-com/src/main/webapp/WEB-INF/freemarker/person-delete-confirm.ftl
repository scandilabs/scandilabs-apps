<#import "/spring.ftl" as spring />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">

<head>
	<#include "includes/head.ftl" />
</head>
<body>

<div data-role="dialog" >
	<div data-role="header">
		<h1>Confirm Delete</h1>
	</div>
	<div data-role="content" style="padding: 15px">
		<h1>Delete ${person.displayName}?</h1>
        <a data-role="button" data-theme="b" href="../${person.id?c}/delete">
        	Yes
    	</a>
        <a data-role="button" data-theme="c" href="../${person.id?c}/edit">
        	Cancel
    	</a>
	</div>
		
</div> <!-- page -->
</body>
</html>