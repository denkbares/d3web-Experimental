<!DOCTYPE html>
<html>
<head profile="http://www.w3.org/2005/10/profile">
<link rel="icon" type="image/ico" href="images/favicon.ico">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<script src="libsExternal/jquery/jquery-1.5.min.js" type="text/javascript" language="javascript"></script>
<script src="libsExternal/jqueryUI/jqueryUi-1.8.10.all.min.js" type="text/javascript" language="javascript"></script>

<script type="text/javascript">
	\$(function() {
		\$("#accordion").accordion({autoheight:true});
		\$("div > a").button();		
	});
</script>


<style type="text/css">
h1,h2 {
	text-align: center;
	font-family: Verdana, Arial, sans-serif;
}
$css$
</style>


<title>ProKEt - Control Center</title>
</head>

<body>
<h1>ProKEt - ControlCenter</h1>

<p align="center">
<div id="accordion"
	style="width: 600px; margin-left: auto; margin-right: auto;">

<h3><a href="#">Pure Prototype Dialogs</a></h3>
<div>$dialogs$</div>
<h3><a href="#">d3web Dialogs</a></h3>
<div>$d3web$</div>
</div>
</body>
</html>