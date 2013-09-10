/**********************************************************
* This script provides the javascript functionality for the 
* KnowWE-Plugin-OntE. The functionality contains for example 
* a small handy syntax editor for the Manchester OWL syntax 
* format.
* Manchester OWL syntax format specification: 
*  http://www.w3.org/2007/OWL/wiki/ManchesterSyntax
***********************************************************/

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
     * The KNOWWE.plugin namespace object. If KNOWWE.plugin is already defined, the
     * existing KNOWWE.plugin object will not be overwritten so that defined namespaces
     * are preserved.
     */
    KNOWWE.plugin = {};
}

/**
 * Namespace: KNOWWE.plugin.onte
 */
KNOWWE.plugin.onte = function() {
    
    var editor = null; // contains the CodeMirror Editor
    var olay = null; // the current overlay containing the editor
    
    return {
        /**
         * Initializes the CodeMirror javascript editor.
         * @param String id - The id of the textarea, the editor uses.  
         */
        initEditor : function(id) {
            
            var textarea = document.createElement('textarea');
            textarea.id = "OntEEditor";
            textarea.name = "OntEEditor";
            
            document.getElementById(id).appendChild(textarea);
            
            editor = CodeMirror.fromTextArea(document.getElementById('OntEEditor'), {
                lineNumbers: true,
                enterMode: "indent",
                matchBrackets : true,
                theme: "manchester"
            });
        },
        popEditor : function(title) {
            
            olay = new KNOWWE.helper.modal({
                callback: function(){KNOWWE.plugin.onte.initEditor('onte_body');}
            });
            olay.show();
        },
        /**
         * When the user selects an option in the options filed of the query tab,
         * only the selected options are visible in the result box. The other options
         * are set to invisible.
         * 
         * @param {Object} Javascript event object, contains the clicked element
         */
        onChangeOptions : function(e) {
            var target = _KE.target(e);
            if(target.name){
                var option = document.getElementById(target.name);
                if(target.checked) {
                    option.style.visibility = 'visible';
                    option.style.display = 'block';
                } else {
                    option.style.visibility = 'hidden';
                    option.style.display = 'none';
                }
            }
        },
        /**
         * Searches within a given element for checkboxes and returns the name values
         * of the checked ones as array.
         * 
         * @param {String} The name of the father element.
         */
        getSelectedOptions : function(id) {
            var father = document.getElementById(id);
            var names = [];
            if(father) {
                var inputs = father.getElementsByTagName('input');
                for(var i = 0; i < inputs.length; i++){
                    var c = inputs[i]; 
                    if(c.type == 'checkbox' && c.checked) {
                        names.push(c.name);
                    }
                }
            }
            return names.join("::");
        },
        enterKeyPressed : function(e) {
            e = e || window.event;
            e.cancelBubble = true;
            if (e.stopPropagation) e.stopPropagation();
            var node = e.target || e.srcElement ;
            
            if(e.keyCode == 13) {
                return true;    
            }
            return false;           
        }
    }
}();

/**
 * Namespace: KNOWWE.plugin.onte.actions
 */
