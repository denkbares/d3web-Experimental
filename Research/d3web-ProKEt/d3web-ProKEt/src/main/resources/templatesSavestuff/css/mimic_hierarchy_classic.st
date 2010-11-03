$basis()$

body,table,div {
	padding: 0px;
	margin: 0px;
	font-family: "Times New Roman", Times, serif;
	font-size: 11pt;
	text-color: black;
}

.answer {
	background-color: transparent;
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

.title {
	padding: 4px;
	vertical-align: middle;
	background-color: #e0e0e0;
	min-height: 35px;
}

.sub-answer {
	padding-left: 35px;
}

.answer-closed>.title {
	padding-left: 35px;
	background-image: url("images/closed_folder.png");
	background-repeat: no-repeat;
	background-position: 3px 3px;
}

.answer {
	padding: 4px;
}

.answer-open > .title {
	padding-left: 35px;
	background-image: url("images/open_folder.png");
	background-repeat: no-repeat;
	background-position: 3px 3px;
}

.question-content, .answer {
	padding: 0px;
}

.color-even {
	background-color: #eee;
}

.color-odd {
	background-color: #ddd;
}