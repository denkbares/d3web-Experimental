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
           Wenn Sie die Maus über den Text einer Frage bewegen, bekommen Sie dazu rechts Zusatzinformationen angezeigt. 
           Wenn Sie eine Frage nicht direkt beantworten können, klicken Sie auf das Dreieck vor der Frage: dann werden
           detailliertere Fragen dazu eingeblendet. 
    </div>
    
    <div id="dialog">
         $children$	
    </div>

    <div id="auxpanel">
        <div id="auxHeader">
        <b>Informationen zu []:</b> <br /><br />
        </div>
        
        <div id="auxInfo">
        -
        </div>
    </div>
    
                 

</div>
</body>
</html>