/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
 * 
 * @author Martina Freiberg
 * @date 19.09.2012
 */


/******************************************/
/* Functions needed for the DialogManager */
/******************************************/

var upfilename;

/* Startup function: we need to check parameters from request here, as 
 * this is the (currently) only way to get noticed whether the KB and XML
 * have been loaded and message should be displayed in UI 
 * TODO: maybe refactor */
$(function(){
  
    var Request = {	
        parameter: function(name) {
            return this.parameters()[name];
        },
 	
        parameters: function() {
            var result = {};
            var url = window.location.href;
            var parameters = url.slice(url.indexOf('?') + 1).split('&');
 		
            for(var i = 0;  i < parameters.length; i++) {
                var parameter = parameters[i].split('=');
                result[parameter[0]] = parameter[1];
            }
            return result;
        }
    }
  
    var statusKB =  Request.parameter("upKB");
    var statusSpecs = Request.parameter("upSPEC");
    var status = Request.parameter("upERR");
     upfilename = Request.parameter("upfilename");
    
    // some error handling for uploading file
    if(status != undefined && status != ""){
        if(status=="nofile"){
            $('#UploadError').html("Bitte Datei auswählen!");
        } 
        if(status=="noxml"){
            $('#UploadError').html("Bitte XML Spezifikation (.xml) auswählen!");
        } 
        if(status=="nokb"){
            $('#UploadError').html("Bitte Wissensbasis (.doc/.zip/.d3web) auswählen!");
        }
    }
    
    if(statusKB!=undefined && statusKB != "" && statusKB=="done"){
        $("#UploadStatus").html("Wissensbasis hochgeladen.")
    }
    
    if(statusSpecs!=undefined && statusSpecs != "" && statusSpecs=="done"){
        $("#UploadStatus").html("Spezifikation hochgeladen.");
    }
 
});



/* calls the functionality for parsing the word document into a .d3web file */
// TODO
function parseDocToKB(id){
   
    var docname = upfilename;
    
    var link = $.query.set("action", "parseKBDoc").set("docname", docname).toString();
    link = window.location.href.replace(window.location.search, "") + link;

    $.ajax({
        type : "GET",
        url : link,
        cache : false, // needed for IE, call is not made otherwise
        success : function(html) {
            if(html.indexOf("parseErrorInvalidDoc")==-1){
                alert("everything's fine");
            } else {
                $("#ErrorReportImgButton img").attr("src", "img/ErrorReport.png");
            }
        } 
    });
}

/* calls functionality for assembling a dialog servlet string from the
 * (currently last loaded) KB and spec 
 * TODO LATER: KB and spec can be chosen freely */
function assembleDialog(){
    
    // TODO: if we have listings of KBs and Specs here, get the selected ones 
    // and send them as arguments
    var link = $.query.set("action", "assembleDialog").toString();
    link = window.location.href.replace(window.location.search, "") + link;

    $.ajax({
        type : "GET",
        url : link,
        cache : false, // needed for IE, call is not made otherwise
        success : function(html) {
            if(html.indexOf("error")==-1){
                // store link in hidden field
                $("#latestDialogLink").html(html); 
                // open the dialog in a new window 
                window.open(html);
                // activate button for storing dialog to the user's list'
                $("#StoreImgButton img").attr("src", "img/Store.png");
            } else {
                alert("assembleDialog error");
            }
        } 
    });
}


/* writes the filename of upload files from the original file input upload field
 * to the better adaptable fake field (which is used for better look and feel) */
function writeToFakeField(origFieldID){
    
    if(origFieldID.indexOf("KBUpload")!= -1){
        $("#docfilename").val($("#" + origFieldID).val());
        $("#docnamestore").val($("#" + origFieldID).val());
        
    }else if (origFieldID.indexOf("SpecUpload")!= -1){
        $("#specfilename").val($("#" + origFieldID).val());
        $("#specnamestore").val($("#" + origFieldID).val());
    }
    
}

/**
 * stores an assembled and displayed dialog link into a list of dialogs for 
 * the user (for quick access later on)
 */
function storeDialogToUsersList(){
    
    // TODO: needs to be altered later. Is used currently to quicly move the
    // latest dialog to the list view
    $("#dialoglinklist").html(getDialogLinkCell($("#latestDialogLink").html()));
    
    var dialogLink = $("#latestDialogLink").html();
    
    var link = 
    $.query.set("action", "storeDialogToUsersList")
    .set("dialogLink", dialogLink).toString();
            
    link = window.location.href.replace(window.location.search, "") + link;
    $.ajax({
        type : "GET",
        url : link,
        cache : false, // needed for IE, call is not made otherwise
        success : function(html) {
            if(html.indexOf("error")==-1){
                
            } else {
                alert("storeDialogToUsersList error");
            }
        } 
    });
}

/**
 * Display a given dialog link within a css cell as link
 */
function getDialogLinkCell(dialoglink){
    var dialoghtml = 
    "<div class='row'>\n\
            <a href='"+dialoglink+"' target='_blank'>"+dialoglink+"</a> \n\
        </div>"
   
    return dialoghtml;
    
}
