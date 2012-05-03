$HTMLHeader()$

<body>
<div id="head">
        <div id='headertext'>$header$</div>
        
        $if(study)$
            <div class="rightButton">
                $ButtonEndStudySession()$
            </div>
        $elseif(ueq)$
            <div class="leftButton">
                $ButtonUEQuestionnaire()$
            </div>
        $endif$

        <div class="rightButton">
            $FeedbackButton()$
        </div>
</div>

<div id="content">

    $if(title)$<div id="dialog-title">$title$</div>$endif$
			$if(text)$$text$
    $endif$

    
    <div id="intro">
        Um eine Bewertung des Sachverhalts ("Hauptfrage") zu bekommen, beantworten Sie bitte die
        unterhalb der Hauptfrage angezeigten Fragen.
        Wenn Sie eine Frage nicht direkt beantworten können, klicken Sie auf das Dreieck vor der Frage um 
        detailliertere Fragen anzuzeigen. 
        <br />
        <b>Wenn Sie die Beratungs-Session als beendet sehen (weil sie alle für den Sachverhalt relevanten Fragen beantwortet
        haben und/oder mit dem Ergebnis zufrieden sind) bestätigen Sie dies bitte per Klick auf
        den Button <i>End Session</i> und beantworten Sie den anschließend angezeigten Kurzfragebogen!</b>.
    
        <div id="solution" class="solution">
            <div id="solutionboxtext">Bewertung Hauptfrage</div>

            <div id="solutiontext">Ist das Arbeitsverhältnis wirksam gekündigt worden?</div>

            <div id="solHigh" class="solRating rating-high hide">
                Sachverhalt bestätigt, <br />weil alle Top-Level Fragen bestätigt werden konnten.
            </div>
            <div id="solMed" class="solRating rating-medium hide">
                Sachverhalt nicht sicher entscheidbar, <br />weil nicht alle Top-Level Fragen sicher bewertet werden konnten.
            </div>
            <div id="solLow" class="solRating rating-low hide">
                Sachverhalt trifft nicht zu, <br /> weil nicht alle Top-Level Fragen bestätigt werden konnte.
            </div>
            <div id="solUn" class="solRating"><br />
                Zu wenig Informationen für eine Bewertung.
            </div>
        </div>


    </div>
    
    

    <div id="dialog" class="dialog75Width">
         $children$	
    </div>

    <div id="auxpanel" class="auxpanelRight">

        <div id="auxpanelHeader">
            <div id="auxpanelHeadline">- Weitere Informationen - </div>
            <div id="auxpanelToggle" class="auxpanelButton rightButton" onclick="toggleAuxInfoPlacing()">
                Nach unten
            </div>
        </div>
        
        <div id="auxHeader">
            <b>FRAGE:</b> 
            <br /><br />
        </div>

        
        <div id="auxInfo">
        -
        </div>

        <div id="linkedResources">
            
        </div>
        
    </div>
    
                 

</div>
</body>
</html>