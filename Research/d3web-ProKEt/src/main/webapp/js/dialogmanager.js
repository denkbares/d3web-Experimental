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
var currentAssembledDialog;

/* Startup function: we need to check parameters from request here, as 
 * this is the (currently) only way to get noticed whether the KB and XML
 * have been loaded and message should be displayed in UI 
 * TODO: maybe refactor */
$(function(){
     
    /* Confirm file upload DIALOG */
    var opts = {
        autoOpen: false,
        position: [ 0, 0],
        minWidth: 200,
        minHeight: 150,
        modal: false,
        buttons: [{
            id: "saveOK",
            text: "Fortfahren",
            click: function(){
                $('#jqFileUploadConfirm').dialog('close');
                fuSaveOverwrite();
            }
        },
        {
            id: "saveCancel",
            text: "Abbrechen",
            click: function(){
                $('#jqFileUploadConfirm').dialog('close');
            }
        }]
    }
    $("#jqFileUploadConfirm").dialog(opts);
   
   
    
    
    
    /* fix the display of C:\fakepath\... in Safari etc browsers... */
    var fileUploadID = "origKBUploadField";
    
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
    
    
    upfilename = "";
    if(Request.parameter("upfilename")!= undefined){
        upfilename = Request.parameter("upfilename").replace("%", " ");
    }
    
    //$('#jqFileUploadConfirm').dialog("close");
    
    if(Request.parameter("fileExists") != undefined){
        if(Request.parameter("fileExists")=="true"){
            //display confirmation dialog
            $("#jqFileUploadConfirm").dialog("open");
        }
    }
    
    
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
            //only for WMS Upload currently
            $('#statusMessage').html("<b>Dateityp nicht unterstützt, bitte lade als CSV mit Semikolontrennzeichen hoch!</b>");
            //$('#statusMessage').html("<b>Bitte erlaubtes Wissensbasis Dateiformat (.doc/.xlsx/.d3web) auswählen!</b>")
            removeClass("statusMessageOK").addClass("statusMessageERR");
        }
    //window.location.href = removeParameter(window.location.href, "upERR");
    }
    
    // Wissensbasis wurde erfolgreich hochgeladen und kann direkt geparst werden.
    if(statusKB!=undefined && statusKB != "" && statusKB=="done"){
        //$("#statusMessage").html("Wissensbasis <b>" + upfilename + "</b> wird geparst.");

        parseDocToKB();
    }
    
    if(statusSpecs!=undefined && statusSpecs != "" && statusSpecs=="done"){
        $("#statusMessage").html("Spezifikation <b>" + upfilename + "</b> hochgeladen.");
    //window.location.href = removeParameter(window.location.href, "upSPEC");
    //window.location.href = removeParameter(window.location.href, "upfilename");
    }
 
    $("#delKBButton").unbind('click').click(function(){
        deleteSelectedKB();
    })
    
    $("#linkButton").unbind('click').click(function(){
        assembleDialogLink();
    })
    
    $("#startDialogButton").unbind('click').click(function(){
        startDialog();
    })
    
});


function fuSaveOverwrite(){
    
    link = $.query.set("action", "uploadAndOverwrite").set("overwrite", "true");
    link = window.location.href.replace(window.location.search, "") + link;
    
    $.ajax({
        type : "GET",
        url : link,
        cache : false, // needed for IE, call is not made otherwise
        success : function(html) {
            if(html.indexOf("NOPARSE")==-1){
                upfilename = html;
                parseDocToKB();
            }
        } 
    });
}



/* calls the functionality for parsing the word document into a .d3web file */
function parseDocToKB(){
   
    var docname = upfilename;
    
    link = $.query.set("action", "parseKBDoc").set("docname", docname).toString();
    link = window.location.href.replace(window.location.search, "") + link;
   
    $("#statusMessage").html("Wissensbasis Dokument <b>" + docname + "</b> wird geparst...");
    $("#progressIndicator").show();
    

    $.ajax({
        type : "GET",
        url : link,
        cache : false, // needed for IE, call is not made otherwise
        success : function(html) {
            $("#progressIndicator").hide();
            if(html.indexOf("success")!=-1){
                //alert("everything's fine");
                $("#statusMessage").html("Wissensbasis <b>" + docname + "</b> erfolgreich geparst.");
                var kbSelectContent = html.replace("success;;;", "");
                //alert(kbSelectContent);
                $("#d3webSelect").html(kbSelectContent);
                
            } else if(html.indexOf("showErrFile") != -1) {
                // everything fine
                $("#ErrorReportImgButtonText").removeClass("buttonTextInactive").addClass("buttonTextActive");
                $("#ErrorReportImgButton img").attr("src", "img/ErrorReport.png");
                var errorReportLink = html.toString().replace("showErrFile;", "");
                $("#ErrorReportImgButton").attr("onclick", "dmOpenErrorReport('" + errorReportLink +"')");
                
                $("#statusMessage").html("Fehler beim Parsen! Bitte Fehlerbericht lesen.").addClass("statusMessageERR").removeClass("statusMessageOK");
                
            } else if(html.indexOf("showExceptionFile") != -1){
                dmOpenExceptionReport();
            } else {
                $("#statusMessage").html("Bitte laden Sie ein valides Dokument hoch.").addClass("statusMessageERR").removeClass("statusMessageOK");
            }
        } 
        
    });
}

function dmOpenErrorReport(errorReportLink){
    window.open(errorReportLink, "Fehlerbericht", "scrollbars=yes,left=100,top=200,width=800,height=600"); 
    $("#statusMessage").html("");
}

