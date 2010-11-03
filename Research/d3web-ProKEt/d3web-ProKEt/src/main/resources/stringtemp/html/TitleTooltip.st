$if(title)$
	<div id="t-$fullId$" class="title t-$fullId$">
	$title$ $Tooltip()$
	</div>
$elseif(tooltip)$
	<div id="t-$fullId$" class="title t-$fullId$">
	$Tooltip()$
	</div>
$endif$