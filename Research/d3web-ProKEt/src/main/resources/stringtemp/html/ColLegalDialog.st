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
            Dieser Dialog bewertet die Diagnose "Arbeitsverhältnis ist wirksam und termingerecht gekündigt worden".
            Um (weitere) klärende Fragen - in Aussagenform - auszuklappen und zu beantworten, klicken Sie auf das ">" (Pfeil) Symbol.<p>
            Bitte bewerten Sie die nachfolgenden Aussagen nach dem Schema "trifft zu" (Haken), "trifft nicht zu" (Kreuz),
	    oder "weiß nicht" (Fragezeichen). Um eine Antwort zurückzunehmen klicken Sie das leere Quadrat.
    </div>
    
    <div id="dialog">
         $children$	
    </div>

    <div id="auxpanel">
        <div id="infopanel">
          No additional information available for current dialog element.
        </div>
    </div>
    
                 

</div>
</body>
</html>