function sendReadbutton(id, threshold) {
	container = document.getElementById('rb_' + id);
	radiobuttons = $ES('input[type=radio]' , 'rb_' + id);
	labels = $ES('tr', 'rb_' + id)[1].childNodes;
	checked = -1;
	
	for (i = 0; i < radiobuttons.length; i++) {
		if (radiobuttons[i].checked)
			checked = i;
	}
	
	if (checked == -1) {
		alert('Bitte wählen Sie eine Bewertung aus')
		return;
	}
	
	realvalue = checked + 1;
	
	discussed = '';
	close = '';
	if (realvalue <= threshold) {
		discussed = 'Noch nicht';
		close = 'Nein';
	}
	else {
		discussed = 'Kein Link';
		close = 'Ja';
	}
	
	var params = {
			action : 'ReadbuttonSubmitAction',
			realvalue : realvalue,
			value : radiobuttons[checked].value,
			label : labels[checked].innerHTML,
			discussed : discussed,
			closed : close,
			id : id
	}
	
	var options = {
			url : KNOWWE.core.util.getURL(params),
			response : {
				action : '',
				ids : [ '' ],
				fn : function(){ setTimeout ( 'document.location.reload()', 100 ); }
			}
	}
	
	new _KA(options).send();
}

function readbuttonDiscuss(id) {
	
	var params = {
			action : 'ReadbuttonLinkClickedAction',
			discussed : 'Ja',
			id : id
	}
	
	var options = {
			url : KNOWWE.core.util.getURL(params),
			response : {
				action : '',
				ids : [ '' ],
				fn : function(){ }
			}
	}
	
	new _KA(options).send();
}

function readbuttonCloseLink(id) {
	
	var params = {
			action : 'ReadbuttonLinkClickedAction',
			discussed : 'Nein',
			id : id
	}
	
	var options = {
			url : KNOWWE.core.util.getURL(params),
			response : {
				action : '',
				ids : [ '' ],
				fn : function(){ setTimeout ( 'document.location.reload()', 100 ); }
			}
	}
	
	new _KA(options).send();
}