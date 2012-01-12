$basis()$

body,div {
	padding: 0px;
	margin: 0px;
	font-family: Arial;
	font-size: 10pt;
	text-color: black;
}

#content {float: none;margin-left: auto; margin-right: auto; width: 85%;
    clear: both; padding-top: 35px;}

#right {display: none;}

#left {display: none;}

#head{
	font-size: 1.5em;
	font-variant: small-caps;
	text-align: center;
	background: #e7e7e7;
	background-image: none;
	padding: 5px;
        position: fixed;
        margin: 0px;
}

#intro{ 
    font-family: Arial;
	font-size: 8pt;
	text-color: darkgrey;
        position: fixed; 
        left: 10%; right: 10%;
        background-color: white;
padding: 10px;
}

#dialog{
    float: left;
    width: 60%;
    margin-top: 60px;
}

#auxpanel{
    background-color: #fffff0;
    width: 25%;
    height: 250px;
    -moz-box-shadow: 0 0 3.5px black;
	-webkit-box-shadow: 0 0 3.5px black;
	box-shadow: 0 0 3.5px black;
    position: fixed;
    right: 65px;
    padding: 10px;
    margin-top: 80px;
}


table{
	margin-top: 10px;
	width: 100%;
}


.question {
	background-color: transparent;
        margin-top: 10px;
        margin-left: 20px;
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
	box-shadow: 3px 3px 3px #888;
	border: 1px solid black;
	font-weight: normal;
}

.title {
	padding-left: 4px;
	padding-right: 4px;
	vertical-align: middle;
	background-color: #e0e0e0;
	-moz-box-shadow: 0 0 3.5px black;
	-webkit-box-shadow: 0 0 3.5px black;
	box-shadow: 0 0 3.5px black;
}

.question .title {
	min-height: 16px;
	padding-left: 90px;
	line-height: 1.75em;
        margin-left: 15px;
}

.question-closed {
	padding-left: 30px;
	background: transparent url(images/closed_folder.png) no-repeat top
		left;
}

.question-open {
	padding-left: 30px;
	background: transparent url(images/open_folder.png) no-repeat top left;
}

.color-even {
	background-color: #ddd;
}

.color-odd {
	background-color: #eee;
}

.question-yes {
	background-color: #C3DAA8;
}

.question-no {
	background-color: #E0B59E;
}

.question-undecided{
	background-color: #F4F4C7;
}

.question-unanswered {
	
}

/* rating colors */
.rating-high, .rating-medium, .rating-low { border: 0px solid black; }
.rating-high > .title { background-color: #C3DAA8; border: none; }
.rating-medium > .title { background-color: #F4F4C7; border: none; }
.rating-low > .title { background-color: #E0B59E; border: none; }

.question-imagebox {
	line-height: 1.5em;
	padding: 4px;
	float: left;
        margin-left: 15px;
        margin-right: 15px;
}

area {border: none;}

.readflow{float: left; clear: both; margin-top: 10px;}