//TODO: Schwacher workaround
if(document.getElementById) {
	window.alert = function(txt) {
		alertCalled(txt);
	}
}

function alertCalled(txt) {
	// create alert-box and background
	alertDiv = document.getElementsByTagName("body")[0].appendChild(document.createElement("div"));
	alertDiv.id = "alertDialog";
	alertDivModal = document.getElementsByTagName("body")[0].appendChild(document.createElement("div"));
	alertDivModal.id = "alertDialogModal";

	// position - alert-box
	alertDiv.style.top = document.documentElement.scrollTop + 200 + "px";
	alertDiv.style.left = (document.documentElement.scrollWidth - alertDiv.offsetWidth)/2 + "px";
	// position - background
	alertDivModal.style.height = document.documentElement.scrollHeight + "px";
	
	// innerHTML
	alertDiv.innerHTML = createAlertInnerHTML(txt);
}

function alertClosed() {
	document.getElementsByTagName("body")[0].removeChild(document.getElementById("alertDialog"));
	document.getElementsByTagName("body")[0].removeChild(document.getElementById("alertDialogModal"));
}

function createAlertInnerHTML(txt) {
	var innerHTML = "<p>"+txt+"</p>";
	innerHTML += "<input type='button' value='OK' onclick='alertClosed()' />";
		
	return innerHTML;
}

function linkAlertCalled(txt, link) {
	// create alert-box and background
	alertDiv = document.getElementsByTagName("body")[0].appendChild(document.createElement("div"));
	alertDiv.id = "alertDialog";
	alertDivModal = document.getElementsByTagName("body")[0].appendChild(document.createElement("div"));
	alertDivModal.id = "alertDialogModal";

	// position - alert-box
	alertDiv.style.top = document.documentElement.scrollTop + 200 + "px";
	alertDiv.style.left = (document.documentElement.scrollWidth - alertDiv.offsetWidth)/2 + "px";
	// position - background
	alertDivModal.style.height = document.documentElement.scrollHeight + "px";
	
	// innerHTML
	alertDiv.innerHTML = createLinkAlertInnerHTML(txt, link);
}

function createLinkAlertInnerHTML(txt, link) {
	var innerHTML = "<p>"+txt+"</p>";
	innerHTML += "<input type='button' value='OK' onclick='alertClosed();visitLink(\""+link+"\")' />";
		
	return innerHTML;
}

function visitLink(link) {
	window.open(link,'_blank');
}