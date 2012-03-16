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
			
	var params = {
			action : 'NewForumAction',
			pagename : id,
			topic : document.getElementsByName(id+"_topic")[0].value,
			message : document.getElementsByName(id+"_text")[0].value
	}
	
	document.getElementsByName(id+"_topic")[0].value = "";
	document.getElementsByName(id+"_text")[0].value = "";
	
	var options = {
			url : KNOWWE.core.util.getURL(params),
			response : {
				action : '',
				ids : [ '' ],
				fn : function(){ 
						var res = this.responseText.split("\n");
						if(res[0] != "") alert(res[0]); 
						window.location = "Wiki.jsp?page="+res[1];
					}
			}
	}
	
	new _KA(options).send();
}

function newChat(user1, user2) {
			
	var params = {
			action : 'PersonalMessageAction',
			user1 : user1,
			user2 : user2
	}

	var options = {
			url : KNOWWE.core.util.getURL(params),
			response : {
				action : '',
				ids : [ '' ],
				fn : function(){ setTimeout ( 'window.location = "Wiki.jsp?page=Persönliche Nachrichten('+user1+','+user2+')"',200 ); }
			}
	}
	
	
	new _KA(options).send();
}