function getReadButtonValue(hide) {
	
	// hide tells the function whether to hide the Readbutton(value = 0) or deliver the checked radiobutton
	if (hide == 0) {
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
		}
		
	} else {
		var params = {
				action : 'ReadPagesSaveAction',
				value : 0 
		}
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