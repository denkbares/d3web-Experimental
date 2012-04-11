
<div id="readFlow-$fullId$" class="readflow">
    <img id="$fullid$-readimg" alt="readflow" src="$readimg$" height="14" width="25">
</div>
 
<div id="$fullId$" class="question $fullId$ $answerType$ $andOrType$" >
    
    <div id="$id$-imagebox" class="question-imagebox" onmouseover="showAuxInfo('$fullId$')">
        <img id="$id$-typeimg" alt="type" src="$typeimg$" height="20" width="20" onclick="toggle_sub_4boxes('$fullId$')">
            <div id="ynYes-$fullId$" class="ynbutton $ratingY$ $fullId$-tt-trigger-ynbutton" onclick="handleOQYNQuestions('ynYes-$fullId$', '$ratingNrY$')">
                Ja
                $TooltipButtonJA()$
            </div>
            <div id="ynNo-$fullId$" class="ynbutton $ratingN$ $fullId$-tt-trigger-ynbutton" onclick="handleOQYNQuestions('ynNo-$fullId$', '$ratingNrN$')">
                Nein
                $TooltipButtonNEIN()$
            </div>
            <div id="ynUn-$fullId$" class="ynbutton rating-medium $fullId$-tt-trigger-ynbutton" onclick="handleOQYNQuestions('ynUn-$fullId$', '2')">
                -?-
                $TooltipButtonWN()$
            </div>
            <div id="ynNan-$fullId$" class="ynbutton rating-none $fullId$-tt-trigger-ynbutton" onclick="handleOQYNQuestions('ynNan-$fullId$', '0')">
                X
                $TooltipButtonZ()$    
            </div>
            
    </div> 
    <div class="title t-$fullId$" id="t-$fullId$" onmouseover="showAuxInfo('$fullId$', '$title$')">
        <div id="solutiontitle-$fullId$" class="solutiontitle">$title$</div>
            $Image()$
		$text$
		<div id="propagation-$fullId$" class="propagation hide $if(tooltip)$$fullId$-tt-trigger tooltip-trigger$endif$" >
                    $if(tooltip)$<div id="tt-$fullId$" class=" tooltip tooltipProp tt-$fullId$">$tooltip$</div>$endif$
                </div>
	</div>
        
        
	
	$if(children)$<div id="sub-$fullId$" class="sub-$fullId$" style="padding-left:15px;display:none">
		$children$
	</div>$endif$
	
	<div id="bonus-$fullId$" style="display:none">$bonusText$</div>
</div>