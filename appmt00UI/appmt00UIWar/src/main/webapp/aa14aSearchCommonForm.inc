<!-- Search form common fields --->

<% if (hasScheduleAccess) { %>
<div id="search_range" class="aa14a_selector_fecha">
<% } else { %>
<div id="search_range" class="aa14a_selector_fecha ocultar">
<% } %>
	<% if (lang == Language.SPANISH) { %>
	<label for="search_start_date" class="search_range"><fmt:message key="comun.desde" /></label>
	<input name="search_start_date" class="aa14a_input" value="">
	<label id="search_range_right" for="search_end_date" class="search_range"><fmt:message key="comun.hasta" /></label>
	<input name="search_end_date" class="aa14a_input" value="">
	</p>
	<% } else if (lang == Language.BASQUE) { %>
	<input name="search_start_date" class="aa14a_input" value="">
	<label for="search_start_date" class="search_range"><fmt:message key="comun.desde" /></label>
	<input id="search_range_right" name="search_end_date" class="aa14a_input" value="">
	<label  for="search_end_date" class="search_range"><fmt:message key="comun.hasta" /></label>
	<% } %>
</div>
