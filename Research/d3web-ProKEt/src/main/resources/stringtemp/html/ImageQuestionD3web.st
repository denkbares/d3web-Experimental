<div id="$fullId$" class="question imageQuestion
					$if(inactive)$ inactive $endif$ $qstate$" >
	$if(title)$$TitleTooltipButton()$$endif$
	<div id="content-$fullId$" class="content-$fullId$ question-content">
		$text$
		<table>
			<tr>
				<td valign="center">
					<img style="margin-right: 15px" $if(width)$width="$width$"$endif$ $if(height)$height="$height$"$endif$ src="kbimg/$image$" alt="$image$" 
					$if(inactive)$
						usemap="">
					$else$
						usemap="#$fullId$-map">
							<map name="$fullId$-map">
							$if(areas)$$areas$$endif$
							</map>
					$endif$
				</td>
				<td valign="center" width="100%">
					$if(children)$$children$$endif$
				</td>
		</table>
	</div>
</div>