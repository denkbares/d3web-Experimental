<div id="$fullId$"  class="answer $fullId$ $if(realAnswerType)$answer-$realAnswerType$$endif$" >
	<label>
		<input id="f_$count$_$fullId$" name="group-$count$-$parentFullId$" type="radio" $selection$ title="$fullId$" 
		$if(readonly)$ DISABLED $endif$ />
		$text$
	</label>
</div>