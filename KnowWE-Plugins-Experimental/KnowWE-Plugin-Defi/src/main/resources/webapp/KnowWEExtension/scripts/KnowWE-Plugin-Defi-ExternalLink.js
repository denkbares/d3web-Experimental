function externalLinkClicked(link) {
	var params = {
			action : 'LogExternalLinkAction',
			link : link
		}

		var options = {
			url : KNOWWE.core.util.getURL(params),
			response : {
				action : '',
				ids : [ '' ],
				fn : function() {}
			}
		}
	
	new _KA(options).send();
}