<div id="$fullId$" $if(imageAnswer)$type="imageAnswer"$endif$ class="answerFlat $fullId$ $if(realAnswerType)$answer-$realAnswerType$$endif$" >
	$if (dropdown_menu)$
            $dropdown_menu$
	$else$
            $text$
            $Tooltip()$
        $endif$
</div>