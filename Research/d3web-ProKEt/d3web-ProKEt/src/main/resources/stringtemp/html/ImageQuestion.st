<div id="$fullId$" class="question question-images $fullId$">
	$TitleTooltip()$
	<div id="content-$fullId$" class="content-$fullId$ question-content">
		$text$
		<table><tr><td width="50%" valign="center">
		<img src="img/$image$" alt="$image$" usemap="#$fullId$-map">
		<map name="$fullId$-map">
		$if(children)$$children$$endif$
		</map>
		</td>
		<td width="50%" valign="center">
		$if(childrenText)$$childrenText$$endif$
		</td></tr></table>
	</div>
</div>