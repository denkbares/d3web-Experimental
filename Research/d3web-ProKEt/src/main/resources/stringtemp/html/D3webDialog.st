$HTMLHeader()$
<body>

<!-- header //-->
<div id="head">
	<div id="reset">
		$if(resetimage)$<img src="img/$resetimage$" alt="Reset" />$endif$
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
				
				<!-- questionnaire navigation ("next") //-->
				$if(questionnaireNav)$<div id="x_questionnairenav"><a href="javascript:next_questionnaire();"><img id="x_questionnairenav_next" src="images/blank.gif" border="0" /></a></div>$endif$
			</div>
			
		</div>
	</div>
	
	<!-- box on the right side //-->
	<div id="right">$sidetext$</div>

<div id="fin">$fin$</div>
</div>

<!-- footer //-->
<div id="foot">$footer$</div>

</body></html>