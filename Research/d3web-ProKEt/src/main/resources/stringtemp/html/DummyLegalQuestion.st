<div id="$fullId$" class="question $fullId$ $answerType$ $andOrType$">
	<div id="$id$-imagebox" class="question-imagebox" onmouseover="showAuxInfo('$fullId$')" onmouseout="hideAuxInfo()">
            <img id="$id$-typeimg" alt="type" src="$typeimg$" height="16" width="35" onclick="toggle_sub_4boxes('$fullId$')">
            <img height="16" 
		$if(noChildren)$usemap="#panel-$fullId$"$endif$ 
		src="img/pane.png" style="border: medium none;" id="panel-$fullId$" 
		$if(hasChildren)$onclick="toggle_sub_4boxes('$fullId$')"$endif$>
        </div> 
    <div class="title t-$fullId$" id="t-$fullId$ " onmouseover="showAuxInfo('$fullId$', '$title$')" onmouseout="hideAuxInfo()">
        
		
        <div id="propagation-$fullId$" class="propagation"></div>
    </div>
	
    $if(children)$<div id="sub-$fullId$" class="sub-$fullId$" style="padding-left:30px;display:none">
	$children$
    </div>$endif$
	
    <div id="bonus-$fullId$" style="display:none">$bonusText$</div>
</div>