/* BASIS STUFF */
$basis()$

body,table,div { padding: 0px; margin: 0px; font-family: Arial; font-size: 10pt; text-color: black; }

body{ background-color: #FFFFFF;}

table { width: 100%; }

td { padding: 2px 0; vertical-align: top; height: 100%}


/* STANDARD OWN STYLED BUTTONS */ 
.caseButton{ width: 90px; height: 20px; padding-top: 3px; border: 1px solid #696969; text-align: center; cursor: pointer; 
	background-color: #dddddd; font-weight: bold; color: #982F02; -moz-box-shadow: 0 0 5px black;	
	-webkit-box-shadow: 0 0 5px black; box-shadow: 0 0 5px black; }

.SaveCaseButton{ width: 90px; height: 20px; padding-top: 3px; border: 1px solid #696969; text-align: center; cursor: pointer; 
	background-color: #fffacd; font-weight: bold; color: #982F02; -moz-box-shadow: 0 0 5px black;	
	-webkit-box-shadow: 0 0 5px black; box-shadow: 0 0 5px black; }
	
.leftButton { float: left; margin-right: 5px;}

.ok-button { border: 1px solid #696969; text-align: center; cursor: pointer;  background-color: #DDDDDD; font-weight: normal; 
 	color: #696969; -moz-box-shadow: 0 0 5px black;	-webkit-box-shadow: 0 0 5px black; box-shadow: 0 0 5px black; }


/* GENERAL STRUCTURING */
#head{ font-size: 1.5em; font-variant: small-caps; font-weight: bold; text-align: center; background: #efefef; 
	background-image: none; padding: 0px 5px 0px 5px; margin-left: auto; margin-right: auto; position: fixed; width: 100%;
	-moz-box-shadow: 0 0 5px black; -webkit-box-shadow: 0 0 5px black; box-shadow: 0 0 5px black; float: none; z-index: 99; } 

#head td .header{ font-size: 1.4em; font-variant: small-caps; text-align: center; font-weight: bold; color: black; }

#left, #right{float: none;}

#right{width: 10%;}

#middle { width: 90%; margin: 0 auto; }


/* DIALOG ITEMS */
.questionnaire{ -moz-box-shadow: 0 0 5px black;	-webkit-box-shadow: 0 0 5px black; box-shadow: 0 0 5px black; padding: 5px; 
 		background: #ffffff; padding-top: 20px;}
 				 
#questionnaireTitle { font-weight: bolder; padding-right: 5px; }

.title { padding-left: 4px; padding-right: 4px; vertical-align: middle; background-color: #efefef;
	-moz-box-shadow: 0 0 5px black; -webkit-box-shadow: 0 0 5px black; box-shadow: 0 0 5px black; }

.title,.title td { font-weight: bolder;}

.question .title { font-size: 11pt;	background-color: #b0c4de; 
	-moz-box-shadow: none; -webkit-box-shadow: none; box-shadow: none; }

.question {height: 100%; background-color: #b0c4de; font-weight: bold; border-top: 2px solid #E0E0E0; 
 	padding: 5px 5px 0 5px;}

.question-c .title { background-color: #b0c4de; }

.question-c { background-color: #b0c4de; padding-top: 5px; }

.question-zc, .question-zc .title{ background-color: #b0c4de; border: none; }

.question-d .title { background-color: transparent; }

.question-d { background-color: transparent; padding-top 5px; }

.answer-num, .answer-text, .answer-date {padding: 0 0 10px 5px;}

.answer .title { background-color: transparent; font-size: 10pt; font-weight: normal; padding: 2px; }

.answer{ font-weight: normal;}


/* TOOLTIPS, ADD STUFF */
.tooltip { z-index: 1001; background-color: rgb(255, 255, 180); background-color: rgba(255, 255, 180, 1);
	padding: 5px; -moz-border-radius-bottomright: 5px; -webkit-border-bottom-right-radius: 5px;
	-moz-box-shadow: 3px 3px 3px #888; -webkit-box-shadow: 3px 3px 3px #888; box-shadow: 3px 3px 3px #888;
	border: 1px solid #BBB; font-weight: normal; color: #555; }

.disabled { background: #E0E0E0; }
	
.questionnairehidden{display: none;}

.hidden .title, .hidden .question, .hidden .title td, 
.hidden .answer, .hidden input, .hidden radio, .hidden .questionnaire-content{
	display: none;}
	
	
.inactive {	background-color: #d3d3d3; color: grey; }

.inactive .title, .inactive .question, .inactive .title td, 
.inactive .answer, .inactive input, .inactive radio{
	background-color: #d3d3d3; color: grey; }

#send_popup{z-index: 100; background-color: rgba(255, 255, 180, 0.90);
	padding: 5px; -moz-border-radius-bottomright: 5px; -webkit-border-bottom-right-radius: 5px;
	-moz-box-shadow: 3px 3px 3px #888; -webkit-box-shadow: 3px 3px 3px #888; box-shadow: 3px 3px 3px #888;
	border: 1px solid #BBB; font-weight: normal; color: #555; width: 500px; height: 100px;}
	
#fileselect{display: none; margin-top: 20px;}

#popupTableDiv{width: 400px; margin-top: 5px; margin-bottom: 10px;}

li.noListMarker {list-style-type: none;}


.unknown{font-weight: normal; margin-top: 5px;}

.qerror{margin: 5px; color: #CC0000; font-weight: bold;}

.imageQuestion  img {border: none; margin-bottom: 10px;}

.langSelect {float:right; margin-right: 10px;}

.header{padding-top: 7px;}

.summarycell {
	padding:2px;
	background-color: #B1E6BD
}


/* overwrite some jquery ui stuff for tabbed panes 
.ui-corner-all{border: none;}

.ui-widget-header{background: none};



/*.ui-state-active, .ui-widget-content .ui-state-active, .ui-widget-header .ui-state-active {
    background: url("img/ui-bg_highlight-hard_50_bababa_1x100.png") repeat-x scroll 50% 50% #BABABA;
    border: 1px solid #D3D3D3;
    color: #555555;
    font-weight: normal;
}

.ui-state-default, .ui-widget-header .ui-state-default {
    background: url("../../../../img/ui-bg_flat_75_eeeeee_40x100.png") repeat-x scroll 50% 50% #EEEEEE;
    border: 1px solid #D3D3D3;
    color: red;
    font-weight: normal;
}

*/


.cell{display: table-cell; }

.table{display: table; width: 100%;}

.row{display: table-row;}

#treeNaviSolutions{padding-left: 20px;
    padding-right: 20px;
    padding-top: 20px;
    width: 15%;}

#contents{margin-top: 80px; width: 65%;}

#solutionPanelCell{padding-top: 70px; padding-left: 20px; padding-right: 10px; width: 20%;
position: fixed; top:0px; left: 75%;}

#solutionPanel{background-color: lightslategray; color: white; }

#sol_heading{font-weight: bold; font-size: 1.1em; 
margin-left: 20px;}

.solution{margin-top:5px; margin-left:30px; margin-right: 10px; display: table-row;}

.sol_bgSpacer{background-color: lightslategray; height:20px;}

.questionnaire-content{padding-top:10px;}

.jstree-apple > ul { background-color: lightslategrey; color: white;}

.solution > img { margin-right: 5px; margin-left:12px; display: table-cell;}

.solutiontext{display: table-cell; vertical-align:middle;}

.bottomSpace{margin-bottom: 10px;}


#contents{padding-top: 70px;}