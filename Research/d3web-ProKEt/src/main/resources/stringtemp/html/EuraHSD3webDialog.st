$HTMLHeader()$
<body>

$if(login)$
	$LoginDialog()$
$endif$

<!-- header & footer in one //-->
<div id="head" style="z-index: 1000">
	<table>
	<tr>
		<td width="35%" cellspacing="0" cellpadding="0">
			$EuraHSTabbedButtons()$
		</td>
		<td width=*>
			
			<div class="header">
				$header$
			</div>
			
		</td>
		<td width="35%">
			<div class="langSelect">
				$RightHeaderLanguageLogoRoute()$
			</div>
		</td>
	</tr>
	</table>
</div>

<div id="eurahsintroscreen" style="$if(eurahsintro)$display: block;$else$display: none;$endif$">


    <div id="newcaseintrobutton" class="eurahsintrobutton">
        <a href="$eurahsintronewlink$">
        <img src="img/EuraHSNew.png" alt="New Case" style="width: 200px;">
        </a>
    </div>

    <div id="loadcaseintrobutton" class="eurahsintrobutton">
        <input type="image" src="img/EuraHSLoad.png" alt="Load Case" style="width: 200px;">
    </div>

    <div id="statisticsintrobutton" class="eurahsintrobutton">
        <a href="$eurahsintrostatslink$">
        <img src="img/EuraHSStats.png" alt="New Case" style="width: 200px;">
        </a>
    </div>

    <div id="creategroupsintrobutton" class="eurahsintrobutton">
        <a href="$eurahsintrogroupslink$">
        <img src="img/EuraHSGroups.png" alt="New Case" style="width: 200px;">
        </a>
    </div>


</div>


<div id="middle" style="$if(eurahsmiddle)$display: block;$else$display: none;$endif$">
	
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
</div>


</body></html>