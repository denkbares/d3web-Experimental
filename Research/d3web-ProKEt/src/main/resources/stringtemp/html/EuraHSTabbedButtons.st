<div id="jqTabbedButtons" >
		<ul>
			<li><a href="#eurahsRegistry">EuraHS Registry</a></li>
                        <li><a href="#eurahsStatistics">Statistics</a></li>
		</ul>
		
                <div style="float:left;" id="eurahsRegistry"> 
                    $if(reset)$
                        <div class="leftButton">
                            $CaseNewButton()$
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

                    $if(summary)$
                        <div class="leftButton">
                            $SummaryButton()$
                        </div>
                    $endif$
		</div>

		
                <div style="float:left;" id="eurahsStatistics">
                    $if(statistics)$
                        <div class="leftButton">
                            $StatisticsButton()$
			</div>
                    $endif$
		</div>
</div>