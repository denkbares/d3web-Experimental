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
	
	<!-- box on the right side //-->
	<div id="right">
		<div class="righttop">
		<b>Solutions</b></div>
		<div class="rightsub">
		Solution 1 <br> Solution 2 <br> Solution 3 <br> Solution 4  </div>
	</div>
</div>


<!-- footer //-->
<div id="foot">$footer$</div>

</body></html>