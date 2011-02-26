/* BASIS STUFF */
$basis()$

body,table,div { padding: 0px; margin: 0px; font-family: Arial; font-size: 10pt; text-color: black; }

body{ background-color: #fff8dc;}

table { width: 100%; }

td { padding: 0px; vertical-align: top; }


/* STANDARD OWN STYLED BUTTONS */ 
.sendExitButton{ width: 130px; height: 20px; padding-top: 3px; border: 1px solid #696969; text-align: center; cursor: pointer; 
	background-color: #DDDDDD; font-weight: bold; color: brown; -moz-box-shadow: 0 0 5px black;	
	-webkit-box-shadow: 0 0 5px black; box-shadow: 0 0 5px black; }

.rightButton { float: right; margin-right: 10px; }

.leftButton { float: left; }

.ok-button { border: 1px solid #696969; text-align: center; cursor: pointer;  background-color: #DDDDDD; font-weight: normal; 
 	color: #696969; -moz-box-shadow: 0 0 5px black;	-webkit-box-shadow: 0 0 5px black; box-shadow: 0 0 5px black; }


/* GENERAL STRUCTURING */
#head{ font-size: 1.5em; font-variant: small-caps; font-weight: bold; text-align: center; background: #f5f5f5;
	background-image: none; padding: 15px 5px 15px 5px; margin-left: auto; margin-right: auto; position: fixed; width: 100%;
	-moz-box-shadow: 0 0 5px black; -webkit-box-shadow: 0 0 5px black; box-shadow: 0 0 5px black; float: none; } 

#head td{ font-size: 1.5em; font-variant: small-caps; text-align: center; font-weight: bold; }

#content { width: 99%; margin-top: 80px;}

#middle { width: 90%; margin: 0 auto; }


/* DIALOG ITEMS */
.questionnaire{ -moz-box-shadow: 0 0 5px black;	-webkit-box-shadow: 0 0 5px black; box-shadow: 0 0 5px black; padding: 5px; 
 		background: #f5f5f5; }
 				
#questionnaireTitle { font-weight: bolder; padding-right: 5px; }

.title { padding-left: 4px; padding-right: 4px; vertical-align: middle; background-color: #e0e0e0;
	-moz-box-shadow: 0 0 5px black; -webkit-box-shadow: 0 0 5px black; box-shadow: 0 0 5px black; }

.title,.title td { font-weight: bolder;}

.question .title { font-size: 11pt;	background-color: #b0c4de; 
	-moz-box-shadow: none; -webkit-box-shadow: none; box-shadow: none; }

.question { background-color: #b0c4de; padding: 5px; font-weight: bold; border-top: 2px solid #E0E0E0; }

.question-c .title { background-color: #b0c4de; }

.question-c { background-color: #b0c4de; padding: 5px; }

.question-zc, .question-zc .title{ background-color: #b0c4de; border: none; }

.question-d .title { background-color: transparent; }

.question-d { background-color: transparent; padding: 2px; }

.answer .title { background-color: transparent; font-size: 10pt; font-weight: normal; padding: 2px; }

.answer{ font-weight: normal;}


/* TOOLTIS, ADD STUFF */
.tooltip { z-index: 100; background-color: rgb(255, 255, 180); background-color: rgba(255, 255, 180, 0.725);
	padding: 5px; -moz-border-radius-bottomright: 5px; -webkit-border-bottom-right-radius: 5px;
	-moz-box-shadow: 3px 3px 3px #888; -webkit-box-shadow: 3px 3px 3px #888; box-shadow: 3px 3px 3px #888;
	border: 1px solid #BBB; font-weight: normal; color: #555; }

.disabled { background: #E0E0E0; }
	
.inactive {	background-color: #d3d3d3; color: grey; }

.inactive .title, .inactive .title td, .inactive .answer, .inactive input{ background-color: #d3d3d3; color: grey; }