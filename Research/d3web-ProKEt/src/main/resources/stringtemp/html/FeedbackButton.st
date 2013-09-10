<div id="FFButton" class="baseButtonOneLetter feedback-tt-trigger">
	F
    
        $if(study)$
        $FeedbackForm_GER()$
        $elseif(feedback)$
        $FeedbackForm()$
        $endif$
        
        <div id="tt-feedback" class="tooltip tooltipArrow">
            Formular aufrufen, um jederzeit Feedback abzugeben.
        </div>
</div>
