//
// THIS FILE CONTAINS ALL NECESSARY JAVASCRIPT NEEDED FOR Extended Questionary 
// Dialogs, i.e., containing solution and questionnaire navigation and
// solution explanation
//

$(function(){
        
        
    $.jstree._themes = "libsExternal/jsTree/themes/";
    
    $("#treeNaviSolutions") 
    
        .jstree({
        
            "themes" : {"theme":"apple", "dots":false, "icons":false},
            "plugins" : ["themes","html_data"]
    })
    
    .bind("loaded.jstree", function(event, data){
        alert("jstree is loaded");
    });
    
    
    

});

