jq$(document).ready(function() {
	jq$(".showGraph").each(function() {
		jq$(this).bind('click', function() {
			jq$(".showGraph").hide();
			jq$(".hideGraph").show();
			jq$(".termgraphcontent").show('50');
			handleTermActionEvent(jq$(this));
		});
	});
	jq$(".hideGraph").each(function() {
		jq$(this).bind('click', function() {
			jq$(".hideGraph").hide();
			jq$(".showGraph").show();
			jq$(".termgraphcontent").hide('50');
			handleTermActionEvent(jq$(this));
		});
	});
	
	jq$(".toggleGraph").each(function() {
		jq$(this).bind('click', function() {
			jq$(".hideGraph").toggle();
			jq$(".showGraph").toggle();
			jq$(".termgraphcontent").toggle('50');
			handleTermActionEvent(jq$(this));
		});
	});
});