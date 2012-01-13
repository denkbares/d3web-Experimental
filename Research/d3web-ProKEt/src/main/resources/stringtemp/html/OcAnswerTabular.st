<div id="$fullId$" $if(imageAnswer)$type="imageAnswer"$endif$ class="answer $fullId$ $if(realAnswerType)$answer-$realAnswerType$$endif$" >
	<label>
		<table>
			<tr>
				<td width=*>
					<input id="f_$count$_$fullId$" name="group-$count$-$parentFullId$" type="radio" $selection$ title="$fullId$" 
					$if(readonly)$ DISABLED $endif$ />
				</td>
				<td id="text-$fullId$" width="100%" $if(inactive)$ class="inactive" $endif$>
					$text$
					$Tooltip()$
				</td>
			</tr>
		</table>
	</label>
</div>