<div style="display:none">
	<div id="jqSummaryDialog" title="$if(langEN)$Summary$endif$$if (langDE)$Zusammenfassung$endif$">
		<ul>
			<li><a href="#tabularSummary"><span>Tabular Summary</span></a></li>
			<li><a href="#questionnaireSummary"><span>Complete Summary</span></a></li>
		</ul>
		<div style="float:left;" id="tabularSummary"> 
			$TabularSummary()$
		</div>
		<div style="float:left;" id="questionnaireSummary"> 
			$QuestionnaireSummary()$
		</div>
	</div>
</div>