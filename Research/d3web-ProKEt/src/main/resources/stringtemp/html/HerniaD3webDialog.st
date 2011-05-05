$HTMLHeader()$
<body>

$if(login)$
	$LoginDialog()$
$endif$

<!-- header & footer in one //-->
<div id="head">
	<table>
	<tr>
	<td width="35%">
		$if(reset)$
			<div class="leftButton">
				$CaseNewButton()$
			</div>
		$endif$
		$if(loadcase)$
			<div class="leftButton">
				$CaseLoadButton()$
			</div>
		$endif$
		$if(summary)$
			<div class="leftButton">
				$SummaryButton()$
			</div>
		$endif$
		$if(followup)$
			<div class="leftButton">
				$FollowUpButton()$
			</div>
		$endif$
		
		$if(statistics)$
			<div class="leftButton">
				$StatisticsButton()$
			</div>
		$endif$
		
		$if(savecase)$
			<div class="leftButton">
				$CaseSaveButton()$
			</div>
		$endif$

	</td>
	<td width=*>
		
		<div class="header">
			$header$
		</div>
		
	</td>
	<td width="35%">
		<div class="langSelect">
			$LanguageSelectFlags()$
		</div>
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
			 $TitleTooltip()$$text$
			
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