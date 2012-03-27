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
        Wenn Sie die Maus über den Text einer Frage bewegen, bekommen Sie dazu im Panel rechts Zusatzinformationen angezeigt 
        (mittels des <i>Nach unten</i> Button können Sie dieses Panel auch an den unteren Bildschirmrand legen).
        Wenn Sie eine Frage nicht direkt beantworten können, klicken Sie auf das Dreieck vor der Frage: dann werden
        detailliertere Fragen dazu eingeblendet. 
        <br />
        <b>Wenn Sie die Beratungs-Session als beendet sehen (weil sie alle für den Sachverhalt relevanten Fragen beantwortet
        haben und/oder mit dem Ergebnis zufrieden sind) bestätigen Sie dies bitte per Klick auf
        den Button <i>End Session</i> und beantworten Sie den anschließend angezeigten Kurzfragebogen!</b>.
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
</body>
</html>