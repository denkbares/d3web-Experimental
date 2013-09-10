jq$(document).ready(function() {
	initTabsOnLoad();
	
	initShowTabsOnClick();
});

function initTabsOnLoad() {
	var attachTab = document.getElementById('menu-attach');
	if (attachTab) {
		if (attachTab.style.visibility === 'visible') {
			showTabs();
		} else {
			hideViewTab();
			hideAttachTab();
		}
	}		
} 

function initShowTabsOnClick() {
	var element = document.getElementById('more-attach');
	if(element) {
		element.onclick = showTabs;
	}
}

function hideViewTab() {
    document.getElementById('menu-pagecontent').style.visibility='hidden';
}

function hideAttachTab() {
    var element = document.getElementById('menu-attach');
	if(element) {
		element.style.visibility='hidden';
	}
}

function showViewTab() {
	var view = document.getElementById('menu-pagecontent');
	if(view) {
		view.style.visibility='visible';
	}
}

function showAttachTab() {
	var attach = document.getElementById('menu-attach');
	if(attach) {
		attach.style.visibility='visible';
	}
}

function setTabActive(element) {
	if (element) {
		element.setAttribute('class', 'activetab');
	}
}

function setTabInactive(element) {
	if (element) {
		element.setAttribute('class', '');
	}
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

function showTabs() {
	makePageFit();
	
	var view = document.getElementById('menu-pagecontent');
	showViewTab();
	setTabInactive(view);

	var attach = document.getElementById('menu-attach');
	showAttachTab();
	setTabActive(attach);
	
	hidePageContent();
	showAttachPage();	
}

function makePageFit() {
	// basically needed heights and values
	var header = jq$('#header');
	if (header) var heightHeader = header.height() + parseFloat(header.css('padding-top')) + parseFloat(header.css('padding-bottom'));
	
	var tabs = jq$('.tabs').first();
	if (!tabs) return;
	
	var attach = jq$('#attach').first();
	if (!attach) return;
	
	// check page size
	var wHeight = window.getHeight();	
	var heightPage = attach.height() + parseFloat(tabs.css('padding-top')) + parseFloat(tabs.css('padding-bottom'));
	if (heightPage < (wHeight - heightHeader - 20)) {
		// make page fit to screen height
		wHeight = window.getHeight();
		var page = document.getElementById('actionsBottom');
		if (page) {
			var padding = wHeight - heightHeader - heightPage - 20 + "px";
			page.style.paddingBottom = padding;
		}
	}
}
