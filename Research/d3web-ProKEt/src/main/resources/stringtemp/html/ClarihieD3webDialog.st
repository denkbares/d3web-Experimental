$HTMLHeader()$

<body>

$if(login)$
	$LoginDialog()$
$endif$

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
				$if(summary)$
					<div class="leftButton">
						$SummaryButton()$
					</div>
				$endif$
                       </td>
			<td width=*>
                            <div id="headertext">
				$headertext$
                            </div>
            		</td>
			<td width="40%">
                            $if(feedback)$
                                <div class="rightButton">
                                    $FeedbackButton()$
                                </div>
                            $endif$
                            $if(ueq)$
                                <div class="rightButton">
                                    $ButtonUEQuestionnaire()$
                                </div>
                            $endif$
			</td>
		</tr>
	</table>
</div>

<div id="content">
    <div id="intro">
        INTROTEXT...
    </div>

    <div id="solution" class="solution">
            <div id="solutionboxtext">Bewertung Hauptfrage</div>

            <div id="solutiontext">Ist das Arbeitsverhältnis wirksam gekündigt worden?</div>

            <div id="solHigh" class="solRating rating-high hide">
                Sachverhalt bestätigt, <br />weil Frage 1 und Frage 2 bestätigt werden konnten.
            </div>
            <div id="solMed" class="solRating rating-medium hide">
                Sachverhalt nicht sicher entscheidbar, <br />weil Frage 1 oder Frage 2 nicht sicher entschieden werden konnten.
            </div>
            <div id="solLow" class="solRating rating-low hide">
                Sachverhalt trifft nicht zu, <br />weil Frage 1 oder Frage 2 nicht bestätigt werden konnte.
            </div>
            <div id="solUn" class="solRating"><br />
                Zu wenig Informationen für eine Bewertung.
            </div>
        </div>

    <div id="dialog" class="dialog75Width">
         $children$	
    </div>

    <div id="auxpanel" class="auxpanelRight">

        <div id="auxpanelToggle" class="auxpanelButton" onclick="toggleAuxInfoPlacing()">
            Nach unten
        </div>   
        
        <div id="auxPropagationInfo" class="auxPropagationInfo">
            
        </div>
        <div id="auxHeader">
            <b>Informationen zu []:</b>  
            <br /><br />
        </div>

        
        <div id="auxInfo">
        -
        </div>

        
    </div>
</div>
$if(logging)$
	$EoSDialog()$
$endif$


</body></html>