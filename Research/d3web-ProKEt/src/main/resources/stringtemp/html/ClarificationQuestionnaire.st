<div style="clear: both;" class="questionnaire $fullId$" id="$fullId$">
	$if(rating)$<div id="t-$fullId$" class="title t-$fullId$" onClick="toggle('content-$fullId$');">
		<table><tr><td>$title$ $Tooltip()$</td><td>$RatingBox()$</td></tr></table>
	</div>
	$else$
		$if(title)$
			<div id="t-$fullId$" class="title t-$fullId$" onClick="toggle('content-$fullId$');">
			$title$ $Tooltip()$
			</div>
		$elseif(tooltip)$
			<div id="t-$fullId$" class="title t-$fullId$" onClick="toggle('content-$fullId$');">
			$Tooltip()$
			</div>
		$endif$
	$endif$
	<div id="content-$fullId$" class="content-$fullId$ questionnaire-content">
   		$if(children)$$children$$endif$
   	</div>
</div>