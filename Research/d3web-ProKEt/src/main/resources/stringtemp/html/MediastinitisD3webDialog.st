$HTMLHeader()$
<body>

$if(login)$
	$LoginDialogMediastinitis()$
$endif$

<!-- header & footer in one //-->
<div id="head">
	<table>
	<tr>
	<td width="40%">
		$if(loadcase)$
			<div class="leftButton">
				$CaseLoadButton()$
			</div>
		$endif$
		$if(savecase)$
			<div class="leftButton">
				$CaseSaveButton()$
			</div>
		$endif$
		$if(reset)$
			<div class="leftButton">
				$CaseNewButton()$
			</div>
		$endif$
	</td>
	<td width=*>
		$header$
	</td>
	<td width="40%">
		<!--$if(summary)$
			<div class="rightButton">
				$SummaryButton()$
			</div>
		$endif$ -->
	</td>
	</tr>
	</table>
</div>


<div id="middle">
	<!-- navigation on the left side //-->
	<div id="left">$NavigationBox()$$left$</div>
	
	<!-- actual content //-->
	<div id="content">
		<div class="dialog">
			$text$
			
			<div class="dialog-content">
				<form autocomplete="off">
				$children$
				</form>
				
				<!-- questionnaire navigation ("next") //-->
				$if(questionnaireNav)$<div id="x_questionnairenav"><a href="javascript:next_questionnaire();"><img id="x_questionnairenav_next" src="images/blank.gif" border="0" /></a></div>$endif$
			</div>
			
		</div>
	</div>
	
	<!-- box on the right side //-->
	<div id="right">$sidetext$</div>
</div>


</body></html>