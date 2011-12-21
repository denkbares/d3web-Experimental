<div id="$fullId$" class="question $fullId$ $answerType$">
	<div id="$id$-imagebox" class="question-imagebox">
		<img id="$id$-typeimg" alt="type" src="$typeimg$" height="16" width="16" onclick="toggle_sub_4boxes('$fullId$')">
    	<img height="16" 
		$if(noChildren)$usemap="#panel-$fullId$"$endif$ 
		src="img/panel.gif" style="border: medium none;" id="panel-$fullId$" 
		$if(hasChildren)$onclick="toggle_sub_4boxes('$fullId$')"$endif$>
    </div> 
    <div class="title t-$fullId$" id="t-$fullId$" onmouseover="showAuxInfo('$fullId$')" onmouseout="hideAuxInfo()">
        $title$
		$Tooltip()$
		$Image()$
		$text$
		<map name="panel-$fullId$">
			<area href="javascript:h4boxes('1','panel-$fullId$')" coords="0,0,14.5,15" shape="rect">
			<area href="javascript:h4boxes('2','panel-$fullId$')" coords="14.5,0,29.0,15" shape="rect">
			<area href="javascript:h4boxes('3','panel-$fullId$')" coords="29.0,0,43.5,15" shape="rect">
			<area href="javascript:h4boxes('4','panel-$fullId$')" coords="43.5,0,58.0,15" shape="rect">
		</map>
	</div>
	
	$if(children)$<div id="sub-$fullId$" class="sub-$fullId$" style="padding-left:30px;display:none">
		$children$
	</div>$endif$
	
	<div id="bonus-$fullId$" style="display:none">$bonusText$</div>
</div>