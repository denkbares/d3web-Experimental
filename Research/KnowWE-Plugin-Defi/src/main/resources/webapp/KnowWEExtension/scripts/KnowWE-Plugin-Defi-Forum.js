function forumForm(form) {
	var formular = document.getElementsByName(form)[0];
	if (formular.style.display == "none") {
		formular.style.display = "block";
	} else {
		formular.style.display = "none";
		document.getElementsByName(form+'_text')[0].value = "";
	}
}

function sendforumForm(id) {
			
	var params = {
			action : 'NewForumAction',
			id : id,
			message : document.getElementsByName(id+"_text")[0].value
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