KNOWWE.plugin.onte.actions = function() {
    
    var olay = null; // the current overlay for the actions
    
    function showDefaultTab(givenOptions) {
        
    }
    
    
    return {
        /**
         * Checks the consistency of the ontology and displays the results
         */
        checkConsistency : function() {
            olay = new KNOWWE.helper.modal({
                top : '10%',
                left: '25%',
                width: '50%',
                callback: function(){
                    var options = {
                        url : KNOWWE.core.util.getURL( { action : 'OWLApiCheckConsistency' } ),
                        loader : true,
                        response : {
                            action : 'insert',
                            ids : ['onte_body'],
                            fn : function(){
                                var _o = olay;
                                _o.show();
                            }
                        }
                    }
                    new _KA( options ).send();
                }
            });
        },
        /**
         * Performs correction for a given Section ID and correction
         */
        doCorrection : function(sectionID, correction) {
            var params = {
                    action : 'KeywordReplaceAction',
                    TargetNamespace :  sectionID,
                    KWiki_Topic : KNOWWE.helper.gup('page'),
                    KWikitext : encodeURIComponent(correction.replace(/\s*$/im,""))
            };

            var options = {
                    url : KNOWWE.core.util.getURL(params),
                    loader : true,
                    response : {
                        action : 'none',
                        fn : function() { 
                            window.location.reload();
                        },
                        onError : function(http) {
                            KNOWWE.helper.message.showMessage(http.responseText, "AJAX call failed");
                        }
                    }
            };
            new _KA(options).send();  
        },
        exportOntology : function() {
            var s = document.getElementById('onte-export-tab-format').value;
            var f = document.getElementById('onte-export-tab-filename').value;
            
            var params = { 
                action : 'OWLApiOntologyExport',
                format : s,
                filename : f
            };
            
            var inputs = [];
            for( key in params ) {
                var value = params[key] ; 
                inputs.push('<input type="hidden" name="'+ key +'" value="'+ value +'" />');
            }
            var form = document.createElement('form');
            form.action = 'KnowWE.jsp';
            form.method = 'post';
            form.innerHTML = inputs.join("");
            
            var frame = document.getElementById('onte-export-tab');
            frame.appendChild(form);
            form.submit();
            frame.removeChild(form);
        },
        removeImportedOntology : function(sectionID) {
            var options = {
                url : KNOWWE.core.util.getURL( { 
                    action : 'OnteRemoveImportedOntologyAction',
                    section : sectionID
                }),
                loader : true,
                response : {
                    action : 'none',
                    fn : function() {
                        olay.destroy();
                    },
                    onError : function(http) {
                        KNOWWE.helper.message.showMessage(http.responseText, "AJAX call failed");
                    }
                }
            }
            new _KA( options ).send();
        },        
        repairOntology : function() {
            var options = {
                url : KNOWWE.core.util.getURL( { 
                    action : 'OWLApiRepairInconsistencies',
                    options : KNOWWE.plugin.onte.getSelectedOptions('onte_body')
                }),
                loader : true,
                response : {
                    action : 'none',
                    fn : function() {
                        olay.destroy();                       
                        KNOWWE.plugin.onte.actions.checkConsistency();
                    },
                    onError : function(http) {
                        KNOWWE.helper.message.showMessage(http.responseText, "AJAX call failed");
                    }
                }
            }
            new _KA( options ).send();
        },
        query : function() {
            var q = document.getElementById('onte-query-tab-input').value;
            var options = {
                url : KNOWWE.core.util.getURL( { 
                    action : 'OWLApiClassExpressionQueryAction',
                    query : q,
                    options : KNOWWE.plugin.onte.getSelectedOptions('onte-options')
                }),
                loader : true,
                response : {
                    action : 'insert',
                    ids : ['onte-query-tab-results-content'],
                    fn : function(){
                        _KE.removeEvents('onte-options');
                        _KE.add('click', document.getElementById('onte-options'), KNOWWE.plugin.onte.onChangeOptions);
                    }
                }
            }
            new _KA( options ).send();
        },
        showExportTab : function(){
            olay = new KNOWWE.helper.modal({
                top : '30%',
                left: '35%',
                width: '30%',
                callback: function(){
                    var options = {
                        url : KNOWWE.core.util.getURL( { action : 'ShowExportTabAction' } ),
                        loader : true,
                        response : {
                            action : 'insert',
                            ids : ['onte_body'],
                            fn : function(){
                                var _o = olay;
                                _o.show();
                            }
                        }
                    }
                    new _KA( options ).send();
                }
            });
        },
        showImportTab : function() {
            olay = new KNOWWE.helper.modal({
                top : '30%',
                left: '25%',
                width: '50%',
                callback: function(){
                    var options = {
                        url : KNOWWE.core.util.getURL( { action : 'ShowImportTabAction' } ),
                        loader : true,
                        response : {
                            action : 'insert',
                            ids : ['onte_body'],
                            fn : function(){
                                var _o = olay;
                                _o.show();
                            }
                        }
                    }
                    new _KA( options ).send();
                }
            });         
        },        
        showQueryTab : function() {
            olay = new KNOWWE.helper.modal({
                top : '10%',
                left : '24%',
                width : '50%'
            });
            
            var tab = document.createElement("div");
            tab.id = "onte-query-tab";
            tab.className = "onte-box";
            tab.innerHTML = "<strong>Query</strong>"
                        + " <div> "
                        + "     <textarea cols='60' rows='5' title='Query' id='onte-query-tab-input' class='onte-textarea-field' value='' autocorrect='off' autocomplete='off'></textarea>"
                        + " </div>  "
                        + " <div class='onte-buttons onte-buttonbar'>"
                        + "     <a href='javascript:KNOWWE.plugin.onte.actions.query();void(0);' title='Execute' class='left onte-button-txt'>Execute</a>"
                        + " </div>"
                        + " <div style='clear:both;padding-top:10px;'></div>"
                        + "     <div class='left' id='onte-query-tab-results' style='width:65%;height:100%'>"
                        + "         <strong>Query results</strong>"
                        + "         <div id='onte-query-tab-results-content'></div>"
                        + "     </div>"
                        + "     <div id='onte-options' class='onte-options right' style='width:25%;'>"
                        + "         <label class='option'>"
                        + "             <input type='checkbox' value='yes' name='onte-option-individuals'>"
                        + "             <strong class='onte-option-label'>Individuals</strong>"
                        + "         </label>"
                        + "         <label class='option'>"
                        + "             <input type='checkbox' value='yes' name='onte-option-subclasses'>"
                        + "             <strong class='onte-option-label'>Subclasses</strong>"
                        + "         </label>            "
                        + "         <label class='option'>"
                        + "             <input type='checkbox' value='yes' name='onte-option-equivalentclasses'>"
                        + "             <strong class='onte-option-label'>Equivalent classes</strong>"
                        + "         </label>"
                        + "         <label class='option'>"
                        + "             <input type='checkbox' value='yes' name='onte-option-superclasses'>"
                        + "             <strong class='onte-option-label'>Super classes</strong>"
                        + "         </label>            "
                        + "     </div>";    
            olay.setContent(tab);
            olay.show();
        },
        showUndefinedTermsTab : function() {
            olay = new KNOWWE.helper.modal({
                top : '30%',
                left: '25%',
                width: '50%',
                callback: function(){
                    var options = {
                        url : KNOWWE.core.util.getURL( { action : 'ShowUndefinedTermsTabAction' } ),
                        loader : true,
                        response : {
                            action : 'insert',
                            ids : ['onte_body'],
                            fn : function(){
                                var _o = olay;
                                _o.show();
                            }
                        }
                    }
                    new _KA( options ).send();
                }
            });         
        },
        showValidationTab : function() {
            olay = new KNOWWE.helper.modal({
                top : '30%',
                left: '25%',
                width: '50%',
                callback: function(){
                    var options = {
                        url : KNOWWE.core.util.getURL( { action : 'ShowValidationTabAction' } ),
                        loader : true,
                        response : {
                            action : 'insert',
                            ids : ['onte_body'],
                            fn : function(){
                                var _o = olay;
                                _o.show();
                            }
                        }
                    }
                    new _KA( options ).send();
                }
            });  
        },
        validateOWL2Profile : function() {
            
            var selectedProfile = document.getElementById('onte-validation-tab-format');
            if(selectedProfile) {
                selectedProfile = selectedProfile.options[selectedProfile.selectedIndex].value;
                var options = {
                    url : KNOWWE.core.util.getURL( { action : 'OWL2ProfileValidationTabAction', profile : selectedProfile} ),
                    loader : true,
                    response : {
                        action : 'insert',
                        ids : ['onte-result'],
                        onError : function(http) {
                            KNOWWE.helper.message.showMessage(http.responseText, "Error in AJAX call!");
                        }
                    }
                };
                new _KA( options ).send();
            }  
        }         
    }
}();

