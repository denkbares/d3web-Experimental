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
        }
    }
}();

/**
 * Namespace: KNOWWE.plugin.onte.actions
 */
KNOWWE.plugin.onte.actions = function() {
    
    var olay = null; // the current overlay for the actions
    
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
        }
    }
}();

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
 * memory leaks when removing adding events handlers and removing element 
 * without removing event handlers? 
 */