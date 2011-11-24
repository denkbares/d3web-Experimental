<div id="$fullId$"  class="unknown" >
	<label>
		<table>
			<tr>
				<td width=*>
					<input id="i_unknown_$fullId$" type="radio" $selection$ title="Unknown" 
					name="group-$count$-$fullId$" $selection$ $if(readonly)$ DISABLED $endif$ />
				</td>
				<td id="text-$fullId$" width="95%" $if(inactive)$ class="inactive" $endif$>
					Unknown
					$Tooltip()$
				</td>
			</tr>
		</table>
	</label>
</div>