KNOWWE.plugin.onte.browser = function() {
    
    var browserTabs = null;
    var trees = [];
    var olay = null;
    
    return {
        /**
         * Initializes the browser. E.g. loads the trees per AJAX from the server
         * and displays the result. Also initializes the tabs view correctly.
         */
        init : function() {
           if(document.getElementById('onte-ontology-browser')) {
            
               function handleTreeResponse() {
                    var nodes = JSON.parse(this.responseText);
                    trees[tabs[i].tree] = new KNOWWE.helper.treeview({
                        'nodes' : nodes,
                        'id' : tabs[i].dom,
                        'onNodeClick' : KNOWWE.plugin.onte.browser.getEntityInformation,
                        'toolbarID' : 'oob-buttons'
                    });
               }
               
               function handleIndividualResponse() {
                   var nodes = JSON.parse(this.responseText);
               }
            
               var tabs = [
                    {'tree' : 'OWLClass', 'dom' : 'oob-class-hierarchy', 'fn': handleTreeResponse},
                    {'tree' : 'OWLObjectProperty', 'dom' : 'oob-object-hierarchy', 'fn': handleTreeResponse},
                    {'tree' : 'OWLDataProperty', 'dom' : 'oob-data-hierarchy', 'fn': handleTreeResponse},
                    {'tree' : 'OWLIndividual', 'dom' : 'oob-individual-hierarchy', 'fn' : handleTreeResponse}
               ];
            
                for(var i = 0, l = tabs.length; i < l; i++) {
                    var options = {
                        url : KNOWWE.core.util.getURL( { action : 'GetOntologyHierarchyAction', 'classification' : tabs[i].tree } ),
                        loader : false,
                        async : false,
                        response : {
                            action : 'none',
                            fn : tabs[i].fn
                        }
                    };
                    new _KA( options ).send();
                }
                
                // ... initialize the classification tabs
                browserTabs = new KNOWWE.helper.simpletabs({'onTabChange' : function() {document.getElementById('oob-information').innerHTML = '';}});
                browserTabs.init();
                
                _KE.add('click', document.getElementById('oob-information'), KNOWWE.plugin.onte.browser.linkConcepts);
                _KE.add('click', document.getElementById('oob-search'), KNOWWE.plugin.onte.browser.showSearchEntity);
            }           
        },
        /**
         * Loads the information stored in the ontology when clicking on an element
         * in a hierarchy.
         */
        getEntityInformation : function(e) {
            e = e || window.event;
            e.cancelBubble = true;
            if (e.stopPropagation) e.stopPropagation();
        
            // handle event
            var liParentElement = e.target || e.srcElement ;
            while(liParentElement.nodeName.toLowerCase() != 'li') {
                liParentElement = liParentElement.parentNode;
            }       
                
            //determine the clicked conceptName
            var conceptName = null;
            for(var i = 0, l = liParentElement.childNodes.length; i < l; i++){
                if(liParentElement.childNodes[i].nodeName.toLowerCase() == "span") {
                    conceptName = liParentElement.childNodes[i];
                }           
            }
            
            if(e.target.nodeName.toLowerCase() == 'span') {
                conceptName.className = 'oob-clicked';
                var conceptType = conceptName.getAttribute('data-info');
                var options = {
                    url : KNOWWE.core.util.getURL( { 'action' : 'GetEntityInformationAction', 'entity' : conceptName.innerHTML, 'type' : conceptType } ),
                    loader : true,
                        response : {
                            action : 'insert',
                            ids : ['oob-information']
                    }
                };
                new _KA( options ).send();
            }
        }, 
        linkConcepts : function(e) {
            e = e || window.event;
            
            var elem = e.target || e.srcElement ;
            
            if(elem.nodeName.toLowerCase() == "span") {
                var treeIndex = elem.getAttribute('data-type');
                
                // ... goto correct tab
                var tab = browserTabs.getTabForName(treeIndex);
                tab.click();
                
                // ... show the concept within the tree;
                trees[treeIndex].link(elem.innerHTML.replace(/^\s+|\s+$/g, '')); 
                //alert(elem.innerHTML);
            }
        },
        searchEntity : function() {
            var term = document.getElementById('oob-search-tab-term').value;
            if(term) {
                term = term.replace(/^\s+|\s+$/g, '');
                
                for(i in trees) {
                    if(typeof(trees[i]) != 'function') {
                        if(trees[i].containsNode(term)) {
                            
                            var tab = browserTabs.getTabForName(i);
                            tab.click();
        
                            trees[i].link(term)
                            olay.destroy(); 
                        }
                    }
                }
            }
        },
        showSearchEntity : function() {
            // tree-search
            olay = new KNOWWE.helper.modal({
                top : '30%',
                left : '30%',
                width : '30%'
            });          
            
            var tab = document.createElement("div");
            tab.id = "oob-search-tab";
            tab.className = "onte-box";
            tab.innerHTML = "<p><strong>Find Entity within the Ontology:</strong></p>"
                            + "<div id='onte-options' class='onte-options'>"
                            + "    <label class='option'>"
                            + "        <p class='onte-option-label' style='float:left; display: block; width:100px;'>Entity identifier:</p>"
                            + "        <input type='text' value='' size='20' name='oob-search-tab-term' id='oob-search-tab-term'>"
                            + "    </label>"
                            + "</div>"
                            + " <div class='onte-buttons onte-buttonbar'>"
                            + "     <a href='javascript:KNOWWE.plugin.onte.browser.searchEntity();void(0);' title='Find' class='left onte-button-txt'>Find</a>"
                            + " </div>"
            olay.setContent(tab);
            olay.show();
            // set focus and register return listener
            var input = document.getElementById('oob-search-tab-term');
            input.focus();
            _KE.add('keyup', input, function(e) {
                if(KNOWWE.plugin.onte.enterKeyPressed(e)) {
                    KNOWWE.plugin.onte.browser.searchEntity();
                }
            });
        }
    }
}();


