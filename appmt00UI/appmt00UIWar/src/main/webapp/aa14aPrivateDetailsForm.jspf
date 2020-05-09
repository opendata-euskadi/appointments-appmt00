<% if (hasScheduleAccess) { %>
	<!-- Private details -->
	<p>
		<label class="aa14aPrivate" for="privateDetails"><fmt:message key="comun.privateData" />: </label>
		
		<!-- data to be sent to the server -->
		<textarea name="privateDetails" id="privateDetails"></textarea>
		
		<script>
			var tinymceLang = '<%= (lang == Language.BASQUE ? "eu" : "es") %>';	
			tinymce.init({
			  				selector: 'textarea#privateDetails',
			  				height  : 300,
			  				language: tinymceLang,
			  				setup	: function (editor) {
			  								// when editor changes update the review
			  								// (see step 4)
			        						editor.on('change',
			        								  function () {
															$("#datoprivateDetails").html(tinyMCE.activeEditor.getContent());
			        								  });
			  					      }
						 });
		</script>
	</p>
<% } %>