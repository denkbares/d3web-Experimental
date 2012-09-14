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
<h1>ProKEt - Doc Loader Module</h1>

<div id="docloadUploader">
    <div id="uploaderLabel">Hochladen</div>
    $FileBrowser()$
    $BaseButton(label="Hochladen")$
    $BaseButton(label="Fehlerbericht")$
</div>

<div id="docloadActions">
    <div id="actionsLabel">Aktionen: </div>
    <div id="docloadKBName"> $if(kbname)$ $kbname$ $else$ / </div>
    $DialogTypeBrowser()$
    $BaseButton(label="Testen")$
    $DomainBrowser()$
    $BaseButton(label="Speichern")$
</div>

</body>
</html>