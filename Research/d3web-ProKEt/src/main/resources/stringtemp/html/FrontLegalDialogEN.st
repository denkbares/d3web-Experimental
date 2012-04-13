$HTMLHeader()$

<body>
<div id="head">
        <div id='headertext'>$header$</div>
        
        $if(study)$
            <div class="rightButton">
                $ButtonEndStudySession()$
            </div>
        $elseif(ueq)$
            <div class="leftButton">
                $ButtonUEQuestionnaire()$
            </div>
        $endif$

        <div class="rightButton">
            $FeedbackButton()$
        </div>
</div>

<div id="content">

    $if(title)$<div id="dialog-title">$title$</div>$endif$
			$if(text)$$text$
    $endif$

    
    <div id="intro">
        Do derive a rating for the core issue displayed in the solution panel above the tree
        (and also as top element of the tree itself) please answer the questions underneath in the tree.
        <b>Please confirm the end of your session by clicking the <i>End Session</i> button!</b>.
    </div>
    
    <div id="solution" class="solution">
            <div id="solutionboxtext">Core Issue Rating</div>

            <div id="solutiontext">Was the working contract terminated effectually?</div>

            <div id="solHigh" class="solRating rating-high hide">
                Core issue confirmed.
            </div>
            <div id="solMed" class="solRating rating-medium hide">
                Core issue rated not confidently / neutral.
            </div>
            <div id="solLow" class="solRating rating-low hide">
                Core issue rejected.
            </div>
            <div id="solUn" class="solRating"><br />
                Too little information for rating the core issue.
            </div>
        </div>

    <div id="dialog" class="dialog75Width">
         $children$	
    </div>

    <div id="auxpanel" class="auxpanelRight">

        <div id="auxpanelHeader">
            <div id="auxpanelHeadline">Additional Information</div>
            <div id="auxpanelToggle" class="auxpanelButton rightButton" onclick="toggleAuxInfoPlacing()">
                To bottom
            </div>
        </div>
           
        
        <div id="auxPropagationInfo" class="auxPropagationInfo">
            
        </div>
        <div id="auxHeader">
            <b>Additional information for []:</b>  
            <br /><br />
        </div>

        
        <div id="auxInfo">
        -
        </div>

        <div id="linkedResources">
            
        </div>
        
    </div>
    
                 

</div>
</body>
</html>