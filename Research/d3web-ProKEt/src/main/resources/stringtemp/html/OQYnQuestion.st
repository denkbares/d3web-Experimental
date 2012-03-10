<div id="$fullId$" class="question $fullId$ $answerType$ $andOrType$ hide">
    <div class="title t-$fullId$ $hide$" id="t-$fullId$" onmouseover="showAuxInfoOQD('$fullId$', '$title$')"> 
        
        <div id="$id$-imagebox" class="question-imagebox">
            <div id="ynYes-$fullId$" class="ynbutton" onclick="handleOQYNQuestions('ynYes-$fullId$', '1')"> Ja </div>
            <div id="ynNo-$fullId$" class="ynbutton" onclick="handleOQYNQuestions('ynNo-$fullId$', '3')">Nein</div>
            <div id="ynUn-$fullId$" class="ynbutton" onclick="handleOQYNQuestions('ynUn-$fullId$', '2')">-?-</div>
            <div id="ynNan-$fullId$" class="ynbutton" onclick="handleOQYNQuestions('ynNan-$fullId$', '0')"> </div>
        </div> 
        
        <div id="titletext-$fullId$" class="titletext" onclick="toggleDetails($fullId$)">
                <div id="solutiontitle-$fullId$" class="solutiontitle">$questioncount$ $title$</div>
                $Tooltip()$
                $Image()$
                $text$
            
            <div id="propagation-$fullId$" class="propagation hide" onmouseover="" onmouseout=""></div>
        </div>
        
        <div id="detail-$fullId$" class="detailButton $hideDetails$" onclick="stepIntoDetail('$fullId$')"> 
            Details
        </div>
   
        
        <div id="auxpanel-$fullId$" class="auxpanelBottom">
            <div id="auxPropagationInfo-$fullId$" class="auxPropagationInfo">
            </div>
        
            <div id="auxInfo-$fullId$">
                $bonusText$
            </div>
        </div>    
    
    </div>
        
    $if(children)$
        <div id="sub-$fullId$" class="sub-$fullId$ hide">
            $children$
	</div>
    $endif$
	
    <div id="bonus-$fullId$" style="display:none">$bonusText$</div>

</div>