function displayPersonalData(selectedUser) {
	var params = {
			action : 'GetPersonalDataAction',
			user : selectedUser
		}

		var options = {
			url : KNOWWE.core.util.getURL(params),
			response : {
				action : '',
				ids : [ '' ],
				fn : function() {
					insertPersonalData(this.responseText);
				}
			}
		}
	
	new _KA(options).send();
}

function insertPersonalData(data) {
	var list = jq$("#userDataLog");
	list.empty();
	list.append( "<li class='defi_log_dl'><a href='"+data+"'>"+data.split("/")[2]+"</li>" );
//	var form = jq$("form[name='timetable_single']")[0];
//	var inputs = jq$(form).find('input[type=text]');
//	var dates = timetable.split("#");
//	
//	jq$("select[name='users']")[0].style.borderColor = "";
//	
//	jq$.each( dates, function( i, date) {
//		inputs[i].value = date;
//	});
}