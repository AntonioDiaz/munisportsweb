<%@include file="taglibs.jsp"%>
<script>
	$(document).ready(function() {
		$('#btnBack').on('click', function(event) {
			event.preventDefault();
			window.location.href = "/users/list";
		});
		$('#updatePassword').attr('checked', true);
		$('#updatePassword').attr('disabled', true);
		$('#my_form').submit(function() {
			$('#updatePassword').attr('disabled', false);
		});
	});
</script>
<form:form method="post" action="doAdd" commandName="my_form" name="my_form" cssClass="form-horizontal">
	<%@ include file="/WEB-INF/jsp/users_form.jsp"%>
	<div class="form-group">
		<label class="control-label col-sm-4">&nbsp;</label>
		<div class="col-sm-2">
			<button id="btnBack" type="button" class="btn btn-default btn-block">cancelar</button>
		</div>		
		<div class="col-sm-2">
			<button type="submit" class="btn btn-primary btn-block">crear</button>
		</div>
		<div class="col-sm-4">&nbsp;</div>
	</div>
</form:form>