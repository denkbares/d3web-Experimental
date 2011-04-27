<div id="$fullId$"  class="answer $fullId$ $if(realAnswerType)$answer-$realAnswerType$$endif$" >
	<table>
	<tbody>
	<tr>
	<td width=*>
		<input id="f_$count$_$fullId$" name="group-$count$-$parentFullId$" type="radio" $selection$ title="$fullId$" 
		$if(readonly)$ DISABLED $endif$ />	
	</td>
	<td width="95%" $if(inactive)$ class="inactive" $endif$>
		$AnswerTitleTooltip()$
		$text$
	</td>
	</tr>
	</tbody>
	</table>
</div>