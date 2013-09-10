$HTMLHeader()$
<body>

<!-- header //-->
<div id="head">
	<div id="sendall">
		<input type="button" name="sendall_bottom" value="Formular senden"
	      onclick="javascript:sendall();">
	</div>
	$header$
</div>

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
<div id="foot">
		<!-- send-all button //-->
		<!--$if(sendall)$ //-->
		
		<!-- $endif$//-->
</div>

</body></html>