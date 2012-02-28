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
        Here goes some general info regarding (the usage) of the oqd...
    </div>
    
    <div id="dialog" class="dialog99Width">
         $children$	
    </div>

    
    
                 

</div>
</body>
</html>