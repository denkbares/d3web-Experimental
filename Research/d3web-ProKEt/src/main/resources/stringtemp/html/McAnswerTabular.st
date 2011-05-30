<div id="$fullId$"  class="answer $fullId$ $if(realAnswerType)$answer-$realAnswerType$$endif$" >
	<label>
		<table>
			<tr>
				<td width=*>
					<input id="f_$fullId$" type="checkbox" $selection$ title="$title$"
					$if(readonly)$ DISABLED $endif$ />
				</td>
				<td width="95%" $if(inactive)$ class="inactive" $endif$>
					$text$
				</td>
			</tr>
		</table>
	</label>
</div>
