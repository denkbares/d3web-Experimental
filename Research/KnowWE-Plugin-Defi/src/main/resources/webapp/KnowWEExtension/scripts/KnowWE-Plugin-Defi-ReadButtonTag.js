function getReadButtonValue(hide,number,id) {
	
	// hide tells the function whether to hide the Readbutton(value = 0) or deliver the checked radiobutton
	if (hide == 0) {
		var form = document.getElementById(id);
		var checked = null;
		for (i = 0; i < number; i++) {
			
			if (form.elements[i].checked) {
				checked = form.elements[i].value;
			}
		}
		
		if (checked == null) {
			alert('Bitte wählen Sie eine Bewertung aus')
			return;
		} else {
			
			var params = {
					action : 'ReadPagesSaveAction',
					value : checked,
					id : id
			}
		}
		
	} else {
		var params = {
				action : 'ReadPagesSaveAction',
				value : 0,
				id : id
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