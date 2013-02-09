function changeTimeTable() {
	var form = jq$("form[name='timetable_all']")[0];
	var inputs = jq$(form).find('input[type=text]');
	var inputsFormatted = "";
	var ok = true;

	for ( var i = 0; i < inputs.length; i++) {
		if (inputCorrect(inputs[i])) {
			inputs[i].style.borderColor = "";
			inputsFormatted += inputs[i].value + ":"
		}
		else {
			inputs[i].style.borderColor = "red";
			ok = false;
		}
	}
	if (!ok) return;

	var params = {
		action : 'TimeTableAction',
		inputs : inputsFormatted
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

function inputCorrect(input) {
	var val = input.value;
	if (val == "" || isNaN(val) || val < 0)
		return false;

	return true;
}