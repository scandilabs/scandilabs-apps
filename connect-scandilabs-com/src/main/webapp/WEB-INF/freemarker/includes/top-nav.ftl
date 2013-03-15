<div id="topNav">	

	<div id="headerRow">
		<div id="logoBox">
			<a href="index">Integrity Rater</a>
		</div>			
	</div>

	<div id="userNav">
		<#if loggedInUser??>
		<span class="dropDownLabel">${(loggedInUser.name?j_string)!"name not set"}</span>
		<ul class="dropDownList">
			<li><a href="settings" title="go to your settings page">Settings</a></li>
			<li><a href="signout">Sign Out</a></li>
		</ul>
		<#else>
		<div id="signUpBox">
			<a href="signin">Sign in</a> |
		</div>
		<div id="signInBox">
			<a href="signup">Become a member</a>
		</div>
		</#if>
	</div>
</div>

<div id="mainNav">
	<span><a href="complaint-edit">File a complaint</a> | <a href="people">People</a> | <a href="about">About</a></span>
</div>


