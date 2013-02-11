/** Change the timetable template */
function changeTimeTableTemplate() {
	var form = jq$("form[name='timetable_all']")[0];
	var inputs = jq$(form).find('input[type=text]');
	var inputsChecked = "";
	var ok = true;

	for ( var i = 0; i < inputs.length; i++) {
		if (checkInputTimeTableTemplate(inputs[i])) {
			inputs[i].style.borderColor = "";
			inputsChecked += inputs[i].value + ":"
		}
		else {
			inputs[i].style.borderColor = "red";
			ok = false;
		}
	}
	if (!ok) return;

	var params = {
		action : 'TimeTableAction',
		inputs : inputsChecked
	}

	var options = {
		url : KNOWWE.core.util.getURL(params),
		response : {
			action : '',
			ids : [ '' ],
			fn : function() {
				alert(this.responseText);
				location.reload(2500);
			}
		}
	}

	new _KA(options).send();
}

/** check input for timetable template */
function checkInputTimeTableTemplate(input) {
	var val = input.value;

	return (val != "" && !isNaN(val) && val >= 0);
}

/** Change a personal timetable */
function changePersonalTimeTable() {
	var user = jq$("select[name='users']").val();
	var form = jq$("form[name='timetable_single']")[0];
	var inputs = jq$(form).find('input[type=text]');
	var inputsChecked = "";
	var ok = true;
	
	if (user == "")
		jq$("select[name='users']")[0].style.borderColor = "red";
	else
		jq$("select[name='users']")[0].style.borderColor = "";
	
	for ( var i = 0; i < inputs.length; i++) {
		if (checkInputTimeTable(inputs[i])) {
			inputs[i].style.borderColor = "";
			inputsChecked += inputs[i].value + "#"
		}
		else {
			inputs[i].style.borderColor = "red";
			ok = false;
		}
	}
	if (!ok) return;
	
	var params = {
		action : 'PersonalTimeTableAction',
		user : user,
		inputs : inputsChecked
	}

	var options = {
		url : KNOWWE.core.util.getURL(params),
		response : {
			action : '',
			ids : [ '' ],
			fn : function() {
				alert(this.responseText);
				document.location.reload(2000);
			}
		}
	}

	new _KA(options).send();
}

/** check input for timetable*/
function checkInputTimeTable(input) {
	var val = input.value;
	var date = val.split(".");
	
	if (date.length != 3) return false;
	if (isNaN(date[0]) || isNaN(date[1]) || isNaN(date[2])) return false;
	if (date[0] > 31 || date[0] < 0) return false;
	if (date[1] > 12 || date[1] < 0) return false;
	if (date[2] > 2500 || date[2] < 1500) return false;
	
	
	return  true;
}

function displayPersonalTimeTable(selectedUser) {
	var params = {
			action : 'GetPersonalTimeTableAction',
			user : selectedUser
		}

		var options = {
			url : KNOWWE.core.util.getURL(params),
			response : {
				action : '',
				ids : [ '' ],
				fn : function() {
					insertPersonalTimeTable(this.responseText);
				}
			}
		}
	
	new _KA(options).send();
}

function insertPersonalTimeTable(timetable) {
	var form = jq$("form[name='timetable_single']")[0];
	var inputs = jq$(form).find('input[type=text]');
	var dates = timetable.split("#");
	
	jq$("select[name='users']")[0].style.borderColor = "";
	
	jq$.each( dates, function( i, date) {
		inputs[i].value = date;
	});
}