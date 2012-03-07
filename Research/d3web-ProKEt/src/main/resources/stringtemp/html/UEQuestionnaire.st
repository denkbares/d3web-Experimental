<div id="jqUEQDialog" class="uequestionnaire" title="Survey">

    $if(sus)$
        <div id="ueqexplanation" class="margBott"> 
            For further improvement of the software, please fill in the following
            questions:
        </div>
    $endif$
    $if(own)$
        <div id="ueqexplanation" class="margBott"> 
            Bitte füllen Sie den folgenden Kurz-Fragebogen komplett aus:
        </div>
    $endif$
    

    <div id="ueq">
        $if(sus)$
            $UE_SUS()$
        $endif$
        $if(own)$
            $UE_OWN()$
        $endif$
    </div>

        
    $if(sus)$
        <div id="ueqOptional" class="margBott">
            Please provide your name and mail-adress optionally. 
            Otherwise, the feedback can as well be sent anonymously.
        </div>
    $endif$
    $if(own)$
        <div id="ueqOptional" class="margBott">
            Bitte geben Sie (optional) Ihren Namen und email-Adresse an.
            Sie können das Feedback aber auch anonym schicken.
        </div>
    $endif$
    
    
    
    <table>
    <tbody>
        <tr>
            <td width="60px;">Name:</td>
            <td><input id="ueqname" class="longTextfield" /></td> 
	</tr>
	<tr>
		<td width="60px;">EMail:</td>
		<td><input id="ueqmail" class="longTextfield" /></td> 
	</tr>
    </tbody>
    </table>
    
</div>