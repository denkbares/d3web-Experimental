$HTMLHeader()$

<body>
<div id="head">
	$header$
</div>

<div id="content">

    $if(title)$<div id="dialog-title">$title$</div>$endif$
			$if(text)$$text$
    $endif$

    
    <div id="intro">
        <div id="introtext">
        Bitte beantworten Sie die jeweils aktuell angezeigte Frage. Können Sie 
        eine Frage nicht direkt beantworten, können - falls vorhanden - mittels Klick auf den
        'Details' Button weitere, verfeinernde Fragen angefordert werden. Beantwortete Fragen   
        werden automatisch vom System auf eine kompaktere Darstellung "zugeklappt"; 
        die Antwort kann dennoch nachträglich durch Klick auf die gewünschte Antwortalternative 
        noch geändert werden.<br />
        Bewertet wird der im Diagnose-Panel angezeigte Sachverhalt; die aktuelle Bewertung
        wird aufgrund der eingegebenen Daten vom System automatisch jederzeit berechnet.
        Nach Beendigung einer Beratungs-Session können die Antworten noch weiter geändert
        werden, das System aktualisiert das Ergebnis dementsprechend.
        </div>

        <div id="solution" class="solution">
            <div id="solutionboxtext">Diagnose-Panel</div>

            <div id="solutiontext">Ist das Arbeitsverhältnis wirksam gekündigt worden?</div>

            <div id="solHigh" class="solRating rating-high hide">
                Sachverhalt bestätigt.
            </div>
            <div id="solMed" class="solRating rating-medium hide">
                Sachverhalt nicht sicher entscheidbar.
            </div>
            <div id="solLow" class="solRating rating-low hide">
                Sachverhalt trifft nicht zu.
            </div>
            <div id="solUn" class="solRating">
                Zu wenig Informationen für eine Bewertung.
            </div>
        </div>
    </div>
    
    <div id="dialog" class="dialog99Width">
         $children$	
    </div>

</div>
</body>
</html>