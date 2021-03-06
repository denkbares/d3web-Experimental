/**
 * * SkinQute javascript extensions * needed to initialise RoundedCorner
 * elements. *
 */

jq$(document).ready(function() {
	var wHeight = window.getHeight();
	
	// basically needed heights and values
	var header = jq$('#header');
	if (header) var heightHeader = header.height() + parseFloat(header.css('padding-top')) + parseFloat(header.css('padding-bottom'));
	
	var tabs = jq$('.tabs').first();
	if (!tabs) return;
	tabsPadding = parseFloat(tabs.css('padding-top')) + parseFloat(tabs.css('padding-bottom'));
	
	var footer = jq$('#footer');
	if (footer) var heightFooter = footer.height() + parseFloat(footer.css('padding-bottom'));
	
	// make left menu fit to screen height
	var element = jq$("#favorites");
	if (!element) return;
	element.css("min-height", wHeight - heightHeader - heightFooter + "px");

	// make page fit to screen height if necessary
	var heightPage = tabs.height() + tabsPadding;
	if (heightPage < (wHeight - heightHeader - 20)) {
		wHeight = window.getHeight();
		var page = document.getElementById('actionsBottom');
		if (!page) return;
		var padding = wHeight - heightHeader - heightPage - 20 + "px";
		page.style.paddingBottom = padding;
	}

	initPageFitForViewAndAttachTab();
});

function initPageFitForViewAndAttachTab() {
	var pageTab = document.getElementById("menu-pagecontent");
	if (pageTab) {
		pageTab.onclick = makePageFitView;
	}

	var attachTab = document.getElementById("menu-attach");
	if (attachTab) {
		attachTab.onclick = makePageFitAttach;
	}
}

function makePageFitView() {
	// basically needed heights and values
	var header = jq$('#header');
	if (header) var heightHeader = header.height() + parseFloat(header.css('padding-top')) + parseFloat(header.css('padding-bottom'));
	
	var pagecontent = jq$('#pagecontent').first();
	var tabs = jq$('.tabs').first();
	if (!tabs || !pagecontent) return;
	var pagePadding = parseFloat(pagecontent.css('padding-top')) + parseFloat(pagecontent.css('padding-bottom'));
	var tabsPadding = parseFloat(tabs.css('padding-top')) + parseFloat(tabs.css('padding-bottom'));
	
	// check page size
	var wHeight = window.getHeight();
	var heightPage = pagecontent.height() + pagePadding + tabsPadding;
	if (heightPage < (wHeight - heightHeader - 20)) {
		fillUpPadding(heightPage, heightHeader);
	} else resetPadding();
}

function makePageFitAttach() {
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
		fillUpPadding(heightPage, heightHeader);
	} else resetPadding();
}

function fillUpPadding(heightPage, heightHeader) {
	// make page fit to screen height
	wHeight = window.getHeight();
	var actionsBottom = document.getElementById('actionsBottom');
	if (actionsBottom) {
		var menu = jq$('#favorites');
		
		var padding = wHeight - heightHeader - heightPage - 20 + "px";
		actionsBottom.style.paddingBottom = padding;
	}
}

function resetPadding() {
	var page = document.getElementById('actionsBottom');
	if (page) page.style.paddingBottom = 0 + "px";
}

if (RoundedCorners) {
	var r = RoundedCorners;
	// r.register( "header", ['yyyy', 'lime', 'lime' ] );
	// r.register( "footer", ['yyyy', 'lime', 'lime' ] );

}

// overwrite "Wiki.locatemenu" to correct bug
// if menu ist relative to a non-document element
Wiki.locatemenu = function(base, el) {
	var win = {
		'x' : window.getWidth(),
		'y' : window.getHeight()
	}, scroll = {
		'x' : window.getScrollLeft(),
		'y' : window.getScrollTop()
	}, corner = base.getPosition(), offset = {
		'x' : base.offsetWidth - el.offsetWidth,
		'y' : base.offsetHeight
	}, popup = {
		'x' : el.offsetWidth,
		'y' : el.offsetHeight
	}, prop = {
		'x' : 'left',
		'y' : 'top'
	}, parent = {
		'x' : 0,
		'y' : 0
	};

	// some special treatment for search to avoid annoying menu position
	if (base == $('query')) {
		parent = {
			'x' : base.offsetWidth,
			'y' : base.offsetHeight
		};
	} else {
		// search for parent defining it own coordinate system
		for ( var anchor = $(el.parentNode); anchor && anchor != document; anchor = $(anchor.parentNode)) {
			var cssPosition = anchor.getStyle('position');
			if (cssPosition == 'absolute' || cssPosition == 'relative') {
				parent = anchor.getPosition();
				break;
			}
		}
	}

	for ( var z in prop) {
		// top-left corner of base
		var pos = corner[z] + offset[z] - parent[z];
		if ((pos + popup[z] - scroll[z]) > win[z])
			pos = win[z] - popup[z] + scroll[z];
		el.setStyle(prop[z], pos);
	}
};

