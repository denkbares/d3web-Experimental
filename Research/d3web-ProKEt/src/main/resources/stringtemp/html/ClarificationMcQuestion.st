<div id="$fullId$" class="question $fullId$$if(type)$ question-$type$$elseif(answerType)$ question-$answerType$$endif$">
	<table>
		<tr>
		<td valign="top"><div id="weight-$fullId$" class="weight-box" /></td>
		<td valign="top">
		$if(title)$ 
			<div id="t-$fullId$" class="title t-$fullId$" onclick="javascript:show_clarification_popup('$fullId$', event);">$title$$endif$
			$Tooltip()$
		$if(title)$</div>$endif$
		</td>
		<td valign="top">
		<div id="content-$fullId$" class="content-$fullId$ question-content">
			$Image()$
			$text$
			$if(children)$$children$$endif$
		</div></td>
		</tr>
	</table>
</div>