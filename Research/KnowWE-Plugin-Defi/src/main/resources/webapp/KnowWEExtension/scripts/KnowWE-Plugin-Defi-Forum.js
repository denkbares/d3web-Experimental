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
		defiAlert("Bitte geben Sie eine Überschrift an.");
		return;
	}
	
	if (document.getElementsByName(id+"_text")[0].value == "") {
		defiAlert("Bitte geben Sie eine Nachricht ein.");
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
						if(res[0] != "") 
							defiAlert(res[0], function() {window.location = "Wiki.jsp?page="+res[1];}); 
						else
							window.location = "Wiki.jsp?page="+res[1];
					}
			}
	}

	loadWindow();
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
				fn : function(){ setTimeout ( 'window.location = "Wiki.jsp?page=Persoenliche Nachrichten('+user1+','+user2+')"',200 ); }
			}
	}
	
	
	new _KA(options).send();
}

function loadWindow() {
	// create alert-box and background
	alertDiv = document.getElementsByTagName("body")[0].appendChild(document.createElement("div"));
	alertDiv.id = "alertDialog";
	alertDivModal = document.getElementsByTagName("body")[0].appendChild(document.createElement("div"));
	alertDivModal.id = "alertDialogModal";

	// position - alert-box
	alertDiv.style.top = (window.innerHeight - 100 - alertDiv.offsetHeight)/2 + "px";
	alertDiv.style.left = (document.documentElement.scrollWidth - alertDiv.offsetWidth)/2 + "px";
	// position - background
	alertDivModal.style.height = document.documentElement.scrollHeight + "px";
	
	// innerHTML
	alertDiv.innerHTML = "<p>Erstelle Forum...<br /><img src='KnowWEExtension/images/load.gif' height='145px' width='220px' /></p>";
}

function getScrollXY() {
    var scrOfX = 0, scrOfY = 0;
 
    if( typeof( window.pageYOffset ) == 'number' ) {
        //Netscape compliant
        scrOfY = window.pageYOffset;
        scrOfX = window.pageXOffset;
    } else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) {
        //DOM compliant
        scrOfY = document.body.scrollTop;
        scrOfX = document.body.scrollLeft;
    } else if( document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) {
        //IE6 standards compliant mode
        scrOfY = document.documentElement.scrollTop;
        scrOfX = document.documentElement.scrollLeft;
    }
    return [ scrOfX, scrOfY ];
}
