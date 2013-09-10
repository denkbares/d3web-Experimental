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
	background-color: rgb(255, 215, 0);
	background-color: rgba(255, 215, 0, 0.725);
	padding: 5px;
	-moz-border-radius-bottomright: 10px;
	-webkit-border-bottom-right-radius: 10px;
	-moz-border-radius-topright: 10px;
	-webkit-border-top-right-radius: 10px;
	-moz-border-radius-bottomleft: 10px;
	-webkit-border-bottom-left-radius: 10px;
	-moz-border-radius-topleft: 10px;
	-webkit-border-top-left-radius: 10px;
	-moz-box-shadow: 0 0 10px #888;
	-webkit-box-shadow: 0 0 10px #888;
	box-shadow: 0 0 1px #888;
	font-weight: normal;
}

.title {
	padding: 4px;
	vertical-align: middle;
	min-height: 20px;
	-moz-box-shadow: 0 0 3.5px black;
	-webkit-box-shadow: 0 0 3.5px black;
	box-shadow: 0 0 3.5px black;
	margin-top: 5px;
}

.sub-answer { padding-left: 35px; }

.answer-closed>.title { padding-left: 35px; background-image: url("images/closed_folder.png"); background-repeat: no-repeat; background-position: 3px 3px; }

question-content, .answer { padding: 0px; }

.answer-open>.title { padding-left: 35px; background-image: url("images/open_folder.png"); background-repeat: no-repeat; background-position: 3px 3px; }

.color-even { background-color: #E0E0E0;}

.color-odd { background-color: #F3F3F3; }

.solution-title{ background-color: #f5f5dc; padding-left: 10px;}

#head{ font-size: 1.5em; font-variant: small-caps; text-align: center;	background: #e7e7e7; background-image: none; padding: 5px; }

#content{width: 85%; margin-left: auto; margin-right: auto; float: none;}