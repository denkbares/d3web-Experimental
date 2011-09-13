function forumForm(form) {
	var formular = document.getElementsByName(form)[0];
	if (formular.style.display == "none") {
		formular.style.display = "block";
	} else {
		formular.style.display = "none";
		document.getElementsByName(form+"_topic")[0].value = "";
		document.getElementsByName(form+'_text')[0].value = "";
	}
}

function sendforumForm(id) {
	if (document.getElementsByName(id+"_topic")[0].value == "") {
		alert("Bitte geben Sie eine Überschrift an.");
		return;
	}
	
	if (document.getElementsByName(id+"_text")[0].value == "") {
		alert("Bitte geben Sie eine Nachricht ein.");
		return;
	}
			
	if (id == "Sonstiges") {
		var params = {
				action : 'NewForumAction',
				pagename : "",
				topic : document.getElementsByName(id+"_topic")[0].value,
				message : document.getElementsByName(id+"_text")[0].value
		}
	} else {
		var params = {
				action : 'NewForumAction',
				pagename : document.getElementsByName(id+"_select")[0].value,
				topic : document.getElementsByName(id+"_topic")[0].value,
				message : document.getElementsByName(id+"_text")[0].value
		}
	}
	
	document.getElementsByName(id+"_topic")[0].value = "";
	document.getElementsByName(id+"_text")[0].value = "";
	
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