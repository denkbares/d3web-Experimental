$HTMLHeader()$
<body>
<div id="head">$header$</div>

<div id="middle">

	<div id="left">$NavigationBox()$$left$</div>
	<div id="content">
		<div class="dialog">
			$if(rating)$<div id="t-$fullId$" class="title t-$fullId$">
				<table><tr><td>$title$ $Tooltip()$</td><td>$RatingBoxTotal()$</td></tr></table>
			</div>
			$else$
			$TitleTooltip()$
			$endif$
			
			$text$
			<div id="dialog-content">
				<div id="sidetext">$sidetext$</div>
				$children$
			</div>
		</div>
	</div>
	
</div>
<div id="foot">$footer$</div>
</body></html>