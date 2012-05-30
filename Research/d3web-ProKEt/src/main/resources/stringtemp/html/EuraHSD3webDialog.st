$HTMLHeader()$
<body>

$if(login)$
	$LoginDialog()$
$endif$

<!-- header & footer in one //-->
<div id="head" style="z-index: 1000; top:0px;">
	<table>
	<tr>
		<td width="35%" cellspacing="0" cellpadding="0">
			$EuraHSTabularButtons()$
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

<div id="eurahsintroscreen" style="$if(eurahsintro)$display: block;$else$display: none;$endif$ margin-top:175px; width:780px;">

    <div id="ehsintrotext" style="width: 780px; margin-left: auto; margin-right: auto; text-align: center;">
        <div style="font-weight: bold;font-size: 2em;">   
            Welcome to the 
        </div>
        <div style="font-size: 2em; color:#984806;margin-top: 15px;">
            EUROPEAN REGISTRY OF ABDOMINAL WALL HERNIAS, 
        </div>
        <div style="font-size: 2em;margin-top: 15px">
            a platform for registration and outcome measurement<br />of abdominal wall surgery.
        </div>      
        <div style="font-size: 1.4em;margin-top: 35px;text-align:justify;">
            The aim of the EuraHS platform is to improve the quality and the quantity of outcome reports in surgery and thus provide more evidence to support future guidelines. The EuraHS platform can be used for patient registration from either one surgeon, one hospital, one country or even on a multicentre and international level.
            <br /><br />
            Please Note:<br/> 
            The platform is required to be completely anonymous. Therefore, it should not contain any patient data like name, date of birth, social security number or address of the patient.
        </div>
        <div style="font-size: 1.4em;margin-top: 60px;text-align:left; font-weight: bold;">
            Please choose one of the following applications:
        </div>
    </div>    
    <div id="newcaseintrobutton" class="eurahsintrobutton">
        <img src="img/EuraHSNew.png" alt="New Case" style="width: 175px;">
    </div>

    <div id="loadcaseintrobutton" class="eurahsintrobutton">
        <input type="image" src="img/EuraHSLoad.png" alt="Load Case" style="width: 175px;">
    </div>

    <div id="statisticsintrobutton" class="eurahsintrobutton">
        <img src="img/EuraHSStats.png" alt="New Case" style="width: 175px;">
    </div>

    <div id="creategroupsintrobutton" class="eurahsintrobutton">
        <img src="img/EuraHSGroups.png" alt="New Case" style="width: 175px;">
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