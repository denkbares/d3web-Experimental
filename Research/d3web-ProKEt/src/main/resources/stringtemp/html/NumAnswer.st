<div id="$fullId$" class="answer $fullId$  $if(realAnswerType)$answer-$realAnswerType$$endif$">
	<input id="f_$fullId$" type="num" width="6" $if(selection)$value="$selection$"$endif$
	$if(left)$left="$left$"$endif$ $if(right)$right="$right$"$endif$ 
	$if(readonly)$ DISABLED class="disabled" $endif$ name="group-$parentFullId$"/>
	$AnswerTitleTooltip()$
	$text$
	
</div>