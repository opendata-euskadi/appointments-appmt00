<%--
Selects the favourite schedule using the XLNets data
--%>
<% if (hasScheduleAccess) { %>
	<script>
		console.log("> User favorite scheduleId: <%=xlnetsAuthCtx.getFavoriteSheduleId()%>");
		console.log("> User allowed schedules ids: <%=xlnetsAuthCtx.getAllowedScheduleIds()%>");
	
	    // if the user has a single schedule, hide the selector	   	   
	    <% if (xlnetsAuthCtx.getFavoriteSheduleId() != null
	   		&& xlnetsAuthCtx.getAllowedScheduleIds().size() == 1) { %>
		console.log("The user has a single available schema: hide the selector...");	   		
	 	$(".aa14a_calendar_location").hide();
	 	<% } %>
	</script>
<% } else { %>
	<script>
		console.log("> The user is NOT authenticated!");
	</script>
<% } %>

