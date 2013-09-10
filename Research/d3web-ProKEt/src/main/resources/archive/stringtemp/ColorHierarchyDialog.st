$HTMLHeader()$
<body>
<div id="head">$header$</div>
<div id="left">$NavigationBox()$$left$</div>
<div id="content">
	<div class="dialog">
		$if(title)$<div id="dialog-title">$title$</div>$endif$
		$if(text)$$text$$endif$	
		<table><tr><td valign="top">
		<div id="dialog-content">
			$children$			
		</div></td><td valign="top">
		<div id="bonus"></div></td></tr></table>
	</div>
</div>
<div id="right">$sidetext$</div>
<div id="foot">$footer$</div>
</body></html>