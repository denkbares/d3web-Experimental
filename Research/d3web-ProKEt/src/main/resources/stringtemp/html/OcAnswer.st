<div id="$fullId$"  class="answer $fullId$ $if(realAnswerType)$answer-$realAnswerType$$endif$" >
	<input id="f_$fullId$" name="group-$parentFullId$" type="radio" $selection$ title="$title$" 
	$if(readonly)$ DISABLED $endif$ />	
	$AnswerTitleTooltip()$
	$text$
</div>