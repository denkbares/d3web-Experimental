function getDataForConcept() {

	document.getElementById("conceptdata").innerHTML = '<div style="margin-left:110px;"><img src="KnowWEExtension/images/loading.png"></div>';

	var params = {
		action : 'GetDataAction',
		concept : document.getElementById("conceptname").value,
		debug : document.getElementById("debug").checked,
		wikipedia : document.getElementById("wikiinput").value
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

function createConcept() {

	document.getElementById("creationWizard").innerHTML = '<div style="margin-left:110px;"><img src="KnowWEExtension/images/loading.png"></div>';

	var params = {
		action : 'CreateAction',
		concept : document.getElementById("conceptname").value,
		wikipedia : document.getElementById("wikiinput").value
	}

	var resultID = 'creationWizard';

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

	var hermes, dbpedia, i, type;

	hermes = new Array(count);
	dbpedia = new Array(count);
	type = new Array(count);

	for (i = 0; i < count; i++) {

		if (document.getElementById("lodwizard")['submit' + i].className == 'submitc') {

			// Hole Tag + Wert
			hermes[i] = document.getElementById("hermestag" + i).innerHTML;
			dbpedia[i] = (document.getElementById("dbpediavalue" + i).type == "text") ? document
					.getElementById("dbpediavalue" + i).value
					: document.getElementById("dbpediavalue" + i).innerHTML;
			type[i] = "submit";

		} else if (document.getElementById("lodwizard")['return' + i].className == 'returnc') {

			// Hole Tag + Wert
			hermes[i] = document.getElementById("hermestag" + i).innerHTML;
			dbpedia[i] = (document.getElementById("dbpediavalue" + i).type == "text") ? document
					.getElementById("dbpediavalue" + i).value
					: document.getElementById("dbpediavalue" + i).innerHTML;
			type[i] = "return";

		} else if (document.getElementById("lodwizard")['ignore' + i].className == 'ignorec') {

			// nicht übernehmen aber ignorieren (nichts anklicken) -->
			// speichern auf seite! mit tags
			hermes[i] = document.getElementById("hermestag" + i).innerHTML;
			dbpedia[i] = (document.getElementById("dbpediavalue" + i).type == "text") ? document
					.getElementById("dbpediavalue" + i).value
					: document.getElementById("dbpediavalue" + i).innerHTML;
			type[i] = "ignore"

		} else if (document.getElementById("lodwizard")['qmarks' + i].className == 'qmarksc') {

			// Tue nichts ;)
			dbpedia[i] = "";
			hermes[i] = "";
			type[i] = "qmarks";

		}
	}

	var params = {
		action : 'ParseDataAction',
		type : type,
		hermes : hermes,
		dbpedia : dbpedia,
		concept : document.getElementById("conceptname").value
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

	document.getElementById("conceptdata").innerHTML = '<div style="margin-left:110px;"><img src="KnowWEExtension/images/loading.png"></div>';
}

function submitDataCreate(count, wiki) {

	var hermes, dbpedia, i, type;

	var last = count - 1;

	hermes = new Array(count);
	dbpedia = new Array(count);
	type = new Array(count);

	for (i = 0; i < count - 1; i++) {

		if (document.getElementById('submit' + i).className == 'submitc') {

			// Hole Tag + Wert
			hermes[i] = document.getElementById("hermestag" + i).innerHTML;
			dbpedia[i] = (document.getElementById("dbpediavalue" + i).type == "select-one") ? document
					.getElementById("dbpediavalue" + i).value
					: document.getElementById("dbpediavalue" + i).innerHTML;
			type[i] = "submit";

		} else if (document.getElementById('qmarks' + i).className == 'qmarksc') {

			// Tue nichts ;)
			dbpedia[i] = "";
			hermes[i] = "";
			type[i] = "qmarks";

		}
	}
	if (document.getElementById('submitoptional').className == 'submitc') {

		// Hole Tag + Wert
		hermes[last] = document.getElementById("hermestag" + last).innerHTML;
		dbpedia[last] = (document.getElementById("dbpediavalue" + last).type == "select-one") ? document
				.getElementById("dbpediavalue" + last).value
				: document.getElementById("dbpediavalue" + last).innerHTML;
		type[last] = "submit";

	} else if (document.getElementById('qmarksoptional').className == 'qmarksc') {

		// Tue nichts ;)
		dbpedia[last] = "";
		hermes[last] = "";
		type[last] = "qmarks";

	}

	var params = {
		action : 'ParseDataAction',
		type : type,
		hermes : hermes,
		dbpedia : dbpedia,
		concept : document.getElementById("conceptname").value,
		create : 'true',
		wiki : wiki
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

	document.getElementById("creationWizard").innerHTML = '<div style="margin-left:110px;"><img src="KnowWEExtension/images/loading.png"></div>';
}

function Mappings(type) {

	document.getElementById("maphandler").innerHTML = '<div style="margin-left:72px;"><img src="KnowWEExtension/images/loading.png"></div>';

	var params = {
		action : 'MappingsAction',
		type : type
	}

	var resultID = 'maphandler';

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

	var nr = objButton.name.substr(6);
	var submit = document.getElementById("lodwizard")['submit' + nr].className;
	var ignore = document.getElementById("lodwizard")['ignore' + nr].className;
	var ret = document.getElementById("lodwizard")['return' + nr].className;
	var ask = document.getElementById("lodwizard")['qmarks' + nr].className;

	if (objButton.name != 'submit' + nr && submit == 'submitc') {

		document.getElementById("lodwizard")['submit' + nr].className = "submit";

	} else if (objButton.name != 'ignore' + nr && ignore == 'ignorec') {

		document.getElementById("lodwizard")['ignore' + nr].className = "ignore";

	} else if (objButton.name != 'return' + nr && ret == 'returnc') {

		document.getElementById("lodwizard")['return' + nr].className = "return";

	} else if (objButton.name != 'qmarks' + nr && ask == 'qmarksc') {

		document.getElementById("lodwizard")['qmarks' + nr].className = "qmarks";

	}

	if (objButton.className == 'submit' || objButton.className == 'submitc') {

		objButton.className = (objButton.className == 'submit') ? 'submitc'
				: 'submit';

	} else if (objButton.className == 'ignore'
			|| objButton.className == 'ignorec') {

		objButton.className = (objButton.className == 'ignore') ? 'ignorec'
				: 'ignore';

	} else if (objButton.className == 'return'
			|| objButton.className == 'returnc') {

		objButton.className = (objButton.className == 'return') ? 'returnc'
				: 'return';

	} else if (objButton.className == 'qmarks'
			|| objButton.className == 'qmarksc') {

		objButton.className = (objButton.className == 'qmarks') ? 'qmarksc'
				: 'qmarks';

	}

	submit = document.getElementById("lodwizard")['submit' + nr].className;
	ignore = document.getElementById("lodwizard")['ignore' + nr].className;
	ret = document.getElementById("lodwizard")['return' + nr].className;
	ask = document.getElementById("lodwizard")['qmarks' + nr].className;

	if (submit == 'submit' && ignore == 'ignore' && ret == 'return'
			&& ask == 'qmarks') {
		document.getElementById("lodwizard")['qmarks' + nr].className = "qmarksc";
	}

}

function buttonToggleCreate(objButton) {

	var nr = objButton.id.substr(6);
	var submit = document.getElementById('submit' + nr).className;
	var ask = document.getElementById('qmarks' + nr).className;

	if (objButton.id != 'submit' + nr && submit == 'submitc') {

		document.getElementById('submit' + nr).className = "submit";

	} else if (objButton.id != 'qmarks' + nr && ask == 'qmarksc') {

		document.getElementById('qmarks' + nr).className = "qmarks";

	}

	if (objButton.className == 'submit' || objButton.className == 'submitc') {

		objButton.className = (objButton.className == 'submit') ? 'submitc'
				: 'submit';

	} else if (objButton.className == 'qmarks'
			|| objButton.className == 'qmarksc') {

		objButton.className = (objButton.className == 'qmarks') ? 'qmarksc'
				: 'qmarks';

	}

	submit = document.getElementById('submit' + nr).className;
	ask = document.getElementById('qmarks' + nr).className;

	if (submit == 'submit' && ask == 'qmarks') {
		document.getElementById('qmarks' + nr).className = "qmarksc";
	}
	// Reset optional
	if (submit == 'submitc') {
		document.getElementById('submitoptional').className = "submit";
		document.getElementById('qmarksoptional').className = "qmarksc";
	}
}

function buttonToggleCreateOptional(objButton, nr) {

	var submit = document.getElementById('submitoptional').className;
	var ask = document.getElementById('qmarksoptional').className;

	if (objButton.id != 'submitoptional' && submit == 'submitc') {

		document.getElementById('submitoptional').className = "submit";

	} else if (objButton.id != 'qmarksoptional' && ask == 'qmarksc') {

		document.getElementById('qmarksoptional').className = "qmarks";

	}

	if (objButton.className == 'submit' || objButton.className == 'submitc') {

		objButton.className = (objButton.className == 'submit') ? 'submitc'
				: 'submit';

	} else if (objButton.className == 'qmarks'
			|| objButton.className == 'qmarksc') {

		objButton.className = (objButton.className == 'qmarks') ? 'qmarksc'
				: 'qmarks';

	}

	submit = document.getElementById('submitoptional').className;
	ask = document.getElementById('qmarksoptional').className;

	if (submit == 'submit' && ask == 'qmarks') {
		document.getElementById('qmarksoptional').className = "qmarksc";
	}
	// Reset all other options if optional is clicked.
	if (submit == 'submitc') {
		for ( var i = 0; i < nr; i++) {
			document.getElementById('submit' + i).className = "submit";
			document.getElementById('qmarks' + i).className = "qmarksc";
		}
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

function shownhide() {

	var wikitext = document.getElementById("wikiurl").innerHTML;
	var elem = document.getElementById("wikiinput");

	if (elem.style.display == "") {
		wikitext = '+' + wikitext.substr(1);
		document.getElementById("wikiurl").innerHTML = wikitext;
		elem.style.display = "none";
	} else {
		wikitext = '-' + wikitext.substr(1);
		document.getElementById("wikiurl").innerHTML = wikitext;
		elem.style.display = "";
	}
}

function submitYearQuiz(obj) {
	var params = {
		action : 'SolveYearOfBirthAction',
		answer : obj.innerHTML,
		subject : document.getElementById("quizyearsubject").innerHTML
	}

	var resultID = 'quizyearanswers';

	var options = {
		url : KNOWWE.core.util.getURL(params),
		response : {
			action : 'insert',
			ids : [ resultID ]
		}
	}

	new _KA(options).send();

}

function switchMenu(num) {
	var toggle = document.getElementById("savetoggle").innerHTML;
	for ( var i = 0; i < num; i++) {

		var el = document.getElementById('instore' + i);

		if (el.style.display != 'none') {
			el.style.display = 'none';
			toggle = toggle.replace(/-/, "+");
			document.getElementById("savetoggle").innerHTML = toggle;
		} else {
			el.style.display = '';
			toggle = toggle.replace(/\+/, "-");
			document.getElementById("savetoggle").innerHTML = toggle;
		}
	}
}

function createCoordURL(num) {

	var lat, long, url;

	lat = document.getElementById('lat' + num).childNodes[0].value.replace(/,/,
			".");
	long = document.getElementById('lon' + num).childNodes[0].value.replace(
			/,/, ".");
	// build url for map
	url = '/KnowWE/Map.jsp?lat=' + lat + '&long=' + long + '&num=' + num;

	return url;
}

function openWindow(url, num) {
	fenster = window.open(url, "KnowWE Map"+num,
			"width=600,height=400,status=yes,scrollbars=yes,resizable=yes");
	fenster.focus();
}
