$basis()$

body,table,div {
	padding: 0px;
	margin: 0px;
	font-family: Arial;
	font-size: 11pt;
	text-color: black;
}

.question {
	clear: left;
}

.group .title {
	margin: 2px;
	padding: 4px 18px;
	font-weight: bolder;
	background-color: #ccf;
	min-width: 300px;
}

.question .title {
	margin-left: 10px;
	padding: 4px;
	font-weight: bold;
	min-width: 300px;
	background: transparent;
}

.question-c .title-c {
	background-color: #ffffcc;
}

.question-d .title-d {
	background-color: #bbbbbb;
}

.tooltip {
	z-index: 100;
	background-color: rgb(255, 255, 66);
	background-color: rgba(255, 255, 66, 0.925);
	padding: 5px;
	-moz-border-radius-bottomright: 5px;
	-webkit-border-bottom-right-radius: 5px;
	-moz-box-shadow: 3px 3px 3px #888;
	-webkit-box-shadow: 3px 3px 3px #888;
	border: 1px solid black;
	font-weight: normal;
}

.group {
	padding-left: 30px;
	background: transparent url(images/arrow.gif) no-repeat top left;
}

.group>.title {
	font-size: 1.5em;
}

.group-content {
	padding: 5px;
}

.question-content {
	float: left;
	padding: 0px 4px;
}

.answer {
	float: left;
	margin-right: 2px;
}

.selected {
	border: 3px solid red;
	padding: 2px;
}

.not-selected {
	padding: 5px;
}


input[type=text] {
	border: 1px dashed black;
}