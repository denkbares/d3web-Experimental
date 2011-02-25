<div id="$fullId$" class="questionnaire $fullId$">
	$Image()$
	$if(text)$$text$$endif$	
	$if(title)$$title$$Tooltip$$endif$
	$if(children)$ 
	<div id="content-$fullId$" class="content-$fullId$ questionnaire-content">
		$children$
	</div>$endif$
</div>