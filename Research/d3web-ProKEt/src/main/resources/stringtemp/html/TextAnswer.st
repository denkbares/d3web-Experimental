<div id="$fullId$" class="answer $fullId$ $if(realAnswerType)$answer-$realAnswerType$$endif$">
	$title$
	
	$if(readonly)$
		<input id="f_$fullId$" type="readonly" $if(selection)$value="$selection$"$endif$ title="$title$" $if(width)$size="$width$"$endif$ />
	$else$
		$if (dropdown_menu)$
		$dropdown_menu$
		$else$
		<input id="f_$fullId$" type="text" 
				$if(selection)$value="$selection$"$endif$ 
				$if(title)$title="$title$"$endif$ 
				$if (useasfilename)$useasfilename="true"$endif$ 
				$if(width)$size="$width$"$endif$ />
		$endif$
	$endif$
	
	
	$Tooltip()$
	$text$
</div>