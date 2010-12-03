/**
 * The KNOWWE global namespace object. If KNOWWE is already defined, the
 * existing KNOWWE object will not be overwritten so that defined namespaces
 * are preserved.
 */
if (typeof KNOWWE == "undefined" || !KNOWWE) {
    var KNOWWE = {};
}

/**
 * The KNOWWE.shared global namespace object. If KNOWWE.shared is already defined, the
 * existing KNOWWE.shared object will not be overwritten so that defined namespaces
 * are preserved.
 */
if (typeof KNOWWE.shared == "undefined" || !KNOWWE.shared) {
        KNOWWE.shared = function(){
             return {  }
        }
}

/**
 * Namespace: KNOWWE.shared.actions
 * The KNOWWE actions namespace object.
 * Contains all actions that can be triggered in KnowWE per javascript.
 */
KNOWWE.shared.actions = function(){
    return {
        /**
         * Function: init
         * Core KnowWE actions.
         */
        init : function(){       
            //init parseAll action
            _KS('.parseAllButton').each(function(element){
                    _KE.add('click', element, KNOWWE.shared.actions.parseAll); 
            });
            
            //init quickedit actions
            var els = _KS('.quickedit');
            for (var i = 0; i < els.length; i++){
                _KE.removeEvents(els[i]);
                if( els[i]._hasClass( 'table' )){
                    _KE.add('click', els[i], function(e){
                        var el = _KE.target(e);
                        var id = el.parentNode.id;
                        KNOWWE.shared.actions.enableQuickEdit( KNOWWE.table.init, id, null );
                    });
                    //Due to problems with refresh, so that table functionality is still guaranteed:
                    KNOWWE.table.init();
                } else if( els[i]._hasClass( 'default') ) {
                    _KE.add('click', els[i], function(e){
                        var el = _KE.target(e);
                        var rel = eval("(" + el.getAttribute('rel') + ")");
                        KNOWWE.shared.actions.enableQuickEdit( KNOWWE.core.edit.init, rel.id, "render" );
                    });
                }
                //check for save button in case the user reloads the page during quick edit
                rel = eval("(" + els[i].getAttribute('rel') + ")");
                if(rel) {
                    bttns = _KS('#'+rel.id + ' input[type=submit]');
                    if( bttns.length != 0 ){
                        _KE.add('click', bttns[0], KNOWWE.core.edit.onSave );
                    }
                }        
            }                
        },
        
        /**
         * Function: parseAll
         * parses all pages
         */  
        parseAll : function(){
            var params = {
                action : 'ParseWebOfflineRenderer',
                KWiki_Topic : KNOWWE.helper.gup('page')
            }   
            
            var options = {
                url : KNOWWE.core.util.getURL( params ),
                response : {
                    action : 'insert',
                    ids : ['parseAllResult']
                }
            }
            new _KA( options ).send();
        },
        /**
         * Function: enableQuickEdit
         * Sets the quick-edit flag to the given element.
         * 
         * Parameters:
         *     fn - The function that should be executed afterwards.
         *     id - The id of the element the quick edit flag should set to.
         */        
        enableQuickEdit : function( fn, id, view){
            var params = {
                action : 'SetQuickEditFlagAction',
                TargetNamespace : id,
                KWiki_Topic : KNOWWE.helper.gup('page'),
                ajaxToHTML : view,
                inPre : KNOWWE.helper.tagParent(_KS('#' + id), 'pre') != document
            }   
            
            var options = {
                url : KNOWWE.core.util.getURL( params ),
                response : {
                    action : 'string',
                    ids : [id],
                    fn : function(){
                        fn.call();
                        Collapsible.render( _KS('#page'), KNOWWE.helper.gup('page'));
                        if(view === "render"){
                            KNOWWE.helper.observer.notify('quick-edit');
                        }
                    }
                }
            }
            new _KA( options ).send();
            if (id.substring(id.length - 13) === 'TestcaseTable') {
                (function() {Testcase.addNewAnswers($(id));}).delay(500);
            }
        }        
    }
}();

