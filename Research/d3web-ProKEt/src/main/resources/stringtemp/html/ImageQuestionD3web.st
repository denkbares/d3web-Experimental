<div id="$fullId$" class="question imageQuestion
					$if(inactive)$ inactive $endif$ $qstate$" >
	$if(title)$$TitleTooltipButton()$$endif$
	<div id="content-$fullId$" class="content-$fullId$ question-content" style="position: relative; padding: 8px;">
		$text$
		<table>
			<tr>
				<td>
					<img style="$if(width)$width: $width$px; $endif$$if(height)$height: $height$px; $endif$margin-right: 15px" 
							src="kbimg/$image$" alt="$image$" $if(inactive)$$else$usemap="#$fullId$-map"$endif$ >
					</img>
					<div style="position: absolute; top: 16px; left: 14px;">
						$if(areas)$$areas$$endif$
					</div>
				</td>
				<td valign="center" width="100%">
					$if(children)$$children$$endif$
				</td>
			</tr>
		</table>
	</div>
</div>