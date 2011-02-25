<div id="$fullId$" class="questionnaire $fullId$" style="$style$">
	$if(title)$$TitleTooltip()$$endif$
	JURA JURA JURA
	$Image()$
	$text$
	$if(children)$ 
	<div id="content-$fullId$" class="content-$fullId$ questionnaire-content" style="$style$">
		$children$
	</div>$endif$
</div>