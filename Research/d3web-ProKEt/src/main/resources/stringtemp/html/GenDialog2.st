$HTMLHeader()$

<body>
<div id="head">
	$header$
</div>

<div id="middle">
	
	<div id="left">		
		<div class="lefttop">
		<b>Questionnaires</b></div>
		
		<div class="leftsub">
		<div id="x_nav">
		</div></div>
	</div>
	
	<div id="content">
		<div class="dialog">
			 $if(title)$<div id="dialog-title">$title$</div>$endif$$text$
		
			<div id="dialog-content">
				$children$
			</div>
		</div>
	</div>
	
	<div id="right">
		<div class="righttop">
		<b>Solutions</b></div>
		<div class="rightsub">
		Solution 1 <br> Solution 2<br> Solution 3<br> Solution 4<br> </div>
	</div>
</div>

<div id="foot">
	$footer$ 
</div>

</body></html>