function dmOpenExceptionReport(){
    
    link = $.query.set("action", "finalizeExceptionReport");
    link = window.location.href.replace(window.location.search, "") + link;
   
    $.ajax({
        type : "GET",
        url : link,
        cache : false, // needed for IE, call is not made otherwise
        success : function(html) {
            window.open(html, "Exception-Bericht", "scrollbars=yes,left=100,top=200,width=800,height=600"); 
            $("#statusMessage").html("");
        } 
        
    });
    
}

/* calls functionality for assembling a dialog servlet string from the
 * (currently last loaded) KB and spec 
 * TODO LATER: KB and spec can be chosen freely */
function assembleDialogLink(){
    
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
            
            if(html.indexOf("ERROR")==-1){
                // store link in hidden field
                var link = "<a href='" + html + "' target='_blank'>" + html + "</a>";
                $("#latestDialogLink").html(link); 
                // open the dialog in a new window 
                //window.open(html);
                // activate button for storing dialog to the user's list'
                $("#StoreImgButton img").attr("src", "img/Store.png");
            } else {
                $("#statusMessage").html("Assemble Dialog Exception!").addClass("statusMessageERR").removeClass("statusMessageOK");
            // TODO: open Exception Report automatically
            }
        } 
    });
}

function startDialog(){
    
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
            
            if(html.indexOf("ERROR")==-1){
                base = window.location.href.toString();
                linkComplete = base.substring(0, base.lastIndexOf("/")) + html;
                
                dialogWindow = window.open(linkComplete, "", "");   
                dialogWindow.focus();
                
               
            } else {
                $("#statusMessage").html("Assemble Dialog Exception!").addClass("statusMessageERR").removeClass("statusMessageOK");
            // TODO: open Exception Report automatically
            }
        } 
    });
    
    
}


/* writes the filename of upload files from the original file input upload field
 * to the better adaptable fake field (which is used for better look and feel) */
function writeToFakeField(origFieldID){
    
    if(origFieldID.indexOf("KBUpload")!= -1){
        $("#docfilename").val(fixFakePathDisplay(origFieldID));
        $("#docnamestore").val(fixFakePathDisplay(origFieldID));
        
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
    //$("#dialoglinklist").html(getDialogLinkCell($("#latestDialogLink").html()));
    
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
            if(html.indexOf("EXCEPTION")==-1){
                // UPDATE THE LINKLIST
                if (html.indexOf("##replaceid##")!= -1) {
                    var updateArray = html.split(/##replaceid##|##replacecontent##/);
                    for (var i = 0; i < updateArray.length - 1; i+=2) {
                        if (updateArray[i].length == 0) {
                            i--;
                            continue;
                        }
                        $("#" + updateArray[i]).replaceWith(updateArray[i + 1]);
                    }
                }
            } else {
            // open Exception Report
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

function deleteSelectedKB(){
    var kbToDelete;
    $('#d3webSelect option:selected').each(function(){
        kbToDelete = $(this).val();
    });
    
    if(kbToDelete != undefined){
        
        var baseLink = window.location.href.replace(window.location.search, "");
       
        var link = 
        $.query.set("action", "deleteSelectedKB")
        .set("kbToDelete", kbToDelete).toString();
        
        link = baseLink + link;
         
        $.ajax({
            type : "GET",
            url : link,
            cache : false, // needed for IE, call is not made otherwise
            success : function(html) {
                if(html.indexOf("ERROR")==-1){
                    $("#statusMessage").html("Wissensbasis " + kbToDelete + " entfernt.").addClass("statusMessageOK").removeClass("statusMessageERR");
                    if (html.indexOf("##replaceid##")!= -1) {
                        var updateArray = html.split(/##replaceid##|##replacecontent##/);
                        for (var i = 0; i < updateArray.length - 1; i+=2) {
                            if (updateArray[i].length == 0) {
                                i--;
                                continue;
                            }
                            $("#" + updateArray[i]).replaceWith(updateArray[i + 1]);
                        }
                    }
                } else {
                    $("#statusMessage").html("Wissensbasis konnte nicht gelöscht werden! Bitte kontaktieren Sie den Systemadministrator.").addClass("statusMessageERR").removeClass("statusMessageOK");
                }
            } 
        });
    }
}

function removeParameter(url, parameter)
{
    var urlparts= url.split('?');

    if (urlparts.length>=2)
    {
        var urlBase=urlparts.shift(); //get first part, and remove from array
        var queryString=urlparts.join("?"); //join it back up

        var prefix = encodeURIComponent(parameter)+'=';
        var pars = queryString.split(/[&;]/g);
        for (var i= pars.length; i-->0;)               //reverse iteration as may be destructive
            if (pars[i].lastIndexOf(prefix, 0)!==-1)   //idiom for string.startsWith
                pars.splice(i, 1);
        url = urlBase+'?'+pars.join('&');
    }
    return url;
}


function setStatusMessage(message, error){
    if(error){
        $("#statusMessage").html(message).addClass("statusMessageERR").removeClass("statusMessageOK");
    } else {
        $("#statusMessage").html(message).addClass("statusMessageOK").removeClass("statusMessageERR");
    }
     
}

function clearStatusMessage(){
    $("#statusMessage").html("").removeClass("statusMessageOK").removeClass("statusMessageERR");
}

function fixFakePathDisplay(fileUploadID){
   
    //get our form field
    var a = $('#'+fileUploadID);
    b = encodeURI(a.val());
    c = b.replace("C:%5Cfakepath%5C","");
    return c;
}