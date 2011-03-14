<div id="$fullId$" class="question $fullId$"><table><tr><td valign="top">
	$if(title)$ <div id="t-$fullId$" class="title t-$fullId$" onclick="javascript:show_clarification_popup('$fullId$', event);">$title$$endif$
		$Tooltip()$
	$if(title)$</div>$endif$
	</td><td valign="top">
	<div id="content-$fullId$" class="content-$fullId$ question-content">
		$Image()$
		$text$
		$if(children)$$children$$endif$
	</div></td></tr></table>
</div>