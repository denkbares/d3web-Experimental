<div id="$fullId$" class="answer $fullId$ answer-closed">
	<div id="t-$fullId$" class="title answer-title" onclick="toggle_sub('$fullId$')">
		$Tooltip()$
		$title$
		$Image()$ $text$
	</div>
	
	$if(children)$<div id="sub-$fullId$" class="sub-answer sub-$fullId$" style="display:none">
		$children$
	</div>$endif$
</div>