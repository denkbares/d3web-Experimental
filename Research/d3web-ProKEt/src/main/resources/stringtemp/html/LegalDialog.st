$HTMLHeader()$

<body>
<div id="head">
	$header$
</div>



<div id="middle">

    <div id="content">
    
        <div id="ueqButtons">
            $if(feedback)$
                <div class="leftButton">
                $FeedbackButton()$
                </div>
            $endif$
        
            $if(ueq)$
                <div class="leftButton">
                $ButtonUEQuestionnaire()$
                </div>
            $endif$
        </div>

	<div class="dialog">
			$if(title)$<div id="dialog-title">$title$</div>$endif$
			$if(text)$$text$$endif$
	
			<table>
			  <tr>
			  	<td>
				<div id="dialog-content">
				$children$			
				</div>
			  </td>
			 
			</td>
			</tr>
		  </table>
		</div>
	 </div>
  </div>
</body></html>