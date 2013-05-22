jq$(document).ready(function() {
	hideViewTab();
	hideAttachTab();
	
	initShowTabs();
});

function hideViewTab() {
    document.getElementById('menu-pagecontent').style.visibility='hidden';
}

function hideAttachTab() {
    var element = document.getElementById('menu-attach');
	if(element) {
		element.style.visibility='hidden';
	}
}

function initShowTabs() {
	var element = document.getElementById('more-attach');
	if(element) {
		element.onclick = showTabs;
	}
}

function showTabs() {
	var view = document.getElementById('menu-pagecontent');
	if(view) {
		view.style.visibility='visible';
		view.setAttribute('class', '');
	}
	
	var attach = document.getElementById('menu-attach');
	if(attach) {
		attach.style.visibility='visible';
		attach.setAttribute('class', 'activetab');
	}
	
	hidePageContent();
	showAttachPage();	
}

function hidePageContent() {
	var elementById=document.getElementById('pagecontent');
	if(elementById) {
		elementById.setAttribute('class', 'hidetab');
	}
}

function showAttachPage() {
	var elementById=document.getElementById('attach');
	if(elementById) {
		elementById.setAttribute('class', '');	
	}
}