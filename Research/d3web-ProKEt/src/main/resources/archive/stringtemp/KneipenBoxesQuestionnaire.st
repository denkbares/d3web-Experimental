<div id="$fullId$" class="questionnaire $fullId$">
	<div id="t-$fullId$" class="title t-$fullId$">
		<table width="100%"><tr>
			<td width="33%">[d3]</td>
			<td width="33%" align="center">
			$if(title)$$title$$Tooltip$$endif$
			$Image()$
			</td>
			<td width="33%" align="right">[dialog]</td>
		</tr></table>
	</div>
	$text$
	$if(children)$ 
	<div id="content-$fullId$" class="content-$fullId$ questionnaire-content">
		$children$
	</div>$endif$
</div>