/**
 * Namespace: KNOWWE.shared.typebrowser
 * The KNOWWE typebrowser namespace.
 */
KNOWWE.shared.typebrowser = function(){
    return {
        /**
         * Function: init
         * The typebrowser init function. Enables the typebrowser and type 
         * activator buttons.
         */
        init : function(){
            var bttn = _KS('#KnowWEObjectTypeBrowser input[type=button]')[0];
            if( bttn )_KE.add('click', bttn, KNOWWE.shared.typebrowser.searchTypes );
            
            bttn = _KS('#KnowWEObjectTypeActivator input[type=button]')[0];   
            if( bttn ) _KE.add('click', bttn, KNOWWE.shared.typebrowser.switchTypeActivation );
        },
        /**
         * Function: getAdditionalMatchTextTypeBrowser
         * 
         * Gets additional context around the typebrowser finding. Used to view the
         * context in which the type occurs.
         * 
         * Parameters:
         *     atmUrl - An special URL. Used o transport the position of the finding and how many context elements should be displayed.
         *     query - The query string  for the TypeBrowser action
         */
        getAdditionalMatchTextTypeBrowser : function( event ){
            var el = KNOWWE.helper.event.target( event );
            var rel = el.getAttribute('rel');
            if(!rel) return;             
            rel = eval("(" + rel + ")" );

            var id = rel.direction + rel.index;
            var atmUrl = rel.article + ":" + rel.section + ":" + rel.index + ":" + rel.words + ":"
                + rel.direction + ":" + rel.wordCount;
            
            var params = {
                TypeBrowserQuery : rel.queryLength, //queryLength
                action : 'KnowWEObjectTypeBrowserAction',
                ATMUrl :  atmUrl
            };
            var options = {
                url : KNOWWE.core.util.getURL( params ),
                response : {
                    action : 'insert',
                    ids : [ id ]
                }
            }
            var request = new _KA( options );
            request.send();
        },
        /**
         * Function: switchTypeActivation
         * Switches the status of the selected type. 
         * It either enables or disables one type.
         */
        switchTypeActivation : function() {
            if(!_KS('#KnowWEObjectTypeActivator')) return;           
            var params = {
                action : 'KnowWEObjectTypeActivationAction',
                KnowWeObjectType : (function () {
                    var ob = _KS('#KnowWEObjectTypeActivator select')[0];
                    if(ob.selectedIndex){
                        return ob[ob.selectedIndex].value;
                    }
                    return "";
                })()
            }
                
            var options = {
                url : KNOWWE.core.util.getURL ( params ),
                response : {
                    action : 'update',
                    fn : function() {
                            var ob = _KS('#KnowWEObjectTypeActivator select')[0];
                            ob = ob[ob.selectedIndex];
                            if ( ob.style.color == "red") {
                                ob.style.color = "green";
                            } else {
                                ob.style.color = "red";
                            }
                         }
                }
            }
            new _KA( options ).send();
        },
        /**
         * Function: searchTypes
         * Searches for selected type and returns the result that can be viewed
         * in the Wiki page.
         */
        searchTypes : function() {
            if(!document.typebrowser) return;
            var params = {
                action : 'KnowWEObjectTypeBrowserAction',
                TypeBrowserQuery : (function () {
                    var box = document.typebrowser.Auswahl;
                    
                    if(box.selectedIndex){
                        return box.options[box.selectedIndex].value;
                    }
                    return "";
                })()
            }
            var options = {
                url : KNOWWE.core.util.getURL ( params ),
                response : {
                    action : 'insert',
                    ids : ['TypeSearchResult'],
                    fn : function(){
                        _KS('.show-additional-text').each(function(){
                             _KE.add('click', this, KNOWWE.shared.typebrowser.getAdditionalMatchTextTypeBrowser);
                        });
                    }
                }
            }
            new _KA( options ).send();
        }        
    }
}();

