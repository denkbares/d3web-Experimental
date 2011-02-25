<div id="$fullId$" class="questionnaire $fullId$ $if(inactive)$ inactive $endif$">
	$if(title)$$TitleTooltip()$$endif$
	$Image()$
	$text$
	$if(children)$ 
	<div id="content-$fullId$" class="content-$fullId$ questionnaire-content" >
		$children$
	</div>
	$endif$
</div>