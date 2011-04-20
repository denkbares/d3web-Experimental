<div id="$fullId$" class="answer $fullId$ $if(realAnswerType)$answer-$realAnswerType$$endif$">
	$title$
	
	$if(readonly)$
		<input id="f_$fullId$" type="readonly" $if(selection)$value="$selection$"$endif$ title="$title$" $if(width)$size="$width$"$endif$ />
	$else$
		<input id="f_$fullId$" type="text" $if(selection)$value="$selection$"$endif$ title="$title$" $if(width)$size="$width$"$endif$ />
	$endif$
	
	
	$Tooltip()$
	$text$
	
	$if(img)$<img src="kbimg/$img$" alt="img">$endif$
</div>