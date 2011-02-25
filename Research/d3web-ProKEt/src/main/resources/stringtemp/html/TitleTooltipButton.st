$if(title)$<div id="t-$fullId$" class="title t-$fullId$">
<table width="100%"><tr><td width="*" valign="center"> 
$count$ $title$ $Tooltip()$
$if(sendButton)$</td><td width="50"><div id="ok-$fullId$" class="ok-button ok-$fullId$">OK</div>$endif$</td></tr></table>
</div>
$elseif(tooltip)$
$Tooltip()$
$endif$