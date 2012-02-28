<div id="$fullId$" class="question $fullId$ $answerType$ $andOrType$" defining=$defining$>
	
    <!--Dropdown for choice display and selection-->
    <div id=$id$-choicesDropdown class="floatingCDrop"
             onmouseover="showAuxInfoOQD('$fullId$')" onmouseout="hideAuxInfo()">
        <select id="$fullId$-dropdown">
            $choicesDropdownOptions$
        </select>
    </div>

    <div class="title t-$fullId$" id="t-$fullId$" onmouseover="showAuxInfoOQD('$fullId$', '$title$')" 
    $if(children)$onclick="toggle_sub_4oqd('$fullId$')"$endif$>
        $title$
	$Tooltip()$
	$Image()$
	$text$
    </div>
        
    $if(children)$
        <div id="sub-$fullId$" class="sub-$fullId$ hide">
            $children$
        </div>
    $endif$
	
    <div id="bonus-$fullId$" style="display:none">$bonusText$</div>

<div id="auxpanel-$fullId$" class="auxpanelBottom">

        <div id="auxPropagationInfo-$fullId$" class="auxPropagationInfo">
        </div>
        
        <div id="auxHeader-$fullId$">
            <b>Informationen zu []:</b>  
            <br /><br />
        </div>
        
        <div id="auxInfo-$fullId$">
        </div>

    </div>
</div>



