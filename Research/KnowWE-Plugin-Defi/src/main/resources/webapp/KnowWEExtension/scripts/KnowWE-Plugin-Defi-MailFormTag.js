function mailForm(id) {
	var nachricht = document.getElementById(id).nachricht.value;
	if (nachricht == "") {
		defiAlert("Sie haben keine Nachricht eingegeben");
		return false;
	}

	var params = {
		action : 'MailFormAction',
		nachricht : nachricht,
		id : id
	}

	var options = {
		url : KNOWWE.core.util.getURL(params),
		response : {
			action : '',
			ids : [ '' ],
			fn : function() {
				defiAlert(this.responseText, function() {location.reload();});
			}
		}
	}
	
	new _KA(options).send();
}
