/*
 * Copyright (C) 2014 denkbares GmbH, Germany
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

if (typeof KNOWWE == "undefined" || !KNOWWE) {
    /**
     * The KNOWWE global namespace object. If KNOWWE is already defined, the
     * existing KNOWWE object will not be overwritten so that defined namespaces
     * are preserved.
     */
    var KNOWWE = {};
}
if (typeof KNOWWE.plugin == "undefined" || !KNOWWE.plugin) {
    /**
     * The KNOWWE.plugin global namespace object. If KNOWWE.plugin is already
     * defined, the existing KNOWWE.plugin object will not be overwritten so
     * that defined namespaces are preserved.
     */
    KNOWWE.plugin = function () {
        return {}
    }
}
/**
 * Namespace: KNOWWE.plugin.termbrowserautocompletion The KNOWWE plugin termbrowser autocompletion namespace.
 * Initialized empty to ensure existence.
 */
KNOWWE.plugin.termbrowserautocompletion = function () {
    return {}
}();

/**
 * Namespace: KNOWWE.plugin.semanticservicecoreautocompletion.actions some actions of the semantic autocompletion
 * plugin for KNOWWE.
 */

KNOWWE.plugin.termbrowserautocompletion.instances = new Object();

KNOWWE.plugin.termbrowserautocompletion.actions = function () {
    return {

        /**
         * Function: init some function that are executed on page load.
         */
        init: function () {
            // add textboxlist to semantic autocompletion input fields
            if (jq$('.termbrowserautocompletion').length != 0) {
                jq$('.termbrowserautocompletion').each(function (index, element) {
                    KNOWWE.plugin.termbrowserautocompletion.actions.initSemanticAutocompletion(element);
                });
            }


            // insert concepts append to url
            var textBoxList = null;
            if (jq$('.termbrowserautocompletion').length != 0) {
                jq$('.termbrowserautocompletion').each(function (index, element) {
                    textBoxList = KNOWWE.plugin.termbrowserautocompletion.actions.getTextboxListInstance(element);
                });
            }
            if (textBoxList) {
                var urlParameter = window.location.search;
                var params = urlParameter.split("&");
                for (var i = 1; i < params.length; i++) {
                    // create items and insert into search slot
                    var decodedUri = decodeURIComponent(params[i]);
                    textBoxList.add(decodedUri, decodedUri, decodedUri, null);
                }
                if (params.length > 0) {
                    // init actual search event
                    //KNOWWE.plugin.semanticservicecore.searchConceptAdded();
                }
            }
        },

        initSemanticAutocompletion: function (element) {
            var params = {
                action: 'SemanticCompleterAction'
            }
            var actionUrl = KNOWWE.core.util.getURL(params);
            var masterArticle = jq$(jq$(element).siblings('.semanticautocompletionmaster')[0]).html();
            var options = {
                unique: true,
                plugins: {
                    autocomplete: {
                        method: 'serverside',
                        onlyFromValues: true,
                        minLength: 2,
                        maxResults: 99,
                        queryRemote: true,
                        remote: {
                            url: actionUrl,
                            extraParams: {
								master: masterArticle,
								SectionID: masterArticle
							}
                        }
                    }
                }
            };
            var tbl = new jq$.TextboxList(element, options);
            KNOWWE.plugin.termbrowserautocompletion.instances[jq$(element).attr('id')] = tbl;
        },

        getTextboxListInstance: function (element) {
            return KNOWWE.plugin.termbrowserautocompletion.instances[jq$(element).attr('id')];
        },

        copyToClipboard: function (sectionId) {
            var concepts = jq$("#" + sectionId).find(".textboxlist-bits .editableconcept");
            var textToCopy = "";
            jq$.each(concepts, function (index, value) {
                textToCopy += jq$(value).data("conceptid");
                textToCopy += "\t";
            });
            prompt("Copy to clipboard: Ctrl+C, Enter", textToCopy.trim());
        }

    }
}();

jq$.TextboxList.Autocomplete.Methods.serverside = {
    filter: function (values, search, insensitive, max) {
        return values;
    },

    highlight: function (element, search, insensitive, klass) {
        var tokens = search.split(/\s+/);
        var pattern = "";
        jq$.each(tokens, function (index, token) {
            pattern += token.replace(/([-.*+?^${}()|[\]\/\\])/g, "\\$1");
            pattern += "|";
        });
        pattern = pattern.substring(0, pattern.length - 1);
        var regex = new RegExp('(<[^>]*>)|(' + pattern + ')',
            insensitive ? 'ig' : 'g');
        return element.html(element.html().replace(
            regex,
            function (a, b, c) {
                return (a.charAt(0) == '<') ? a : '<strong class="' + klass
                    + '">' + c + '</strong>';
            }));
    }
};

/*
 * init semantic autocompletion slot
 */
KNOWWE.plugin.termbrowserautocompletion.initSemanticAutocompletionSlot = function() {
	window.setTimeout(function() {
		jq$("#sscSearchSlot").each(function() {
			var termbrowserframeElement = jq$(this);
			var inputElement = termbrowserframeElement.find(".termbrowserautocompletion");
			var textboxListObject = KNOWWE.plugin.termbrowserautocompletion.actions.getTextboxListInstance(inputElement);
			if (textboxListObject) {
				textboxListObject.addEvent('bitAdd', KNOWWE.plugin.termbrowserautocompletion.searchConceptAdded);
				//textboxListObject.addEvent('enter', KNOWWE.plugin.termbrowserautocompletion.searchConceptAdded);
			}
		});
	});
};

/*
 * when a concept has been entered into the search slot, a search request is sent to server
 */
KNOWWE.plugin.termbrowserautocompletion.searchConceptAdded = function(/*bitObject*/) {
	var master;
	jq$("#sscSearchSlot").each(function() {
		var searchSlot = jq$(this);
		var masterElement = searchSlot.find(".semanticautocompletionmaster");
		master = masterElement.html();
		var allBoxes = jq$(".textboxlist-bit-box");
		var allConceptInBox = searchSlot.find(allBoxes);
		var uris = new Array(allConceptInBox.length);
		var types = new Array(allConceptInBox.length);
		var labels = new Array(allConceptInBox.length);
		for (var int = 0; int < allConceptInBox.length; int++) {
			var box = jq$(allConceptInBox[int]);
			var concept = box.find(".editableconcept");
			var uri = jq$(concept).attr("id");
			var type = jq$(concept).attr("data-conceptclass");
			if (!uri) {
				uri = box.text();
			}
			uris[int] = uri;
			types[int] = type;
			labels[int] =  box.find(".label").text();
		}
		//KNOWWE.plugin.semanticservicecore.lastSearchURIs = uris;
		//KNOWWE.plugin.semanticservicecore.sendSearchQuery(uris, master);
		sendTermBrowserAction(uris[0], 'searched', types[0], labels[0]);
	});

};

window.addEvent('domready', function() {
	KNOWWE.plugin.termbrowserautocompletion.initSemanticAutocompletionSlot();
});

(function init() {
    if (KNOWWE.helper.loadCheck([ 'Wiki.jsp' ])) {
        window.addEvent('domready', function () {

            var ns = KNOWWE.plugin.termbrowserautocompletion;
            for (var i in ns) {
                if (ns[i].init) {
                    ns[i].init();
                }
            }

        });
    }
}());