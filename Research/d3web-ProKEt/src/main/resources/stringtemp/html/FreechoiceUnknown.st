<div id="$fullId$" class="answer $fullId$ $if(realAnswerType)$answer-$realAnswerType$$endif$" 
	onClick="javascript:flat_button_toggle('$fullId$');" $if(style)$ style="$style$;"$endif$>

	<div id="unknown_$fullId$" class="f_$fullId$ $if(selection)$preselected$else$not-selected$endif$ 
		$selection$ $fullId$-tt-trigger $ratingClass$">
		 
		Unknown
		$TooltipPure()$ 
		$Image()$ 
		$text$
		</div>
</div>