// hack into search menu
// 1) to avoid popup at mouseover but having it on focus instead
// 2) locate search-popup, even if text is selected
if (SearchBox) {

	SearchBox.onPageLoadQuickSearch_original = SearchBox.onPageLoadQuickSearch;
	SearchBox.ajaxQuickSearch_original = SearchBox.ajaxQuickSearch;
	SearchBox.noSearchTargetText_original = null;

	SearchBox.onPageLoadQuickSearch = function() {
		// call original first, doing lots of other stuff
		SearchBox.onPageLoadQuickSearch_original();
		// remove hover events
		$(this.query.form).removeEvents("mouseout");
		$(this.query.form).removeEvents("mouseover");
		// and add focus events instead
		$(this.query).addEvent("blur", function() {
			this.hover.start(0);
		}.bind(this)).addEvent("focus", function() {
			Wiki.locatemenu(this.query, $("searchboxMenu"));
			this.hover.start(0.9);
		}.bind(this));
	}

	SearchBox.ajaxQuickSearch = function() {
		// capture original text before first search
		if (!SearchBox.noSearchTargetText_original) {
			SearchBox.noSearchTargetText_original = $('searchTarget').innerHTML;
		}
		SearchBox.ajaxQuickSearch_original();
		// if search is empty, restore original text and relocate menu
		var a = this.query.value.stripScripts();
		if ((a == null) || (a.trim() == "") || (a == this.query.defaultValue)) {
			$('searchTarget').innerHTML = SearchBox.noSearchTargetText_original;
			Wiki.locatemenu($("query"), $("searchboxMenu"));
		}
	}
};

var DenkbaresSkin = {};

/**
 * Initialize cutting edge favorite scrolling
 */
DenkbaresSkin.initFavScroll = function() {
	if (DenkbaresSkin.isInitialized)
		return;
	var element = $("favorites");
	if (!element)
		return;
	DenkbaresSkin.isInitialized = true;
	DenkbaresSkin.originY = element.offsetTop;
	// initialize some additional events
	document.body.onclick = DenkbaresSkin.checkDocSizeScroll;
};

/**
 * Quick convenience function to be called every time the document size may have
 * changed. Unfortunately this cannot be traced by an event.
 */
DenkbaresSkin.checkDocSizeScroll = function() {
	// alert("check");
	var docHeight = window.getScrollHeight();
	if (DenkbaresSkin.docHeight == docHeight)
		return;
	DenkbaresSkin.docHeight = docHeight;
	DenkbaresSkin.checkFavScroll();
};

/**
 * Get the height of the document independent of the used browser.
 */
DenkbaresSkin.getDocHeight = function() {
	var D = document;
	return Math.max(Math.max(D.body.scrollHeight,
			D.documentElement.scrollHeight), Math.max(D.body.offsetHeight,
			D.documentElement.offsetHeight), Math.max(D.body.clientHeight,
			D.documentElement.clientHeight));
}

/**
 * Adapt the left menu favorites to the screen so that the display size is
 * optimally used.
 */
DenkbaresSkin.checkFavScroll = function() {
	DenkbaresSkin.initFavScroll();
	var element = $("favorites");
	if (!element)
		return;
	var originY = DenkbaresSkin.originY;
	var wHeight = window.getHeight();
	var docHeight = DenkbaresSkin.getDocHeight();
	var favHeight = element.clientHeight;
	var favBottom = originY + favHeight;
	var scrollY = window.getScrollTop();
	var scrollMax = docHeight - wHeight;
	var favToScroll = favHeight - wHeight;
	var actionsBottom = $("actionsBottom");
	var disableFixing = false;
	if (actionsBottom) {
		disableFixing = (favHeight >= $("actionsBottom").offsetTop
				+ $("actionsBottom").clientHeight);
	}
	
	// basically needed heights and values
	var header = jq$('#header');
	if (header) var heightHeader = header.height() + parseFloat(header.css('padding-top')) + parseFloat(header.css('padding-bottom'));
	
	var footer = jq$('#footer');
	if (footer) var heightFooter = footer.height() + parseFloat(footer.css('padding-bottom'));
	
	// scrolling
	if (scrollY <= originY || disableFixing) {
		// when reaching top of page or if page height is made by leftMenu
		// align fav originally to page
		element.style.position = "static";
		element.style.top = originY + "px";
		element.style.height = wHeight - heightHeader - heightFooter + "px";
	} else if (scrollMax - scrollY <= favToScroll) {
		// when reaching end of page
		// align bottom of fav to bottom of page
		element.style.position = "fixed";
		element.style.top = (wHeight - favHeight) + "px";
		element.style.height = wHeight + "px";
	} else {
		// otherwise fix fav to the top of the viewport
		element.style.position = "fixed";
		element.style.top = "0px";
		element.style.height = wHeight + "px";
	}
};

DenkbaresSkin.cleanTrail = function() {
	return;
	var breadcrumbs = jq$('.breadcrumbs');
	if (breadcrumbs.length == 0)
		return;
	for ( var k = 0; k < breadcrumbs.length; k++) {
		var crumbs = breadcrumbs[k].find('a.wikipage');
		if (crumbs.length == 0)
			continue;
		var crumbsCheck = new Object();
		var removeBecauseLeadingComma = false;
		// remove duplicate entries
		for ( var i = crumbs.length - 1; i >= 0; i--) {
			var crumb = crumbs[i];
			var crumbText = jq$(crumb).text();
			var existingEntry = crumbsCheck[crumbText];
			if (typeof existingEntry == "undefined") {
				crumbsCheck[crumbText] = i;
			} else {
				jq$(crumb).remove();
				if (i == 0)
					removeBecauseLeadingComma = true;
			}
		}
		// remove superfluous commas
		var lastNodeText = "";
		for ( var i = 0; i < breadcrumbs[k].childNodes.length; i++) {
			var childNode = breadcrumbs[k].childNodes[i];
			var tempValue = childNode.nodeValue;
			if ((lastNodeText == ", " || removeBecauseLeadingComma == true)
					&& tempValue == ", ") {
				childNode.nodeValue = "";
				removeBecauseLeadingComma = false;
			}
			lastNodeText = tempValue;

		}
		var text = jq$(breadcrumbs.first());
	}
}

jq$(window).ready(function() {
	DenkbaresSkin.cleanTrail();
});

window.onresize = DenkbaresSkin.checkFavScroll;
window.onscroll = DenkbaresSkin.checkFavScroll;