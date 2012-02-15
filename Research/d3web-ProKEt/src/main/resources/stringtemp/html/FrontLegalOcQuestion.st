<div id="readFlow-$fullId$" class="readflow">
    <img id="$fullid$-readimg" alt="readflow" src="$readimg$" height="14" width="25">
</div>
 
<div id="$fullId$" class="question $fullId$ $answerType$ $andOrType$" defining=$defining$>
	<div id="$id$-imagebox" class="question-imagebox" onmouseover="showAuxInfo('$fullId$')" onmouseout="hideAuxInfo()">
            <img id="$id$-typeimg" alt="type" src="$typeimg$" height="20" width="35" onclick="toggle_sub_4boxes('$fullId$')">
            
        </div> 
    
<!--Dropdown for choice display and selection-->
    <div id=$id$-choicesDropdown class="floatingCDrop"
             onmouseover="showAuxInfo('$fullId$')" onmouseout="hideAuxInfo()">
        
        <select id="$fullId$-dropdown">
            $choicesDropdownOptions$
        </select>
    </div>

<div class="title t-$fullId$" id="t-$fullId$" onmouseover="showAuxInfo('$fullId$', '$title$')" onmouseout="hideAuxInfo() ">
        $title$
		$Tooltip()$
		$Image()$
		$text$
		<map name="panel-$fullId$">
			<area href="javascript:h4boxes('1','panel-$fullId$')" coords="0,0,21,16" shape="rect">
			<area href="javascript:h4boxes('3','panel-$fullId$')" coords="21,0,42,16" shape="rect">
			<area href="javascript:h4boxes('2','panel-$fullId$')" coords="42,0,63,16" shape="rect">
			<area href="javascript:h4boxes('0','panel-$fullId$')" coords="63,0,84.0,16" shape="rect">
		</map>
                <div id="propagation-$fullId$" class="propagation"></div>
	</div>
        
        
	
	$if(children)$<div id="sub-$fullId$" class="sub-$fullId$" style="padding-left:30px;display:none">
		$children$
	</div>$endif$
	
	<div id="bonus-$fullId$" style="display:none">$bonusText$</div>
</div>



