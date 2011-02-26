$HTMLHeader()$

<body>
<div id="head">
	$header$
</div>

<div id="middle">
	<div id="content">
		<div class="dialog">
		
		$if(title)$<div id="dialog-title">$title$</div>$endif$
			$if(text)$$text$$endif$
			
		<table>
		<th />
		<tbody>
			<tr>
				<td id="navileft" width="15%></td>
				
				<td id="dialog-content" width="50%">
					Dieser Dialog bewertet die Diagnose "Arbeitsverhältnis ist wirksam und termingerecht gekündigt worden".
					Um (weitere) klärende Fragen - in Aussagenform - auszuklappen und zu beantworten, klicken Sie auf das "+" Symbol.<p>
					Bitte bewerten Sie die nachfolgenden Aussagen nach dem Schema "trifft zu" (Haken), "trifft nicht zu" (Kreuz),
					oder "weiß nicht" (Fragezeichen). Um eine Antwort zurückzunehmen klicken Sie das leere Quadrat.
					<div id="dialog-content">
						$children$			
					</div>
				</td>
				
				<td id="inforight" width="35%></td>
			</tr>
		</tbody>
		</table>
			
		
			

			
		</div>
	 </div>
  </div>
</body></html>