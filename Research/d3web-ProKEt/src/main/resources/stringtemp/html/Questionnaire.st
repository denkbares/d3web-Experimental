<div id="$fullId$" $if(hidden)$style="display: none"$endif$ class="questionnaire $fullId$ $if(questInactive)$ inactive $endif$ 
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