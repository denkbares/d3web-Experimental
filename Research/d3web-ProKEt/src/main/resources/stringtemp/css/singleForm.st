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
	background-image: none; padding:5px; margin-left: auto; margin-right: auto; position: fixed; width: 100%;
	-moz-box-shadow: 0 0 5px black; -webkit-box-shadow: 0 0 5px black; box-shadow: 0 0 5px black; float: none; z-index: 99; } 

#head td .header{ font-size: 1.4em; font-variant: small-caps; text-align: center; font-weight: bold; color: black; }

#head td{background: #efefef; font-size: 1.4em;}

#left, #right{float: none;}

#right{width: 10%;}

#middle { width: 90%; margin: 0 auto; }

#contents{padding: 5px;}


/* DIALOG ITEMS */

td { -moz-box-shadow: 0 0 2px black; -webkit-box-shadow: 0 0 2px black; box-shadow: 0 0 2px black; padding: 5px; 
 		background: #ffffff; }

/* reset the td styling for questionnaire inner elements and header */
#head td, .questionnaire td { -moz-box-shadow: 0 0 0 black; -webkit-box-shadow: 0 0 0 black; box-shadow: 0 0 0 black; padding: 0; }
 				 
.questionnaireTitle { font-weight: bolder; padding-right: 5px; }


.answerFlat{float: left; margin-right: 5px;}
.unknownFlat{float: left;}


/* TOOLTIPS, ADD STUFF */



.table{display: table; width: 100%;}

.row{display: table-row;}

.cell{display: table-cell; }

.flatcell{vertical-align: top;}

.flatcell.question{width: 140px;}


#solutionPanelCell{
  padding-bottom: 10px;
  padding-top: 60px;
}


#solutionPanel{width: 70%; margin-left: auto; margin-right: auto; padding: 10px; padding-top: 20px; }

#sol_heading{vertical-align: top; padding-bottom: 20px; padding-right: 10px;}