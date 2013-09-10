<div id="$fullId$" class="question $fullId$ $answerType$ $andOrType$" inter="$defmin$-$defmax$">

    <!--NUM VALUE ENTRY-->
    <div id=$id$-numentry class="floatingnum"
             onmouseover="showAuxInfoOQD('$fullId$')" >
        <input name="$id$-numentry" type="num" size="5" maxlength="5" value="" min="$defmin$" max="$defmax$"/>
    </div>
   
    <!--DISPLAY OF QUESTION-->
    <div class="title t-$fullId$" id="t-$fullId$" 
        onmouseover="showAuxInfoOQD('$fullId$', '$title$')" 
        $if(children)$onclick="toggle_sub_4oqd('$fullId$')"$endif$>
        $title$
        $Tooltip()$
        $text$
    </div>	
        <!--FOR PROPAGATING COMPUTER-DERIVED VALUES (NEEDED FOR JS MECHANISMS)-->
        <div id="propagation-$fullId$" class="propagation hide"></div>
	
        <!--ADD CHILDREN IF EXIST-->
        $if(children)$
            <div id="sub-$fullId$" class="sub-$fullId$ hide">
                $children$
            </div>
        $endif$

        <!--STORAGE OF AUXILIARY INFORMATION-->
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