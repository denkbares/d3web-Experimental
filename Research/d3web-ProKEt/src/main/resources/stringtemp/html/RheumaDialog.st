$HTMLHeader()$
<body>

<!-- header //-->
<div id="head">$header$</div>

<div id="middle">
	<!-- navigation on the left side //-->
	<div id="left">$NavigationBox()$$left$</div>
	
	<!-- actual content //-->
	<div id="content">
		<div class="dialog">
			 $TitleTooltip()$$text$
			<div class="dialog-content">
				$children$
			</div>
		</div>
	</div>
</div>

<!-- footer //-->
<div id="foot">$footer$</div>

</body></html>