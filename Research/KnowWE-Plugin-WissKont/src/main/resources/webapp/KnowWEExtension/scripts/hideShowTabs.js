jq$(document).ready(function() {
	hideViewTab();
	hideAttachTab();
	
	initShowTabs();
});

function hideViewTab() {
    document.getElementById('menu-pagecontent').style.visibility='hidden';
}

function hideAttachTab() {
    document.getElementById('menu-attach').style.visibility='hidden'; 
}

function initShowTabs() {
	  document.getElementById('more-attach').onclick = showTabs;
}

function showTabs() {
	var view = document.getElementById('menu-pagecontent');
	view.style.visibility='visible';
	view.setAttribute('class', '');
	
	var attach = document.getElementById('menu-attach');
	attach.style.visibility='visible';
	attach.setAttribute('class', 'activetab');
	
	hidePageContent();
	showAttachPage();	
}

function hidePageContent() {
	document.getElementById('pagecontent').setAttribute('class', 'hidetab');
}

function showAttachPage() {
	document.getElementById('attach').setAttribute('class', '');	
}