/**
 * A simple modal window to display popup content.
 * 
 * @author Stefan Mark
 */
KNOWWE.helper.modal = (function(){
    
    function Modal(givenOptions) {
        var options = {}, defaults = Modal.defaults;
        for (var opt in defaults) {
            if (defaults.hasOwnProperty(opt)) {
                options[opt] = (givenOptions && givenOptions.hasOwnProperty(opt) ? givenOptions : defaults)[opt];
            }
        }
        
        // append modal window HTML template
        var wrapper = document.createElement("div");
        wrapper.id = "onte_wrapper";
        wrapper.className = "onte_wrapper";
        wrapper.style.visibility = "hidden";
        wrapper.style.display = "none";
        wrapper.innerHTML =
          '<div id="onte_overlay"></div>' + // the greyed box to hide the background
            '<div id="onte_modal" class="onte_modal_rounded" style="width:'+options.width+';top:'+options.top+';left:'+options.left+'">'+
                '<div id="onte_header" style="background-color: #FFFFFF;"><span>'+options.title+'</span><span id="onte_modal_close" style="float:right;padding-right:20px;">close</span></div>'+
                '<div id="onte_body" style="background-color: #FFFFFF;padding:20px;">'+
                '</div>'+
                '<div id="onte_footer" style="background-color: #FFFFFF;padding-top:30px;"></div>'+
           '</div>';
        document.getElementsByTagName('body')[0].appendChild(wrapper);
        
        // destroyes the modal window
        this.destroy = function() {
            _KE.removeEvents(document.getElementById('onte_modal_close'));
            var body = document.getElementsByTagName('body')[0];            
            body.removeChild(document.getElementById(wrapper.id));
        }        
        
        // shows the modal window
        this.show = function() {
            wrapper.style.display = 'block';
            wrapper.style.visibility = 'visible';
            _KE.add('click', document.getElementById('onte_modal_close'), this.destroy);
        }
        
        //set content of the window
        this.setContent = function(element) {
            if(element) {
                document.getElementById('onte_body').appendChild(element);
            }
        }
        
        // ... before returning an instance, check for a callbak function and call
        if(options.callback) {
            options.callback.call();
        }
        return this;
    }
    
    // the default configuration settings.
    Modal.defaults = {
        width : '75%',       // Width of window
        top : '28px',        // Set as an Integer to be spaced from the Top
        left : '10%',        // Set as an Integer to be spaced from the Left
        title : '',             //the title of the window
        loading_animation : false,  // Set to false to opt to not fade out the Loading Cover
        form : '',           // The name of the form
        id : 'onte_modal',
        overlay : 'onte_overlay',     
        callback : null
        
    };
        
    return Modal;
})();

