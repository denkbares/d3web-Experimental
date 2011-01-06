function getDataForConcept(web) {

	// // Necessary attributes for the action
	// var user = _KS('#sessionvalues-user').value;
	// var topic = _KS('#sessionvalues-topic').value;

	var params = {
		action : 'GetDataAction',
		web : web,
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