/**
 * Namespace: KNOWWE.shared.template
 * The KNOWWE template namespace.
 */
KNOWWE.shared.template = function(){
    return {
        /**
         * Function: init
         * The template init function. Enables the
         * templatetaghandler and templatetaghandler
         * buttons.
         */
        init : function(){
            //init TemplateTagHandler
            if(_KS('#TemplateTagHandler')) {
                var els = _KS('#TemplateTagHandler input[type=button]');
                for (var i = 0; i < els.length; i++){
                    _KE.add('click', els[i], KNOWWE.shared.template.doTemplate); 
                }
            }
            //add generate template button action
            if(_KS('.generate-template').length != 0){
                _KS('.generate-template').each(function( element ){
                    _KE.add('click', element, KNOWWE.shared.template.doTemplate);
                });
            }          
        },
        
        /**
         * Function: doTemplate
         * Creates a new Wikipage out of a templateType.
         * 
         * Parameters:
         *     event - The event from the create knowledgebase button. 
         */
        doTemplate : function( event ) {
            var pageName = eval( "(" + _KE.target(event).getAttribute('rel') + ")").jar;

            var params = {
                action : 'TemplateGenerationAction',
                NewPageName : _KS('#' + pageName ).value,
                TemplateName : pageName
            }
            
            var options = {
                url : KNOWWE.core.util.getURL( params ),
                response : {
                    ids : ['TemplateGeneratingInfo']
                }
            }
            new _KA( options ).send();
        }
    }
}();

/**
 * Namespace: KNOWWE.shared.renaming
 * The KNOWWE renaming tool plugin object.
 */
