<div id="$fullId$" class="question $fullId$$if(type)$ question-$type$$elseif(answerType)$ question-$answerType$$endif$$if(inactiveQuestion)$ inactive $endif$ $if(abstractQuestion)$ abstract $endif$ $qstate$"
	$if(before)$before="$before$"$endif$ $if(after)$after="$after$"$endif$$if(durcheck)$check="$durcheck$"$endif$>
	$if(title)$$TitleTooltipButton()$$endif$
	<div id="content-$fullId$" class="content-$fullId$ question-content">
		$Image()$
		
		$if(selectBox)$
		 	<select>
			<option>-Antworten-</option>
  			$if(children)$
  				$children$
  			$endif$
			</select>
		$else$
			$if(children)$
				$children$
			$endif$
		$endif$
		<div id=error-$fullId$ class="qerror"></div>
	</div>
</div>