/**
 * Title: KnowWE-plugin-biolog Contains all javascript functions concerning the
 * KnowWE plugin biolog.
 */
if (typeof KNOWWE == "undefined" || !KNOWWE) {
 /**
     * The KNOWWE global namespace object. If KNOWWE is already defined, the
     * existing KNOWWE object will not be overwritten so that defined namespaces
     * are preserved.
     */
 var KNOWWE = {};
}

/**
 * Namespace: KNOWWE.plugin.biolog The KNOWWE plugin d3web namespace.
 * Initialized empty to ensure existence.
 */
KNOWWE.plugin.biolog = function(){
    
    var _autocompletion = "knowwe-autocompletion";
    var _autoHighlight = "highlight";
    var _search = "biologsearch";   
    var _searchInput = "s";
    var _minLength = 3;
    var _suggestionResult = true;
    
    /**
     * Function createAutoDOMElement
     * 
     * Creates an element that displays the autocomplete suggestions.
     */
    function createAutoDOMElement(){
        div = document.createElement('div');
        var domID = document.createAttribute('id');
        domID.nodeValue = _autocompletion;
        div.setAttributeNode( domID );
        _KS( '#biologsearch-wrapper' ).appendChild( div );
        
        //add events
        _KE.add('mouseover', div, function(e){
            var element = _KE.target( e );
            if(element.tagName.toLowerCase() !== 'ul'){
                element = KNOWWE.helper.tagParent( element, 'li' );
                element.className = _autoHighlight;
            }
        });
        _KE.add('mouseout', div, function(e){
            _KE.target( e ).className = '';
        });
        _KE.add('click', div, function(e){
            var element = _KE.target( e );
            element = KNOWWE.helper.tagParent( element, 'li' );
            _KS('#'+_searchInput).value = element.textContent || element.innerText; 
            _KS('#'+_autocompletion)._remove();
        });
        _suggestionResult = false;      
    }
    
    /**
     * 
     * Removes the dom element that displays the suggestions
     */
    function removeAutoDOMElement(){
        var auto = _KS('#'+_autocompletion);
        if( auto ){ 
            auto._remove();
            _KE.removeEvents(auto);
        }
        _suggestionResult = true;
    }
    
 return {
    init : function(){
        _KE.add('keydown', _KS('#'+_searchInput), function(e){
            switch(e.keyCode) {
                case 38: return KNOWWE.plugin.biolog.autoKeyMove('up');
                    break;
                case 40: return KNOWWE.plugin.biolog.autoKeyMove('down');
                    break;        
                case 13: 
                    var suggestion = _KS('.'+_autoHighlight);
                    if(suggestion.length != 0){
                        _KS('#'+_searchInput).value = suggestion[0].textContent || suggestion[0].innerText;
                        removeAutoDOMElement();
                    } else {
                        removeAutoDOMElement();
                        KNOWWE.plugin.biolog.search();
                    }
                    break;
                default:
                    break;
            }
        });
        if(_KS('#biologsearch-tagcloud')){
            var el = _KS('#biologsearch-tagcloud');
            _KE.add('click', el, function(e){
                var target = _KE.target( e ); 
                var field = _KS('#'+_searchInput).value;
                
                if(field.length > 0){
                    field += " " + target.innerHTML; 
                } else {
                    field = target.innerHTML;
                }
                _KS('#'+_searchInput).value = field;
                _KE.cancel( e );
            });
        }
    },
    /**
     * Function: autocomplete
     */
     autocomplete : function(){
        var el, div;
        el = _KS( '#'+_searchInput );
        
        if(el.value.length < _minLength) return;
        
        if(_suggestionResult){
            createAutoDOMElement();
        }       
        
        var params = {
            action : 'AutocompleteAction',
            searchText : el.value
        }
        var options = {
            url : KNOWWE.core.util.getURL( params ),
            response : {
                action : 'none',
                fn : KNOWWE.plugin.biolog.showSuggestions
            }
        }
        new _KA( options ).send();      
     },
     /**
      * Function: showSuggestions
      */
     showSuggestions : function(){
        var that = this;
        var result = that.responseText;

        if(result.length === 0){
            removeAutoDOMElement();
            return;
        }
        var auto = _KS('#'+ _autocompletion );
        if(!auto){
            createAutoDOMElement();
            auto = _KS('#'+ _autocompletion );
        }
        
        var token = that.responseText.split("\\c");
        var html = "<ul>";
        for(var i = 0; i < token.length; i++){
            html += "<li style=\"display: list-item\">" + token[i] + "</li>";
        }
        html += "</ul>";
        auto.innerHTML = html;  
     },
     /**
      * Function: autoKeyMove
      */
     autoKeyMove : function(direction){
        var highlighted = _KS('.'+_autoHighlight);
        var li = _KS('li', _KS('#'+_autocompletion));
            
        var el, sibling;
        if(highlighted.length != 0){
            el = highlighted[0];
            if(direction === 'up'){
                sibling = el._previous();
            }
            if(direction === 'down'){
                sibling = el._next();
            }
            if(!sibling) sibling = el;
            el.className = '';
            sibling.className = _autoHighlight; 
        } else {
            sibling = li[0];
            sibling.className = _autoHighlight;
        }   
     },
     /**
      * Function: getCaret
      */
     getCaret : function(){
        var el = document.getElementById('s');
        var caretPos = 0;
        if (document.selection) {
            el.focus ();
            var sel = document.selection.createRange ();
            sel.moveStart ('character', -el.value.length);
            caretPos = sel.text.length;
        }
        else if (el.selectionStart || el.selectionStart == '0'){
            caretPos = el.selectionStart;
        }
        return caretPos;
     },
     /**
      * Function: updateTagCloud
      * Updates the TagCloud after the user send a search request.
      * 
      * Parameters:
      *     query - The user entered query string.
      */
     updateTagCloud : function( query ){
        var params = {
            action : 'UpdateCloudAction',
            searchText : query
        }
        var options = {
                url : KNOWWE.core.util.getURL( params ),
                response : {
                    action : 'insert',
                    ids : ['biologsearch-tagcloud']
                }
        }
        new _KA( options ).send();
     },
     /**
      * Function: search
      * 
      * Executes the Biolog search action and displays the result of the request.
      * If the request was successfully the search results are displayed, if not
      * an error message is shown to the user. 
      */
     search : function(){
        var params = {
                action : 'SearchAction',
                searchText : document.getElementById(_searchInput).value
        }
        var options = {
                url : KNOWWE.core.util.getURL( params ),
                response : {
                    action : 'insert',
                    ids : ['biologsearch-result'],
                    fn : function(){
                        KNOWWE.core.util.addCollabsiblePluginHeader();
                        KNOWWE.plugin.biolog.updateTagCloud(params.searchText);
                        if(_KS('#'+_autocompletion)){
                            _KS('#'+_autocompletion)._remove();
                        }
                        Wiki.prefs.set('PrevQuery', '');
                        var p = Wiki.prefs.get('PrevQuery');
                        Wiki.prefs.set('PrevQuery', params.searchText);
                    }
                }
        }
        new _KA( options ).send();
        _suggestionResult = true;
    }
 }
}(); 


(function init(){ 
     window.addEvent( 'domready', function(){
         if(_KS('#searchsubmit')){
            KNOWWE.plugin.biolog.init();
            _KE.add('click', _KS('#searchsubmit'), KNOWWE.plugin.biolog.search);
            _KE.add('keyup', _KS('#s'), function(e){
                if((e.keyCode > 46 && e.keyCode < 91) || e.keyCode === 8){
                    KNOWWE.plugin.biolog.autocomplete();                
                }
            });
         }
     });
}());