KNOWWE.shared.renaming = function(){
    var myColumns = [{key:'match'  , label:'Match'   , type:'string' , sortable:'false'},
                {key:'section', label:'Section' , type:'string' , sortable:'true'},
                {key:'replace', label:'Replace?', type:'string' , sortable:'false'},
                {key:'preview', label:'Preview' , type:'string' , sortable:'false'}];
    return {
        /**
         * Function:init
         * The init function for the renaming tool. Enables the preview button.
         */
        init : function(){                    
            var bttn = _KS( '#rename-panel input[type=button]')[0];
            if( bttn ){
                _KE.add('click', bttn, KNOWWE.shared.renaming.preview );
            }
        },
        /**
         * Function: selectPerSelection
         * Select all checkboxes within a certain section.
         * If section has value "undefined", select all checkboxes.
         *
         * Parameters:
         *     e - The occurred event. 
         */
        selectPerSection : function( e ) {
            var renameForm = this.form;
            var el = _KE.target( e );
            var rel = el.getAttribute('rel');
            if(!rel) return;  
                       
            rel = eval("(" + rel + ")");
            var section = rel.section;
            
            var  l = renameForm.length;
            for(i = 0; i < l; i++){
                if(renameForm[i].type == 'checkbox' && renameForm[i].id != ''){
                    if(section == undefined || renameForm[i].id.search(section) != -1)
                        renameForm[i].checked = true;
                }
            }
        },
        /**
         * Function: deselectPerSelection
         * Deselects all chechboxes in the renaming form.  
         * If section has value "undefined", deselect all checkboxes.
         *
         * Parameters:
         *     e - The occurred event. 
         */
         deselectPerSection : function( e ){
            var renameForm = this.form;
            var el = KNOWWE.helper.event.target( e );
            
            var rel = el.getAttribute('rel');
            if(!rel) return;             
            
            rel = eval("(" + rel + ")");
            var section = rel.section;            
            
            var l = renameForm.length;
            for(i = 0; i < l; i++){
                if(renameForm[i].type == 'checkbox' && renameForm[i].id != ''){
                    if(section == undefined || renameForm[i].id.search(section) != -1)
                        renameForm[i].checked = false;
                }
            }
         },
        /**
         * Function: preview
         * Sends an request with the entered values and shows the result of the
         * renaming request in a preview table.
         */
        preview : function(){
            if(!_KS('#rename-panel')) return;
            
            var params = {
                action : 'WordBasedRenamingAction',
                TargetNamespace : _KS('#renameInputField').value,
                KWikiFocusedTerm : _KS('#replaceInputField').value,
                ContextPrevious : _KS('#renamePreviousInputContext').value,
                ContextAfter : _KS('#renameAfterInputContext').value,
                CaseSensitive :_KS('#search-sensitive').checked
            }
            
            var options = {
                url : KNOWWE.core.util.getURL(params),
                //method getSelectedSections() is located in TreeView.js (TreeView.js should be included here eventually)
                data : "SelectedSections=" + JSON.stringify(getSelectedSections()),
                response : {
                    ids : ['rename-result'],
                    fn : function(){
                        if(_KS('.check-select')) {
                             _KS('.check-select').each(function(element){
                                _KE.add('click', element, KNOWWE.shared.renaming.selectPerSection );     
                             });
                        }
                            
                        if(_KS('.check-deselect')){
                            _KS('.check-deselect').each(function(element){
                                _KE.add('click', element, KNOWWE.shared.renaming.deselectPerSection );     
                             });
                        }
                        if(_KS('#renaming-replace'))
                            _KE.add('click', _KS('#renaming-replace'), KNOWWE.shared.renaming.replace );
                        
                        var imgs = _KS('.show-additional-text-renaming');
                        for( var i = imgs.length - 1; i > -1 ; i--){
                            _KE.add('click', imgs[i], KNOWWE.shared.renaming.getAdditionalMatchText);
                        }
                        
                        //init sortable table
                        var tableID = _KS('#rename-result table')[0].id;
                        var tblHeader = _KS('#' + tableID + ' thead')[0].getElementsByTagName('th');
                        for( var i = 0; i < tblHeader.length; i++){
                            if(myColumns[i].sortable == "true"){ 
                                var text = tblHeader[i].innerHTML; 
                                tblHeader[i].innerHTML = '<a href="#" onclick="javascript:KNOWWE.tablesorter.sort(' 
                                    + i + ",'" + tableID + "'" + ');">' + text + '</a>';
                            }
                        }                        
                    }
                }
            }
            new _KA( options ).send();
        },
        /**
         * Function: replace
         * Replace action. Replaces the given string in all the selceted articles.
         */
        replace : function(){
            var codeReplacements = '';
            var inputs = _KS('input');

            for(var i = 0; i < inputs.length; i++) {
                var inputID = inputs[i].id;
                if(inputID.substring(0,11) == 'replaceBox_') {
                    if(inputs[i].checked) {
                        var code = inputID.substring(11);
                        codeReplacements += code + "__";
                    }
                }
            } 
         
            var params = {
                TargetNamespace : _KS('#renameInputField').value,
                action : 'GlobalReplaceAction',
                KWikiFocusedTerm : _KS('#replaceInputField').value
            }
             
            var options = {
                url : KNOWWE.core.util.getURL( params ),
                method : 'post',
                data : 'KWikitext='+codeReplacements,
                response : {
                    action : 'insert',
                    ids : [ 'rename-result' ],
                    fn : function(){ setTimeout ( 'document.location.reload()', 5000 ); }
                }
            }
            new _KA( options ).send();
        },
        /**
         * Function: getAdditionalMatchText
         * Get additional context in which the string occurs. This text is shown
         * in the match column of the renaming tool.
         * 
         * Parameters:
         *     atmUrl - A string containing information which context to expand.
         */ 
        getAdditionalMatchText : function( event ){   
            var el = KNOWWE.helper.event.target( event );
            var rel = el.getAttribute('rel');
            if(!rel) return;             
            rel = eval("(" + rel + ")" );
            
            var atmUrl = rel.article + ":" + rel.section + ":" + rel.index + ":" 
                + rel.words + ":" + rel.direction;
            
            var params = {
                action : 'WordBasedRenamingAction',
                ATMUrl : atmUrl,
                KWikiFocusedTerm : _KS('#replaceInputField').value,
                TargetNamespace : _KS('#renameInputField').value,
                ContextAfter : _KS('#renameAfterInputContext').value,
                ContextPrevious : _KS('#renamePreviousInputContext').value,
                CaseSensitive : _KS('#search-sensitive').value
            }
            var options = {
                url : KNOWWE.core.util.getURL( params ),
                method : 'post',
                response : {
                    action : 'none',
                    fn : function(){ 
                        var id = rel.direction + rel.index;
                        _KS('#'+id).setHTML( request.getResponse() );
                        request = null;
                        _KE.add('click', _KS('#'+id), KNOWWE.shared.renaming.getAdditionalMatchText);
                    }
                }
            }
            var request = new _KA( options );
            request.send();
        },
        /**
         * Function: getColumns
         * Returns the columns used in the renaming result table.
         * 
         * Returns:
         *     The columns used in the renaming result table.
         */
        getColumns : function(){
            return myColumns;
        }
    }
}();

