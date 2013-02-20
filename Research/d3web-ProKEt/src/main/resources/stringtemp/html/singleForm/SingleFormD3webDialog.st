<!-- Needed for Internet Explorer to work with JSTree (JS Lib) correctly //-->
<!DOCTYPE html 
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html>
<head>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META http-equiv="Content-Script-Type" content="text/javascript">
<META http-equiv="Content-AttributeContainer-Type" content="text/css">
<META http-equiv="X-UA-Compatible" content="IE=8">
<title>$title$</title>
$fullcss$
$fulljs$
</head>


<body class="table">

    <!-- header & footer in one //-->
    <div id="head" class="row">
        <table>
        <tr>
            <td width="40%">
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
                $if(reset)$
                    <div class="leftButton">
                        $CaseNewButton()$
                    </div>
                $endif$
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
            </td>
            <td width=*>
                $header$
            </td>
            <td width="40%">
            </td>
	</tr>
        </table>
    </div>


    <div id="middle" class="row">
        <div class="table">
	<!-- Solution Panel is displayed over actual dialog //-->
        <div id="solution row" class="row">
            <div id="solutionPanelCell" class="cell">
               
                    $solutionPanel/SolutionPanelBaseSingle()$
                
            </div>
        </div>

        <div id="dialogrow" class="row">
            <!-- actual dialog content //-->
            <div id="contents" class="cell">
                $children$
            </div>
        </div>
        </div>
    </div>

</body>
</html>