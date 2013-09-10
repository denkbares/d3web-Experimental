$basis()$

body,table,div {
	padding: 0px;
	margin: 0px;
	font-family: Arial;
	font-size: 11pt;
	text-color: black;
}

.question {
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
	font-weight: bolder;
	background-color: #e0e0e0;
	min-height: 35px;
}

.sub-question {
	padding-left: 35px;
}

.question-closed>.title {
	padding-left: 35px;
	background-image: url("images/closed_folder.png");
	background-repeat: no-repeat;
	background-position: 3px 3px;
}

.answer {
	padding: 4px;
}

.question-open>.title {
	padding-left: 35px;
	background-image: url("images/open_folder.png");
	background-repeat: no-repeat;
	background-position: 3px 3px;
}

.color-even {
	background-color: #c88;
}

.color-odd {
	background-color: #cbb;
}

#head{
	font-size: 2em;
	font-variant: small-caps;
	text-align: center;
	background: #e7e7e7;
	background-image: none;
	padding: 5px;
}