<!DOCTYPE html>
<html>
<head profile="http://www.w3.org/2005/10/profile">
<link rel="icon" type="image/ico" href="images/favicon.ico">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<script src="libsExternal/jquery/jquery-1.6.1.min.js" type="text/javascript" language="javascript"></script>
<script src="libsExternal/jqueryUI/jqueryUi-1.8.10.all.min.js" type="text/javascript" language="javascript"></script>


<style type="text/css">$css$</style>

<title>ProKEt - Doc Loader Module</title>
</head>

<body>
<div id="docloader">

    <div id="dlUploadLabelRow" class="row">
        <div id="uploaderLabel" class="cell labelcell">Hochladen</div>
    </div>

    <div class="row">
        <div class="cell selectcell firstcell">$FileBrowser()$</div>
        <div id="span2buttons" class="cell lastcell">
             <div class="cell buttoncell">
                $ImgButton(src="img/Upload.png", 
                            alt="Upload",
                            tt="Upload",
                            click="alert('Upload');")$
             </div>
             <div class="cell buttoncell">
                 $ImgButton(src="img/ErrorReport.png", 
                            alt="Error Report",
                            tt="Error Report",
                            click="alert('ErrorReport');")$
             </div>
         </div>
    </div>

    <div id="dlActionsLabelRow" class="row">
        <div id="actionsLabel" class="cell labelcell">Aktionen: </div>
        <div id="docloadKBName" class="cell labelcell"> $if(kbname)$ $kbname$ $else$ / $endif$</div>  
    </div>


    <div class="row">
        <div class="cell selectcell firstcell">$DialogTypeBrowser()$</div>
        <div id="span2buttons" class="cell lastcell">
            <div class="cell buttoncell">
                $ImgButton(src="img/Execute.png", 
                            alt="Execute Dialog",
                            tt="Execute Dialog",
                            click="alert('Execute Dialog');")$
            </div>               
            <div class="cell"></div>
        </div>
    </div>

    <div class="row spacerow" > </div>
    
    <div class="row">
        <div class="cell selectcell firstcell">$DomainBrowser()$</div>
        <div id="span2buttons" class="cell lastcell">
            <div class="cell buttoncell">
                $ImgButton(src="img/Save.png", 
                            alt="Save",
                            tt="Save Knowledge Base",
                            click="alert('Save Knowledge Base');")$
            </div>
            <div class="cell"></div>
        </div>
    </div>
</div>
</body>
</html>