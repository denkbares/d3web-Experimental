<div id="$fullId$" class="answer $fullId$ $if(realAnswerType)$answer-$realAnswerType$$endif$">
	$title$
	
	$if(readonly)$
		<$if(longtext)$textarea$else$input$endif$ id="f_$fullId$" type="readonly" $if(selection)$value="$selection$"$endif$ title="$title$" $if(longtext)$style="width: 100%; height: 100px"$endif$ />
	$else$
		$if (dropdown_menu)$
		$dropdown_menu$
		$else$
		<$if(longtext)$textarea$else$input$endif$ id="f_$fullId$" type="text"
				$if(title)$title="$title$"$endif$ 
				$if (useasfilename)$useasfilename="true"$endif$
				$if(longtext)$style="width: 100%; height: 100px"
				$else$value="$selection$"$endif$>$if(longtext)$$selection$$endif$</$if(longtext)$textarea$else$input$endif$>
		$endif$
	$endif$
	$Tooltip()$
	$text$
</div>