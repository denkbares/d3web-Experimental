<div id="$fullId$" class="question imageQuestion
					$if(inactive)$ inactive $endif$ $qstate$" >
	$if(title)$$TitleTooltipButton()$$endif$
	<div id="content-$fullId$" class="content-$fullId$ question-content">
		$text$
		<table>
			<tr>
				<td valign="center">
					<img width="$width$" src="kbimg/$image$" alt="$image$" 
					$if(inactive)$
						usemap="">
					$else$
						usemap="#$fullId$-map">
							<map name="$fullId$-map">
							$if(areas)$$areas$$endif$
							</map>
					$endif$
				</td>
			</tr>
			<tr>
				<td valign="center">
					$if(children)$$children$$endif$
				</td>
			</tr>
			
		</table>
	</div>
</div>