<div id="$fullId$" class="question $fullId$ $answerType$ $andOrType$" inter="$defmin$-$defmax$">
   
    <!--this is the opening/close arrow image-->
    <div id="$id$-imagebox" class="question-imagebox" 
            onmouseover="showAuxInfo('$fullId$')" onmouseout="hideAuxInfo()">
	<img id="$id$-typeimg" alt="type" src="$typeimg$" height="16" width="35" 
            onclick="toggle_sub_4boxes('$fullId$')" />
    </div> 

    <!--NUM VALUE ENTRY-->
    <div id=$id$-numentry class="floatingnum"
             onmouseover="showAuxInfo('$fullId$')" onmouseout="hideAuxInfo()">
        <input name="$id$-numentry" type="num" size="5" maxlength="5" value="" min="$defmin$" max="$defmax$"/>
    </div>
   
    <!--DISPLAY OF QUESTION-->
    <div class="title t-$fullId$" id="t-$fullId$" 
        onmouseover="showAuxInfo('$fullId$', '$title$')" 
        onmouseout="hideAuxInfo()">
            $title$
            $Tooltip()$
            $text$
		
        <!--FOR PROPAGATING COMPUTER-DERIVED VALUES (NEEDED FOR JS MECHANISMS)-->
        <div id="propagation-$fullId$" class="propagation"></div>
	
        <!--ADD CHILDREN IF EXIST-->
        $if(children)$
            <div id="sub-$fullId$" class="sub-$fullId$" style="padding-left:30px;display:none">
                $children$
            </div>
        $endif$

        <!--STORAGE OF AUXILIARY INFORMATION-->
        <div id="bonus-$fullId$" style="display:none">$bonusText$</div>
    </div>
</div>