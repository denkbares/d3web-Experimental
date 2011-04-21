<div id="$fullId$" class="question imageQuestion $fullId$
					$if(inactive)$ inactive $endif$ $qstate$">
	$if(title)$$TitleTooltipButton()$$endif$
	<div id="content-$fullId$" class="content-$fullId$ question-content">
		$text$
		<table>
			<tr>
				<td valign="center">
					<img width="100%" src="kbimg/$image$" alt="$image$" usemap="#$fullId$-map">
					<map name="$fullId$-map">
						$if(areas)$$areas$$endif$
					</map>
					
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