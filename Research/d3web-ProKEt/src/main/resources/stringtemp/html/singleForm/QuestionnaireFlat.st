<div id="$fullId$" $if(hidden)$style="display: none"$endif$ class="questionnaire $fullId$ $if(questInactive)$ inactive $endif$ ">
    <div class="row">
        
        <div class="questionnaireTitle cell">
            $if(count)$$count$.$endif$
        </div>
        <div class="questionnaireTitle cell">
            $title$$Tooltip()$
        </div>
    </div>
    
	
	$if(children)$ 
	<div id="content-$fullId$" class="content-$fullId$ questionnaire-content" >
		$children$
	</div>
	$endif$
</div>