/**
 * A simple tab implementation. Please use the following markup in your HTML output
 * to use the SimpleTab. The value of the IDs are example values.
 * 
 * <code>
 * <div id="tab" class="simpletab">
 *   <ul id="tabs" class="simpletabs">
 *       <li data-simpletab="first">First tab</li>
 *       <li data-simpletab="second">Second tab</li>
 *   </ul>
 *   <div id="first">
 *       Content of the first tab
 *   </div>
 *   
 *   <div id="second">
 *      Content of the second tab
 *
 *   </div>
 * </code>
 * TODO: extend to multi tab?
 * @author Stefan Mark
 */
KNOWWE.helper.simpletabs = (function(){
    function SimpleTabs(givenOptions) {
        var options = {}, defaults = SimpleTabs.defaults;
        var activeTab = null;
        var tabs = null;
        for (var opt in defaults) {
            if (defaults.hasOwnProperty(opt)) {
                options[opt] = (givenOptions && givenOptions.hasOwnProperty(opt) ? givenOptions : defaults)[opt];
            }
        }       
        
        /* helper functions, only visible inside the SimpleTabs object */
        
        /**
         * Initializes a found SimpleTab. E.g. sets the visible state usw.
         */
        function initSimpleTabMenu(menu) {      
            // First find the correct menu indicator (options.simpleMenuClass)
            var thisSimpleTab = null;
            var simpleTabsMenu = menu.getElementsByTagName('ul');
            for(var i = 0, l = simpleTabsMenu.length; i < l; i++){
                if(css('check', simpleTabsMenu[i], options.simpleMenuClass)){
                    thisSimpleTab = simpleTabsMenu[i];
                }
            }
            if(!thisSimpleTab){ return; }
            var simpleTabsList = thisSimpleTab.getElementsByTagName('li');
            

            for(var i = 0, l = simpleTabsList.length; i < l; i++){
                // TODO: in HTML5 simpleTabsList[i].dataset['simpletab'];
                var id = simpleTabsList[i].getAttribute('data-simpletab');
                if(id){
                    setTabState(document.getElementById(id), 0);
                }
            }
            
            var id = simpleTabsList[0].getAttribute('data-simpletab');
            var elem = document.getElementById( id );
            if(elem) {
                setTabState(elem, 1);
                css('add', simpleTabsList[0], options.simpleTabActive);
            }
            activeTab = simpleTabsList[0];
            tabs = simpleTabsList;
            return thisSimpleTab;
        }
        /**
         * John Resig, erklärt auf Flexible Javascript Events
         */
        function addEvent( obj, type, fn ) {
           if (obj.addEventListener) {
              obj.addEventListener( type, fn, false );
           } else if (obj.attachEvent) {
              obj["e"+type+fn] = fn;
              obj[type+fn] = function() { obj["e"+type+fn]( window.event ); }
              obj.attachEvent( "on"+type, obj[type+fn] );
           }
        }

        function removeEvent( obj, type, fn ) {
           if (obj.removeEventListener) {
              obj.removeEventListener( type, fn, false );
           } else if (obj.detachEvent) {
              obj.detachEvent( "on"+type, obj[type+fn] );
              obj[type+fn] = null;
              obj["e"+type+fn] = null;
           }
        }       
        /**
         * Determines the element in the DOM the event occured in.
         */
        function target(e) {
            e = e || window.event;
            return e.target || e.srcElement;
        }
        /**
         * Cancel a javascript events. Prevents bubbling an unforseen sideeffects
         */ 
        function cancelEvent(e) {
            e = e || window.event;
            
            if (e.stopPropagation) { e.stopPropagation(); }
            e.cancelBubble = true;
            e.returnValue = false;
        }
        
        /**
         * Set the visible state of the given tab.
         */
        function setTabState(elem, state) {
            elem.style.display = (state == 0) ? 'none' : 'block';
            elem.style.visibility = (state == 0) ? 'hidden' : 'visible';
        }
        
        /**
         * Checks weather a given CSS classname is attached to an element.
         */
        function css(action, elem, value) {
            switch(action) {
                case 'check' :
                    var classNameArray = elem.className.split(' ');
                    for(var i = 0, l = classNameArray.length; i < l; i++){
                        if(classNameArray[i] == value) {
                            return true;
                        }
                    }
                    return false;   
                    break;
                case 'add' :
                    if(!css('check', elem, value)) {
                        elem.className += elem.className ? ' ' + value : value;
                    }
                    break;
                case 'remove' :
                    var str = elem.className.match(value);
                    elem.className = elem.className.replace(str,'');
                    break;
            }
        }       
        
        /* some privileged methods */
        this.init = function() {
            if(!document.getElementById || !document.createTextNode){return;} // simply do nothing
            
            // get SimpleTabs marked by options.simpleTabClass
            var simpleTabList = document.getElementsByTagName('div');
            for(var i = 0, l = simpleTabList.length; i < l; i++){
                if(css('check', simpleTabList[i], options.simpleTabClass)){
                    var m = initSimpleTabMenu(simpleTabList[i]);
                                
                    // ... add toggle action
                    if( m ) {
                        addEvent(m, 'click', this.showTab);
                    }
                }
            }
            if(options.onInit) {
                options.onInit.call(this);
            }       
        }
        this.showTab = function(e) {
            if(activeTab) {
                var id = activeTab.getAttribute('data-simpletab');
                var old = document.getElementById(id);
                setTabState(old, 0);
                css('remove', activeTab, options.simpleTabActive);
            }
               
            var tab = target(e);
            var id = tab.getAttribute('data-simpletab');
            if(id) {
                setTabState(document.getElementById(id), 1);
                css('add', tab, options.simpleTabActive);
            }
            activeTab = tab;
            
            // execute custom function on tab change if needed
            if(options.onTabChange) {
                options.onTabChange.call();
            }
        }
        this.getTabForName = function(title) {
            for(var i = 0, l = tabs.length; i < l; i++) {
                if(tabs[i].getAttribute('data-type') == title) {
                    return tabs[i];
                }
            }
            return null;
        }       
        
        return this;    
    }
    
    /* the default configuration settings */
    SimpleTabs.defaults = {
        simpleTabClass : 'simpletab',
        simpleMenuClass : 'simpletabs',
        simpleTabActive : 'simpletab-active',
        onInit : null,
        onTabChange : null
    };
    return SimpleTabs;
})();

