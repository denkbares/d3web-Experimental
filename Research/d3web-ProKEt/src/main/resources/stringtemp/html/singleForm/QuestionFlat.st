<div id="$fullId$" class="question $fullId$ $if(inactiveQuestion)$ inactive $endif$ $if(abstractQuestion)$ abstract $endif$ $qstate$ questionFlat"
	$if(before)$before="$before$"$endif$ $if(after)$after="$after$"$endif$>
	
    <div class="row">
        <div class="cell flatcell question">
            
            <div class="cell">
                $if(count)$$count$.$endif$
            </div>
            <div class="cell">
                $title$
                $Tooltip()$
            </div>
        </div>
	
        <div class="cell">
            $if(selectBox)$
		 	<select>
			<option>-Antworten-</option>
  			$if(children)$
  				$children$
  			$endif$
			</select>
		$else$
			$if(children)$
				$children$
			$endif$
		$endif$
		<div id=error-$fullId$ class="qerror"></div>
        </div>
    </div>
</div>