/**
 * Namespace: KNOWWE.core.edit
 * The KNOWWE quick edit namespace.
 */
KNOWWE.shared.edit = function(){
    return {
        /**
         * Function: init
         * Initializes some wuick edit default functionality.
         */     
        init : function(){
            var elements = _KS('.quickedit .default');
            for(var i = 0; i < elements.length; i++){
                var rel, bttns;
                
                _KE.removeEvents(elements[i]);
                rel = eval("(" + elements[i].getAttribute('rel') + ")");
                bttns = _KS('#'+rel.id + ' input[type=submit]');
                if( bttns.length != 0 ){
                    _KE.add('click', bttns[0], KNOWWE.shared.edit.onSave );
                    _KE.add('click', elements[i], function(e){
                        var el = _KE.target(e);
                        var rel = eval("(" + el.getAttribute('rel') + ")");
                        KNOWWE.shared.actions.enableQuickEdit( KNOWWE.shared.edit.init, rel.id, "render");
                    });
                }  else {               
                    _KE.add('click', elements[i], function(e){
                        var el = _KE.target(e);
                        var rel = eval("(" + el.getAttribute('rel') + ")");
                        KNOWWE.shared.actions.enableQuickEdit( KNOWWE.shared.edit.init, rel.id, null);
                    });
                }
            }               
        },
        /**
         * Function: onSave
         * Triggered when the changes to the quick edit element in edit mode should be saved.
         * 
         * Parameters:
         *     e - The occurred event.
         */     
        onSave : function( e ){
            var el = _KE.target(e);
            var rel = eval("(" + el.getAttribute('rel') + ")");
            var params = {
                action : 'UpdateKDOMNodeAction',
                SectionID :  rel.id,
                KWiki_Topic : KNOWWE.helper.gup('page')
            }

            var options = {
                url : KNOWWE.core.util.getURL ( params ),
                data : 'TargetNamespace='+encodeURIComponent(_KS('#' + rel.id + '/default-edit-area').value.replace(/\s$/,"")),
                loader : true,
                response : {
                    action : 'none',
                    fn : function(){ 
                        KNOWWE.shared.actions.enableQuickEdit( KNOWWE.shared.edit.init, rel.id, "render");
                        Collapsible.render( _KS('#page'), KNOWWE.helper.gup('page'));
                    }
                }
            }
            new _KA( options ).send();          
        }
    }
}();


/* ############################################################### */
/* ------------- Onload Events  ---------------------------------- */
/* ############################################################### */
(function init(){
    
    window.addEvent( 'domready', _KL.setup );

    if( KNOWWE.helper.loadCheck( ['Wiki.jsp'] )){
        window.addEvent( 'domready', function(){
        	KNOWWE.shared.actions.init();
        	KNOWWE.shared.renaming.init();
            KNOWWE.shared.typebrowser.init();
            KNOWWE.shared.template.init();
        });
    };
}());