/**
 * Implements a collapsible TreeView of hierarchy data.
 * 
 * @author Stefan Mark
 */
KNOWWE.helper.treeview = (function(){
    
    function TreeView(givenOptions) {
    
        var options = {}, defaults = TreeView.defaults;
        for (var opt in defaults) {
            if (defaults.hasOwnProperty(opt)) {
                options[opt] = (givenOptions && givenOptions.hasOwnProperty(opt) ? givenOptions : defaults)[opt];
            }
        }
        
        var treeState = null; // the expand | collapse state of the tree
        
        // build up tree for given nodes
        var root = document.getElementById(options.id);
        var tokens = [];
        
        var hasChildren = false;
        if(options.nodes['children']) {
            hasChildren = true;
        }
            
        var li = document.createElement('li');
        li.className = 'last';
        li.innerHTML = '<div class="last_expanded treeicon"></div>\n<span data-info="' + options.nodes['type'] + '">'+options.nodes['name']+'</span>';
            
        if(hasChildren) {
            var ul = createSubTreeDOMNodeTemplate(options.nodes['children'].length);
            li.appendChild(ul);
            buildUpDOMTree(options.nodes['children'], ul);
        }
        
        var rootUL = document.createElement('ul');
        rootUL.className = 'treeview ';
        
        if(options.nodes['name']) {
            rootUL.appendChild(li);
        }    
        
        // ... handle optional roots, sometimes possibles
        
        if(options.nodes['size'] > 0) {
            for(var i = 0, l = options.nodes['size']; i < l; i++) {
                var optionalRoot = options.nodes['optionalRoot'+i];
                var liOptional = document.createElement('li');
                liOptional.className = 'last';
                liOptional.innerHTML = '<div class="last_expanded treeicon"></div>\n<span data-info="' + optionalRoot['type'] + '">'+optionalRoot['name']+'</span>';
            
               if(optionalRoot['children']) {
            
                   var ulOptional = createSubTreeDOMNodeTemplate(optionalRoot['children'].length);
                   liOptional.appendChild(ulOptional);
                   buildUpDOMTree(optionalRoot['children'], ulOptional);
               }
                rootUL.appendChild(liOptional);
            }
        }
        
//        if(options.nodes['optionalRoot']) {
//            var optionalRoot = options.nodes['optionalRoot'];
//            var liOptional = document.createElement('li');
//            liOptional.className = 'last';
//            liOptional.innerHTML = '<div class="last_expanded treeicon"></div>\n<span data-info="' + optionalRoot['type'] + '">'+optionalRoot['name']+'</span>';
//            
//            var ulOptional = createSubTreeDOMNodeTemplate(optionalRoot['children'].length);
//            liOptional.appendChild(ulOptional);
//            buildUpDOMTree(optionalRoot['children'], ulOptional);
//            rootUL.appendChild(liOptional);         
//        }
        
                        
        // create header
        if(options.toolbarID) {
             //bttn-names: tree-expand, tree-collapse
             addEvent(document.getElementById(options.toolbarID), 'click', toogleTree);
        }
           
        root.appendChild(rootUL);
        /**
         * @param children The amount of children
         */
        function createSubTreeDOMNodeTemplate(children){
            var ul = document.createElement('ul');
            for(var i = 0; i < children; i++){
                ul.appendChild(document.createElement('li'));
            }
            return ul;
        }
        /**
         * @param children The children nodes
         * @param father The father node;
         */     
        function buildUpDOMTree(children, father){
            var l = children.length;
            for(var i = 0; i < l; i++){
                var name = children[i]['name'];
                var type = children[i]['type'];
                var color = children[i]['color'];
                var hasChildren = children[i]['children'];
                var clsName = (hasChildren) ? options.initState + ' treeicon' : 'leaf treeicon';
                
                if(i + 1 == l) { // last node, need to set correct li class
                    father.childNodes[i].className = 'last';
                    clsName = (hasChildren) ? 'last_'+options.initState+' treeicon' : 'last_leaf treeicon';
                }
                father.childNodes[i].innerHTML = '<div class="' + clsName + '"></div>'
                    + '<span data-info="' + type + '" style="color:' + ((color) ? color : '') + '">' + name + '</span>';
                    
                if(hasChildren) {
                    var ul = createSubTreeDOMNodeTemplate(children[i]['children'].length);
                    if(options.initState == 'closed') {
                        //ul.className = 'noShow';
                        ul.style.display = 'none';
                        ul.style.visibility = 'hidden';
                    }
                    else {
                        //ul.className = 'show';
                        ul.style.display = 'block';
                        ul.style.visibility = 'visible';
                    }
                    father.childNodes[i].appendChild(ul);                   
                    buildUpDOMTree(children[i]['children'], ul);
                }
            }
        }
        
        function toogleTree(e) {
            e = e || window.event;
            e.cancelBubble = true;
            if (e.stopPropagation) e.stopPropagation();
            var t = e.target || e.srcElement ;
            
            
            var root = document.getElementById(options.id);
            var ulList = root.getElementsByTagName("ul");
            var iconList = root.getElementsByTagName("div");
            
            if(t.className.indexOf("tree-collapse") != -1) {
                for(var i = 1, l = ulList.length; i < l; i++) {
                    ulList[i].style.display = 'block';
                    ulList[i].style.visibility = 'visible';
                }
                
                for(var i = 1, l = iconList.length; i < l; i++) {
                    if(iconList[i].className.indexOf('closed')) {
                        iconList[i].className = iconList[i].className.replace(/closed/, "expanded");
                    }
                }
            }
            else if(t.className.indexOf("tree-expand") != -1){
                // always show children of root
                for(var i = 1, l = ulList.length; i < l; i++) {
                    ulList[i].style.display = 'none';
                    ulList[i].style.visibility = 'hidden';
                }           
                for(var i = 1, l = iconList.length; i < l; i++) {
                    if(iconList[i].className.indexOf('expanded')) {
                        iconList[i].className = iconList[i].className.replace(/expanded/, "closed");
                    }
                }
            }
        }
        
        function resetHighlightedNode() {
            var root = document.getElementById(options.id);
            var spanList = root.getElementsByTagName("span");
            for(var i = 0, l = spanList.length; i < l; i++) {
                spanList[i].className = '';
            }
        }
        
        function expandAll() {
            var root = document.getElementById(options.id);
            var ulList = root.getElementsByTagName("ul");
            
            for(var i = 0, l = ulList.length; i < l; i++) {
                ulList[i].style.display = 'block';
                ulList[i].style.visibility = 'visible';
            }
            var iconList = root.getElementsByTagName("div");
            for(var i = 0, l = iconList.length; i < l; i++) {
                if(iconList[i].className.indexOf('closed')) {
                    iconList[i].className = iconList[i].className.replace(/closed/, "expanded");
                }
            }
        }
        
        function collapseAll() {
            var root = document.getElementById(options.id);
            var ulList = root.getElementsByTagName("ul");
            
            // always show children of root
            for(var i = 1, l = ulList.length; i < l; i++) {
                ulList[i].style.display = 'none';
                ulList[i].style.visibility = 'hidden';
            }           
            var iconList = root.getElementsByTagName("div");
            for(var i = 1, l = iconList.length; i < l; i++) {
                if(iconList[i].className.indexOf('expanded')) {
                    iconList[i].className = iconList[i].className.replace(/expanded/, "closed");
                }
            }
        }
        
        /**
         * John Resig, erklärt auf Flexible Javascript Events
         */
        function addEvent( obj, type, fn ) {
            if(!obj) { return; }
            
           if (obj.addEventListener) {
              obj.addEventListener( type, fn, false );
           } else if (obj.attachEvent) {
              obj["e"+type+fn] = fn;
              obj[type+fn] = function() { obj["e"+type+fn]( window.event ); }
              obj.attachEvent( "on"+type, obj[type+fn] );
           }
        }

        function removeEvent( obj, type, fn ) {
           if (obj.removeEventListener) {
              obj.removeEventListener( type, fn, false );
           } else if (obj.detachEvent) {
              obj.detachEvent( "on"+type, obj[type+fn] );
              obj[type+fn] = null;
              obj["e"+type+fn] = null;
           }
        }
        
        function fireEvent(element,event) {
           if (document.createEvent) {
               // dispatch for firefox + others
               var evt = document.createEvent("HTMLEvents");
               evt.initEvent(event, true, true ); // event type,bubbling,cancelable
               return !element.dispatchEvent(evt);
           } else {
               // dispatch for IE
               var evt = document.createEventObject();
               return element.fireEvent('on'+event,evt)
           }
        }
        
        // .. initialize the expand and collapse events
        addEvent(root, 'click', this.toogle);
        
        // ... before returning an instance, check for a callbak function and call
        if(options.callback) {
            options.callback.call();
        }
        // ... register onXX functions for dynamic action handling
        if(options.onNodeClick) {
            addEvent(root, 'click', resetHighlightedNode);
            addEvent(root, 'click', options.onNodeClick);
        }
        
        this.options = options;
        return this;
    }
    
    // the default configuration settings.
    TreeView.defaults = {
        id : '', // the id of the dom element that should be converted to a tree
        cls : '',  // a className of the dom element that should be converted to a tree, allows multiple trees on the same page
        nodes : {},
        callback : null,
        initState : 'closed',
        toolbarID : null,
        onNodeClick : null,
        onInit : null
        
    };
    
    // toogle the expand / collapse state of the current branch
    TreeView.prototype.toogle = function(e){
        e = e || window.event;
        e.cancelBubble = true;
        if (e.stopPropagation) e.stopPropagation();
        var t = e.target || e.srcElement ;
        
        if(t.nodeName.toLowerCase() == 'span') {
            return; // do nothing when node name is clicked
        }
        
        // handle event
        var liParentElement = t;
        while(liParentElement.nodeName.toLowerCase() != 'li') {
            liParentElement = liParentElement.parentNode;
        }
        
        //determine icon element and root element of the subtree
        var div = null;
        var ul = null;
        for(var i = 0, l = liParentElement.childNodes.length; i < l; i++){
            if(liParentElement.childNodes[i].nodeName.toLowerCase() == "div") {
                div = liParentElement.childNodes[i];
            }
            if(liParentElement.childNodes[i].nodeName.toLowerCase() == "ul") {
                ul = liParentElement.childNodes[i];
            }           
        }

        if(div.className.indexOf("expanded") != -1) {
            var clsName = "closed treeicon";
            if(div.className.indexOf('last_expanded') != -1) {
                clsName = "last_closed treeicon";
            }
            div.className = clsName;
            //ul.className = "noShow";
            ul.style.display = 'none';
            ul.style.visibility = 'hidden';
        }
        else if(div.className.indexOf("closed") != -1) {
            var clsName = "expanded treeicon";
            if(div.className.indexOf('last_closed') != -1) {
                clsName = "last_expanded treeicon";
            }
            div.className = clsName;
            //ul.className = "show";
            ul.style.display = 'block';
            ul.style.visibility = 'visible';
        }
    }

    // expand till chosen entity
    TreeView.prototype.link = function(entity){
        
        var root = document.getElementById(this.options.id);
        var spanList = root.getElementsByTagName('span');
        var span = null;
        
        entity = entity.toLowerCase();
        
        // ... find correct node
        for(var i = 0, l = spanList.length; i < l; i++) {
            if(spanList[i].innerHTML.toLowerCase() == entity) {
                span = spanList[i];
                span.className = 'oob-clicked';
            }
        }
        
        // .. collapse whole tree
        var ulList = root.getElementsByTagName('ul');
        for(var i = 0, l = ulList.length; i < l; i++) {
            ulList[i].style.display = 'none';
            ulList[i].style.visibility = 'hidden';
        }
        
        // ... expand way up to root and set correct display styles
        var ul = span;      
        do {
            ul = ul.parentNode.parentNode;
            ul.style.display = 'block';
            ul.style.visibility = 'visible';
        } while (ul.nodeName.toLowerCase() == 'ul');
        
        // ... additional information for linked element
        span.click();
    }
    
    TreeView.prototype.containsNode = function( identifier ) {
        var root = document.getElementById(this.options.id);
        var spanList = root.getElementsByTagName('span');
        for(var i = 0, l = spanList.length; i < l; i++) {
            if(spanList[i].innerHTML.toLowerCase() == identifier.toLowerCase()) {
                return true;
            }
        }
        return false;
    }
    
    return TreeView;
})();


/* ############################################################### */
/* ------------- Onload Events  ---------------------------------- */
/* ############################################################### */
(function init(){
    if( KNOWWE.helper.loadCheck( ['Wiki.jsp'] )){
        window.addEvent( 'domready', function(){
           KNOWWE.plugin.onte.browser.init();
        });
    };
}());

/**
 * memory leaks when removing adding events handlers and removing element 
 * without removing event handlers? 
 */