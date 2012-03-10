<div id="jqUEQDialog" class="uequestionnaire" 
    $if(sus)$title="Survey"$elseif(own)$title="Kurz-Fragebogen"$endif$>

    $if(sus)$
        <div id="ueqexplanation" class="margBott"> 
            For further improvement of the software, please fill in the following
            questions:
        </div>
    $endif$
    $if(own)$
        <div id="ueqexplanation" class="margBott"> 
            Bitte beantworten Sie die <b>Fragen 1-7 komplett</b>. <br /> Optional können Sie bei Punkt
8 weitere Anmerkungen (zB. Benutzung, Design, Browserprobleme...) eintragen.
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
    $endif$
    
    
    
    
    
    
</div>