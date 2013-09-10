$HTMLHeader()$

<body>
<div id="head">$header$</div>
<div id="middle">
	<div id="content">
		<div class="dialog">
			 $if(title)$<div id="dialog-title">$title$</div>$endif$$text$
			<div id="dialog-content">
				$children$
			</div>
		</div>
	</div>
</div>
</body></html>