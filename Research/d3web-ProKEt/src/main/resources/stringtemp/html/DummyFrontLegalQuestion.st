
<div id="readFlow-$fullId$" class="readflow">
    <img id="$fullid$-readimg" alt="readflow" src="$readimg$" height="14" width="25">
</div>
 
<div id="$fullId$" class="question $fullId$ $answerType$ $andOrType$ dummy" >
    
    <div id="$id$-imagebox" class="question-imagebox" onmouseover="showAuxInfo('$fullId$')">
        <img id="$fullId$-typeimg" alt="type" src="$typeimg$" height="20" width="20" onclick="toggle_sub_4boxes('$fullId$')">
        
    </div> 
    <div class="title t-$fullId$" id="t-$fullId$" onmouseover="showAuxInfo('$fullId$', '$title$')">
        <div id="solutiontitle-$fullId$" class="solutiontitle"></div>
           
            <div id="propagation-$fullId$" class="propagation hide" onmouseover="showAuxPropInfo('$fullId$')" onmouseout="hideAuxPropInfo('$fullId$')"></div>
	</div>
        
        
	
	$if(children)$<div id="sub-$fullId$" class="sub-$fullId$" style="padding-left:15px;display:none">
		$children$
	</div>$endif$
	
	<div id="bonus-$fullId$" style="display:none">$bonusText$</div>
</div>