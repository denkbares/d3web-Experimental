$HTMLHeader()$
<body>
<div id="head">$header$</div>
<div id="middle">
	<div id="left">$NavigationBox()$$left$</div>
	<div id="content">
		<div class="dialog">
			 $TitleTooltip()$$text$
			<div id="dialog-content">
				$children$
			</div>
		</div>
	</div>	
	<div id="right">
		<div class="righttop">
		<b>Diagnosen</b></div>
		<div class="rightsub">
		Diagnose 1 <br> Diagnose 2<br> </div>
		<div class="righttop">
		<b>Fallvergleich</b></div>
		<div class="rightsub">
		Fallvergleich starten</div>
	</div>
</div>
<div id="foot">$footer$</div>
</body></html>