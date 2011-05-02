<div id="$fullId$" class="questionnaire$if(hidden)$hidden $endif$ $fullId$ $if(questInactive)$ inactive $endif$ 
  ">
	$if(title)$$TitleTooltip()$$endif$
	$Image()$
	$text$
	$if(children)$ 
	<div id="content-$fullId$" class="content-$fullId$ questionnaire-content" >
		$children$
	</div>
	$endif$
</div>