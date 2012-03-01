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
        <div id="solution" class="solution hide">
    Ist das Arbeitsverhältnis wirksam gekündigt worden?
    <div id="solHigh" class="solRating rating-high hide">
    wird bestätigt.
    </div>
    <div id="solMed" class="solRating rating-medium hide">
    ist unentschieden.
    </div>
    <div id="solLow" class="solRating rating-low hide">
    wird verneint.
    </div>
<div id="solUn" class="solRating hide">
    ist nicht bewertet.
    </div>
    </div>
    </div>
    
    <div id="dialog" class="dialog99Width">
         $children$	
    </div>

    
    
    
                 

</div>
</body>
</html>