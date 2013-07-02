var reload;

function defiAlert(txt, reload) {
	this.reload = reload;
	alertCalled(txt);
}

function alertCalled(txt) {
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
	alertDiv.innerHTML = createAlertInnerHTML(txt);
}

function createAlertInnerHTML(txt) {
	var innerHTML = "<p>"+txt+"</p>";
	innerHTML += "<input type='button' value='OK' onclick='alertClosed()' />";
		
	return innerHTML;
}

function alertClosed() {
	document.getElementsByTagName("body")[0].removeChild(document.getElementById("alertDialog"));
	document.getElementsByTagName("body")[0].removeChild(document.getElementById("alertDialogModal"));

	if (reload) location.reload();
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