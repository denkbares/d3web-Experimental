function mailForm(id) {
	var nachricht = document.getElementById(id).nachricht.value;
	if (nachricht == "") {
		alert("Sie haben keine Nachricht eingegeben");
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
				alert(this.responseText);
				document.location.reload();
			}
		}
	}
	
	new _KA(options).send();
}
