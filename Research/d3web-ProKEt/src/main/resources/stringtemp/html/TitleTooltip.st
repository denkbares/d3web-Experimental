$if(title)$
	<div id="text-$fullId$" class="title t-$fullId$">
	$count$ $title$ $Tooltip()$
	</div>
$elseif(tooltip)$
	<div id="t-$fullId$" class="title t-$fullId$">
	$Tooltip()$
	</div>
$endif$