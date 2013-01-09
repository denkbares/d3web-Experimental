<table>
				<tr>
					<td>
						$if(reset)$
							<div class="leftButton">
								$CaseNewButton()$
							</div>
						$endif$
						
						$if(statistics)$
							<div class="leftButton">
								$StatisticsButton()$
							</div>
						$endif$

						$if(summary)$
							<div class="leftButton">
								$SummaryButton()$
							</div>
						$endif$
					</td>
				</tr>
				<tr>
					<td>
						$if(followupbutton)$
							<div class="leftButton">
								$FollowUpButton()$
							</div>
						$endif$
						
						$if(loadcase)$
							<div class="leftButton">
								$CaseLoadButton()$
							</div>
						$endif$
						
						$if(savecase)$
							<div class="leftButton">
								$CaseSaveButton()$
							</div>
						$endif$
					</td>
				</tr>
			</table>