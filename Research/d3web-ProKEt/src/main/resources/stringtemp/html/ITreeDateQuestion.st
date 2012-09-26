
<div id="readFlow-$fullId$" class="readflow">
    <img id="$fullId$-readimg" alt="readflow" src="$readimg$" 
 $if(imgheight)$height="$imgheight$"$else$height="14"$endif$ 
    $if(imgwidth)$width="$imgwidth$"$else$width="25"$endif$ 
 $if(qtype)$qtype="$qtype$"$endif$>
</div>
 
<div id="$fullId$" class="question $fullId$ $answerType$ $andOrType$ $qrating$ $swap$" >
    
    <div id="$fullId$-imagebox" class="date-imagebox" onmouseover="showAuxInfo('$fullId$')">
        <img id="$fullId$-typeimg" alt="type" src="$typeimg$" height="20" width="20" onclick="toggle_sub_4boxes_clariHie('$fullId$')">
            
        <!--DATE VALUE ENTRY-->
        <div id="$fullId$" onmouseover="showAuxInfo('$fullId$')" onmouseout="hideAuxInfo()" class="answer">
            $dropdown$
        </div>
        
    </div> 
    <div class="title titleDate t-$fullId$" id="t-$fullId$" onmouseover="showAuxInfo('$fullId$', '$title$')">
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