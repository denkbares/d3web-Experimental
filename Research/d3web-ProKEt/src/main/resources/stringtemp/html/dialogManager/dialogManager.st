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
            $FileBrowser()$
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
            <div id="uploadSpacerRow" class="row">
                <div id="UploadSpacer"></div> 
            </div>
        </div>
        
    </div>

    <div id="statusRow" class="row">
        <div id="statusMessage" class="statusMessageOK cell"></div> 
    </div>

    <div id="selectorsRow" class="row">
        <div id="d3webFileSelectorCell" class="cell">
            <div id="actionsLabel" class="cell labelcell">Wissensbasen</div>
            $dialogManager/FileSelectorD3web()$
        </div>
        <div id="specFileSelectorCell" class="cell">
            <div id="actionsLabel" class="cell labelcell">UI Spezifikationen</div>
            $dialogManager/FileSelectorSpecs()$
        </div>
    </div>
    </div>

    <div id="spacecol" class="cell">
    </div>

    <div id="datarightcol" class="cell">
        <div id="dlActionsLabelRow" class="row">
            <div id="actionsLabel" class="cell labelcell">Testen </div>
        </div>
        
        <div class="row">
            <div class="cell buttoncell firstcell">
                <div class="cell">
                    $ImgButton(src="img/AssembleRun.png", 
                            alt="Assemble and Run Dialog",
                            tt="Assemble and Run Dialog",
                            click="assembleDialog()")$
                </div>
                <div class="cell spacercell">
                </div>
                <div class="cell">
                    $ImgButton(src="img/StoreInactive.png", 
                            alt="Store",
                            tt="Store Knowledge Base to Repository",
                            click="storeDialogToUsersList();")$
                </div>
            </div>
            <div class="cell buttoncell lastcell">    
            </div>
        </div>

        <div id="spacerowRightcol" class="cell">
        </div>

        <div id="dialoglinkviewer">
            <div id="latestDialogLink" style="display: none"></div>
            <div id="actionsLabel" class="cell labelcell">Dialog Links</div>
            <div id="dialoglinklist">
            </div>
        </div>


    </div>
</div>
</body>
</html>