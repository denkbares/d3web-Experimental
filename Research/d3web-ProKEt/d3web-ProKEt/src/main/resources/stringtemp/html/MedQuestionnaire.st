<div id="$fullId$" class="questionnaire $fullId$">
	$Image()$
	$text$	
	$if(title)$
		<div id="questionnaireTitle" class="title">
			$title$$Tooltip$
		</div>
	$endif$
	$if(children)$ 
	<div id="content-$fullId$" class="content-$fullId$ questionnaire-content">
		$children$
	</div>$endif$
</div>