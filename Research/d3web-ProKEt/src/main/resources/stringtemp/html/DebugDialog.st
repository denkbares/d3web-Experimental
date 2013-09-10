<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html><head>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META http-equiv="Content-Script-Type" content="text/javascript">
<META http-equiv="Content-AttributeContainer-Type" content="text/css">
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<title>$title$</title>
<style type="text/css">
body {
	font-family: Arial Sans-Serif;
}
.parent {
	background-color: rgb(180, 180, 255);
	padding: 3px;
	margin: 3px;
	border: 1px dashed gray;
}
.child {
	background-color: rgb(220, 220, 255);
	padding: 3px;
	margin: 3px;
}
</style>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
<script type="text/javascript" language="javascript">
	\$(function(){
		\$(".child:visible").hide(0);
	});
	\$(function(){
		\$(".parent").click(function() {
			if (\$(this).children(":visible").size() == 0) {
				\$(".child:visible").hide(0);
				\$(this).children().show(0);
			} else {
				\$(this).children().hide(0);
			}
			
		});
	});
</script>
</head>

<body>

$items$

</body></html>