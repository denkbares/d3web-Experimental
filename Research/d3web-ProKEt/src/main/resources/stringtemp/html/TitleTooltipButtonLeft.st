$if(title)$<div id="text-$fullId$" class="title t-$fullId$">
<table width="100%"><tr><td> 
$if(sendButton)$<td width="30"><div id="ok-$fullId$" class="ok-button ok-$fullId$">OK</div>$endif$</td>
<td width="*" valign="center">$title$ $Tooltip()$</td></tr></table>
</div>
$elseif(tooltip)$
$Tooltip()$
$endif$