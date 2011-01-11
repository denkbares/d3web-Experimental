function getDataForConcept() {

	 // Necessary attributes for the action
//	 var user = _KS('#sessionvalues-user').value;
//	 var topic = _KS('#sessionvalues-topic').value;

	var params = {
		action : 'GetDataAction',
		web : 'default_web',
		concept : document.getElementById("concept").value
	}

	var resultID = 'conceptdata';

	var options = {
		url : KNOWWE.core.util.getURL(params),
		response : {
			action : 'insert',
			ids : [ resultID ]
		}
	}

	new _KA(options).send();

}

function submitData(count) {

	var hermes, dbpedia, i, type, user = _KS('#sessionvalues-user').value;

	hermes = new Array(count);
	dbpedia = new Array(count);
	type = new Array(count);

	for (i = 0; i < count; i++) {

		if (document.getElementById("lodwizard")['submit' + i].className == 'submitc') {

			// Hole Tag + Wert
			hermes[i] = document.getElementById("hermestag" + i).innerHTML;
			dbpedia[i] = document.getElementById("dbpediavalue" + i).innerHTML;
			type[i] = "submit";

		} else if (document.getElementById("lodwizard")['cancel' + i].className == 'cancelc') {

			// tue nichts =), leere variablen um konsistenz des arrays zu bewahren
			dbpedia[i] = "";
			hermes[i] = "";
			type[i] = "cancel";

		} else if (document.getElementById("lodwizard")['return' + i].className == 'returnc') {

			// Hole Tag + Wert
			hermes[i] = document.getElementById("hermestag" + i).innerHTML;
			dbpedia[i] = document.getElementById("dbpediavalue" + i).innerHTML;
			type[i] = "return";

		} else {

			// nicht übernehmen aber ignorieren (nichts anklicken) -->
			// speichern auf seite! mit tags
			hermes[i] = document.getElementById("hermestag" + i).innerHTML;
			dbpedia[i] = document.getElementById("dbpediavalue" + i).innerHTML;
			type[i] = "ignore"

		}

	}

	var params = {
		action : 'ParseDataAction',
		type : type,
		hermes : hermes,
		dbpedia : dbpedia,
		concept : document.getElementById("conceptname").value,
		user : user
	}

	var resultID = 'conceptdata';

	var options = {
		url : KNOWWE.core.util.getURL(params),
		response : {
			action : 'insert',
			ids : [ resultID ]
		}
	}

	new _KA(options).send();
}

function buttonToggle(objButton) {

	var nr = objButton.name.charAt(6);
	var submit = document.getElementById("lodwizard")['submit' + nr].className;
	var cancel = document.getElementById("lodwizard")['cancel' + nr].className;
	var ret = document.getElementById("lodwizard")['return' + nr].className;
	if (objButton.name != 'submit' + nr && submit == 'submitc') {

		document.getElementById("lodwizard")['submit' + nr].className = "submit";

	} else if (objButton.name != 'cancel' + nr && cancel == 'cancelc') {

		document.getElementById("lodwizard")['cancel' + nr].className = "cancel";

	} else if (objButton.name != 'return' + nr && ret == 'returnc') {

		document.getElementById("lodwizard")['return' + nr].className = "return";

	}

	if (objButton.className == 'submit' || objButton.className == 'submitc') {

		objButton.className = (objButton.className == 'submit') ? 'submitc'
				: 'submit';

	} else if (objButton.className == 'cancel'
			|| objButton.className == 'cancelc') {

		objButton.className = (objButton.className == 'cancel') ? 'cancelc'
				: 'cancel';

	} else if (objButton.className == 'return'
			|| objButton.className == 'returnc') {

		objButton.className = (objButton.className == 'return') ? 'returnc'
				: 'return';

	}
}

function cancelWizard() {

	document.getElementById("conceptdata").innerHTML = '';

}

function changeOnMouseOver(objButton) {

	if (objButton.src.match(/submit.png$/)) {
		objButton.src = "KnowWEExtension/images/submitc.png";
	}
	if (objButton.src.match(/cancel.png$/)) {
		objButton.src = "KnowWEExtension/images/cancelc.png";
	}

}

function changeOnMouseOut(objButton) {

	if (objButton.src.match(/submitc.png$/)) {
		objButton.src = "KnowWEExtension/images/submit.png";
	}
	if (objButton.src.match(/cancelc.png$/)) {
		objButton.src = "KnowWEExtension/images/cancel.png";
	}

}