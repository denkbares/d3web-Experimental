$basis()$
$standardLeft()$

body,table,div {
	font-family: Arial;
	font-size: 11pt;
	margin: 0px;
	padding: 0px;
	text-color: black;
}
.dialog {
	padding: 0px 5px;
}
#left {
	box-shadow: none;
	margin-top: 1%;
	-moz-box-shadow: none;
	-webkit-box-shadow: none;
}
#sidetext {
	background: #ffffc8;
	padding: 1em;
	margin-bottom: 3em;
}
.question {
	clear: left;
}

/* popup for adding new answers to the viewport */
#__popup {
  background-color:white;
  border: 2px solid orange;
  padding: 5px;
}


.questionnaire .title, .questionnaire .title td {
	background-color: transparent;
	font-size: 1.25em;
	font-weight: bolder;
	-moz-border-radius: 0px;
	padding: 0px;
	-webkit-border-radius: 0px;
	vertical-align: middle;
}
.questionnaire-content {
	padding-bottom: 2em;	
}

.question .title, .question-c .title-c, .question-d .title-d {
	background: transparent;
	font-size: 1.0em;
	font-weight: bolder;
	padding: 2px;
}

.tooltip {
	background-color: rgba(255, 255, 155, 0.825);
	font-weight: normal;
	-moz-border-radius-bottomright: 5px;
	-moz-box-shadow: 3px 3px 3px #888;
	padding: 5px;
	-webkit-border-bottom-right-radius: 5px;
	-webkit-box-shadow: 3px 3px 3px #888;
	z-index: 100;
}
.questionnaire {
	background: transparent url(images/arrow.gif) no-repeat top left;
	padding-left: 20px;
}
.questionnaire-content {
	margin-top: 5px;
}
.question-content {
	float: left;
	padding: 0px 5px;
}
.answer {
	float: left;
	margin-right: 2px;
}
.preselected, .selected, .not-selected {
	margin-top: 1px;
	-moz-border-radius: 2px;
	padding: 0px 2px;
	-webkit-border-radius: 2px;
}
.preselected {
	border: 2px solid green;
}
.selected {
	border: 2px solid red;
}
.not-selected {
	border: 2px solid white;
}
input[type=text] {
	border: 1px dashed black;
}
#head {
	background: #f5fbff;
	background-image: none;
	padding: 0px;
	margin-top: 0px;
}
td {
	vertical-align: top;
}

.dialog > .title, .dialog > .title td  {
	font-size: 2em;
	vertical-align: middle;
}

#content {
	float: left;
	margin: 0px 1% 0px 1%;
	width: 70%;
}
#dialog-content {
	margin-bottom: 15px;
}

.weight-box {
	min-width: 2ex;
	border-bottom: 1px solid black;
	height: 1.2em;
	text-align: center;
}

.rating-box {
	margin-left: 3em;
	font-size: 0.5em;
}

.questionnaire .title .rating-box {
	font-size: 0.8em;
}

.answer, .answer td {
	font-style:italic;
}

.answer .rating-high {
	background-color: #9f9;
}

.answer .rating-medium {
	background-color: #ff9;
}

.answer .rating-low {
	background-color: #f99;
}

