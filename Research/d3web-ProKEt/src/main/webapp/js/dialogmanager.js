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
            $('#statusMessage').html("Bitte Datei auswählen!")
                .removeClass("statusMessageOK").addClass("statusMessageERR");
        } 
        if(status=="noxml"){
            $('#statusMessage').html("Bitte XML Spezifikation (.xml) auswählen!")
                removeClass("statusMessageOK").addClass("statusMessageERR");
        } 
        if(status=="nokb"){
            $('#statusMessage').html("Bitte Wissensbasis (.doc/.zip/.d3web) auswählen!")
                removeClass("statusMessageOK").addClass("statusMessageERR");
        }
    }
    
    if(statusKB!=undefined && statusKB != "" && statusKB=="done"){
        $("#statusMessage").html("Wissensbasis hochgeladen.");
    }
    
    if(statusSpecs!=undefined && statusSpecs != "" && statusSpecs=="done"){
        $("#statusMessage").html("Spezifikation hochgeladen.");
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
            if(html.indexOf("success")!=-1){
                //alert("everything's fine");
                $("#statusMessage").html("Wissensbasis erfolgreich geparst.");
                var kbSelectContent = html.replace("success;;;", "");
                //alert(kbSelectContent);
                $("#d3webSelect").html(kbSelectContent);
                
            } else if(html.indexOf("showErrFile") != -1) {
                // everything fine
                $("#ErrorReportImgButton img").attr("src", "img/ErrorReport.png");
                var errorReportLink = html.toString().replace("showErrFile;", "");
                $("#ErrorReportImgButton img").attr("onclick", "dmOpenErrorReport('" + errorReportLink +"')");
                $("#statusMessage").html("Fehler beim Parsen! Bitte Fehlerbericht lesen.")
                
            } else {
                $("#statusMessage").html("Bitte laden Sie ein valides Dokument hoch.")
            }
        } 
    });
}

function dmOpenErrorReport(errorReportLink){
    window.open(errorReportLink, "Fehlerbericht", "width=800,height=600,left=100,top=200"); 
  }

/* calls functionality for assembling a dialog servlet string from the
 * (currently last loaded) KB and spec 
 * TODO LATER: KB and spec can be chosen freely */
function assembleDialog(){
    
    // TODO: if we have listings of KBs and Specs here, get the selected ones 
    // and send them as arguments
    var selections = "";
    $("select option:selected").each(function () {
        selections += $(this).text() + ";;;";
    });
    
    var selArray = selections.split(";;;");
    var kb = selArray[0];
    var spec = selArray[1];

    var link = $.query.set("action", "assembleDialog")
        .set("kb", kb).set("spec", spec).toString();
        
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
                // extra error div or use upload status div?
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
