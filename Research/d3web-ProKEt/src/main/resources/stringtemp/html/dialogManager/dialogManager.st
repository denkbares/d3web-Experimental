<!DOCTYPE html>
<html>
<head profile="http://www.w3.org/2005/10/profile">
<link rel="icon" type="image/ico" href="images/favicon.ico">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<script src="libsExternal/jquery/jquery-1.6.1.min.js" type="text/javascript" language="javascript"></script>
<script src="libsExternal/jqueryUI/jqueryUi-1.8.10.all.min.js" type="text/javascript" language="javascript"></script>

<script src="libsExternal/jquery/jquery-1.6.1.min.js" type="text/javascript" language="javascript"></script>
<script src="libsExternal/jquery/jquery.object.js" type="text/javascript" language="javascript"></script>
<script src="libsExternal/jqueryUI/jqueryUi-1.8.10.all.min.js" type="text/javascript" language="javascript"></script>
<script src="js/dialogmanager.js" type="text/javascript" language="javascript"></script>


<style type="text/css">$css$</style>

<title>ProKEt - Doc Loader Module</title>
</head>

<body>
<div id="docloader">
    <div id="actionleftcol" class = "cell">
    
    <div id="dlUploadLabelRow" class="row">
        <div id="uploaderLabel" class="cell labelcell labelcellFirstRow">Bereitstellen</div>
    </div>

    <div class="row" id="uploadRow">
       
        <div class="cell selectcell firstcell">
            $dialogManager/FileBrowserKBOnly()$
        </div>
        
        <div class="cell lastcell">
            <div id="uploadParseButtons" class="row">
                <div id="span2buttons" class="cell">
                    <div class="cell buttoncell">
                        $ImgButton(src="img/Parse.png", 
                        alt="Parse knowledge base",
                        tt="Parse knowledge base",
                        click="parseDocToKB('origKBUploadField');")$
                    </div>
                    <div class="cell buttoncell">
                        $ImgButton(src="img/ErrorReportInactive.png", 
                        alt="ErrorReport",
                        tt="Error Report",
                        click="alert('ErrorReport');")$
                    </div>
                </div>
                
            </div>
            <!--<div id="uploadSpacerRow" class="row">
                <div id="UploadSpacer"></div> 
            </div>
            -->
            
        </div>
        
    </div>

    <div id="statusRow" class="row">
        <div id="statusMessage" class="statusMessageOK cell">
            $if(statusmessage)$$statusmessage$$endif$
        </div>
        <div id='progressIndicator'>
                <img src='img/processCircle.gif' alt='processCircle' width="22px" height=22px;/>
        </div>
    </div>

    <div id="selectorsRow" class="row">
        <div id="d3webFileSelectorCell" class="cell">
            <div id="fileSelectHeading" class="row">
                <div id="actionsLabel" class="cell labelcell">Wissensbasen</div>
            </div>
            
            $dialogManager/FileSelectorD3web()$

            <div id="kbButtons" class="row">
                <div class="cell">
                    <input type="button" name="linkButton" id="linkButton" class="defaultPlainButton" value="Link Anzeigen" />
                </div>
                 <div class="cell">
                    <input type="button" name="startDialogButton" id="startDialogButton" class="defaultPlainButton" value="Dialog Starten" /> 
                </div>
                <div class="cell">
                    <input type="button" name="delKBButton" id="delKBButton" class="defaultPlainButton" value="Entfernen" /> 
                </div>
                 
            </div>

        </div>

        <div id="selctorRowRightColCell" class="cell">
            <div id="specFileSelectorCellRow" class="row" >
                <div id="specFileSelectorCell" class="cell">
                    <div id="actionsLabel" class="cell labelcell">Dialogtypen</div>
                    $dialogManager/FileSelectorSpecs()$
                </div>
            </div>

        
            <div id="dialogLinkViewerRow" class="row">
                <div id="dialogLinkViewerCell" class="cell">
                    <div id="actionsLabel" class="cell labelcell">Dialog Links</div>
                    <div id="latestDialogLink" style="display:block;"></div>
                </div>
            </div>
        </div>

    </div>
    
</div>
</body>
</html>