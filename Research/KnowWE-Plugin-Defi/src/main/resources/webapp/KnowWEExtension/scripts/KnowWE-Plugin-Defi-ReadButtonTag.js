function getReadButtonValue() {
	var form = document.readbuttonform;
	var checked = null;
	for (i = 0; i < 4; i++) {
		
		if (form.elements[i].checked) {
			checked = form.elements[i].value;
		}
	}
	
	if (checked == null) {
		alert('Bitte wählen Sie eine Bewertung aus')
	} else {
		
		var params = {
				action : 'ReadPagesSaveAction',
				value : checked 
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
	
}