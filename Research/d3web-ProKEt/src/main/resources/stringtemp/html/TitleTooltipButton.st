$if(title)$<div id="text-$fullId$" class="title t-$fullId$">
<table width="100%"><tr>
$if(count)$<td style="width:1px; padding-right:3px">$count$</td>$endif$ 
<td id="text-$fullId$">$title$ $Tooltip()$ $if(sendButton)$</td>
<td width="50"><div id="ok-$fullId$" class="ok-button ok-$fullId$">OK</div>$endif$</td></tr></table>
</div>
$elseif(tooltip)$
$Tooltip()$
$endif$