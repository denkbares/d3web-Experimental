<div id="$fullId$"  class="unknown" >
	<label>
		<table>
			<tr>
				<td width=*>
					<input id="i_unknown_$fullId$" type="radio" $selection$ title=$title$ 
					name="group-$count$-$fullId$" $selection$ $if(readonly)$ DISABLED $endif$ />
				</td>
				<td id="text-$fullId$" width="95%" $if(inactive)$ class="inactive" $endif$>
					$title$
					$Tooltip()$
				</td>
			</tr>
		</table>
	</label>
</div>

