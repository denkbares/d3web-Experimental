<div id="$fullId$" $if(imageAnswer)$type="imageAnswer"$endif$ class="answer $fullId$ $if(realAnswerType)$answer-$realAnswerType$$endif$" >
	$if (dropdown_menu)$
            $dropdown_menu$
	$else$
            <table>
		<tr>
				<td width=*>
					<input id="f_$count$_$fullId$" name="group-$count$-$parentFullId$" type="radio" $selection$ title="$fullId$" 
					$if(readonly)$ DISABLED $endif$ />
				</td>
				<td id="text-$fullId$" width="100%" $if(inactive)$ class="inactive" $endif$>
					$text$
					$Tooltip()$
				</td>
                </tr>
            </table>
	$endif$
</div>