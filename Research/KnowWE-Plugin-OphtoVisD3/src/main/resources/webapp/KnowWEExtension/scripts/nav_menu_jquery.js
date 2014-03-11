jq$(document).ready(function() {
	jq$('#cssmenu > ul > li > a').click(function() {
		jq$('#cssmenu li').removeClass('active');
		jq$(this).closest('li').addClass('active');
		var checkElement = jq$(this).next();
		if ((checkElement.is('ul')) && (checkElement.is(':visible'))) {
			jq$(this).closest('li').removeClass('active');
			checkElement.slideUp('normal');
		}
		if ((checkElement.is('ul')) && (!checkElement.is(':visible'))) {
			jq$('#cssmenu ul ul:visible').slideUp('normal');
			checkElement.slideDown('normal');
		}
		return jq$(this).closest('li').find('ul').children().length == 0;
	});
});