
<div id="readFlow-$fullId$" class="readflow">
    <img id="$fullId$-readimg" alt="readflow" src="$readimg$" height="14" width="25" $if(qtype)$qtype="$qtype$"$endif$>
</div>
 
<div id="$fullId$" class="question $fullId$ $answerType$ $andOrType$ $qrating$ $swap$" >
    
    <div id="imagebox-$fullId$" class="question-imagebox" onmouseover="showAuxInfo('$fullId$')">
        <img id="$fullId$-typeimg" alt="type" src="$typeimg$" height="20" width="20" onclick="toggle_sub_4boxes_clariHie('$fullId$')">
            
        <!--NUM VALUE ENTRY-->
        <div id=$id$-numentry class="floatingnum"
             onmouseover="showAuxInfo('$fullId$')" onmouseout="hideAuxInfo()">
            <input name="$id$-numentry" type="num" size="9" maxlength="9" min="$defmin$" max="$defmax$" $if(selection)$value="$selection$"$endif$/>
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
	
	$if(children)$<div id="sub-$fullId$" class="sub-$fullId$" style="padding-left:15px;$if(showitree)$display:block$else$display:none$endif$">
		$children$
	</div>$endif$
	
	<div id="bonus-$fullId$" style="display:none">$bonusText$</div>
</div>