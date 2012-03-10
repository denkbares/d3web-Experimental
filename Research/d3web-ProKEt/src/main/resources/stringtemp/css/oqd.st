$basis()$

body,div {
	padding: 0px;
	margin: 0px;
	font-family: Arial;
	font-size: 10pt;
	text-color: black;
}

#content {float: none;margin-left: auto; margin-right: auto; width: 100%;
    clear: both;}

#right {display: none;}

#left {display: none;}

#head{
    background: #e7e7e7;
    background-image: none;
    clear: both;
    min-height: 35px;
    width: 100%;
}

#headertext{
	font-size: 1.5em;
	font-variant: small-caps;
	text-align: center;
	margin-right: 10%;
        margin-left: 10%;
	padding: 5px;
        position: fixed;
        width: 70%;
}

#intro{ 
    background-color: white;
    font-family: Arial;
    font-size: 8pt;
    left: 0;
    padding-bottom: 20px;
    position: fixed;
    right: 0;
}

#introtext{
background-color: white;
    font-family: Arial;
    font-size: 8pt;
    left: 10%;
    padding: 10px;
    position: fixed;
    right: 10%;
}

#dialog{
    float: left;
    margin-top: 190px;
}


table{
	margin-top: 10px;
	width: 100%;
}


.question {
	background-color: transparent;
        margin-top: 10px;
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
	vertical-align: middle;
	background-color: #e0e0e0;
	-moz-box-shadow: 0 0 3.5px black;
	-webkit-box-shadow: 0 0 3.5px black;
	box-shadow: 0 0 3.5px black;
}

.question .title {
    font-weight: bold;
    line-height: 1.75em;
    margin-left: 15px;
    min-height: 24px;
    padding-bottom: 1px;
    padding-left: 15px;
}


/* initial coloring of questions: alternating greytones */
.color-even {
	background-color: #ddd;
}
.color-odd {
	background-color: #eee;
}


/* rating colors for question backgrounds */
.rating-high, .rating-medium, .rating-low { 
    border: 0px solid black; 
}
.rating-high > .title { 
    background-color: #C3DAA8; border: none; 
}
.rating-medium > .title { 
    background-color: #F4F4C7; border: none; 
}
.rating-low > .title { 
    background-color: #E0B59E; border: none; 
}


/* Styles the box containing the button panel */
.question-imagebox { 
    line-height: 1.5em; float: left; padding-left: 2px;
}


/* Style the appearance of area fields, as in the button panel */
area {
    border: none;
}


/* Styles the div that displays hints of the readflow, e.g. "und/and, oder/or"...*/
.readflow{
    float: left; clear: both; margin-top: 7px;
}


/* Styling of the div within questions that displays the currently propagated value */
.propagation{
     float: right;
    height: 22px;
    padding-right: 4px;
    width: 150px;
}
.propagation.rating-high { 
    background-color: #C3DAA8; border: 1px solid grey; 
}
.propagation.rating-medium { 
    background-color: #F4F4C7; border: 1px solid grey; 
}
.propagation.rating-low { 
    background-color: #E0B59E; border: 1px solid grey; 
}

.question-toggleImg { 
    line-height: 1.5em; padding: 2px; float: left; margin-left: 15px; margin-right: 5px;
}

/* style the num input area of hierarchical num questions */
.floatingnum {line-height: 1.5em; padding: 2px; float: left; margin-right: 15px;}

.floatingnum input {height: 10px; margin-top: 1px; margin-left: 17px;}


/* style the dropdown area of hierarchical oc questions */ 
.floatingCDrop {line-height: 1.5em; padding: 3px; float: left; margin-right: 15px;}

.floatingCDrop select{width: 54px; margin-left: 17px;}

.ynbutton {
    background-color: #DCDFF3;
    border: 1px solid #696969;
    box-shadow: 0 0 5px black;
    cursor: pointer;
    float: left;
    font-size: 0.8em;
    font-weight: bold;
    height: 16px;
    margin-right: 10px;
    margin-top: 4px;
    text-align: center;
    width: 28px;
}

.dialog99Width{
    width: 99%;
}

.dialog75Width{
    width: 75%;
}

.auxPropagationInfo{color: #800000; font-size: 1.1em;}

.hide{display: none;}

.show{display: block;}

.titletext {
    font-weight: bold;
    padding-left: 40px;
}

.auxpanelBottom{
    background-color: #fffff0;
    -moz-box-shadow: 0 0 3.5px black;
	-webkit-box-shadow: 0 0 3.5px black;
	box-shadow: 0 0 3.5px black;
    padding: 10px;
    height: 20%;
    overflow: auto;
    line-height: 1.1em;
    margin-top: 5px;
    font-weight: normal;
    clear: both;
    margin-right: 15px;
}

.detailButton{width: 50px; height: 18px; 
        border: 1px solid #696969; font-size: 0.8em; font-weight: bold;
        text-align: center; cursor: pointer; 
	background-color: #DCDFF3; 
        -moz-box-shadow: -2px -2px 5px black;	
	-webkit-box-shadow: 0 0 5px black; 
        box-shadow: 0 0 5px black; 
        margin-bottom: 12px;
        margin-top: 2px;
        margin-right: 10px;
        clear: both;
        float: left;
        }

.solution{
    box-shadow: 0 0 3.5px black;
    clear: both;
    font-size: 1.5em;
    font-weight: bold;
    margin-left: auto;
    margin-right: auto;
    margin-top: 110px;
    padding: 30px;
    width: 60%;
}

.solRating {
      background-color: #DEDEDE;
    float: right;
    font-weight: bold;
    padding: 2px 30px;
}

.solRating.rating-high { 
    background-color: #C3DAA8; 
}
.solRating.rating-medium { 
    background-color: #F4F4C7; 
}
.solRating.rating-low { 
    background-color: #E0B59E;  
}

#solutionboxtext{
    font-size: 0.6em;
    font-variant: small-caps;
    font-weight: normal;
    margin-left: -25px;
    margin-top: -25px;
}

#solutiontext{
     float: left;
    padding: 2px 0;
}

.solutiontitle{
    float: left;
    min-height: 16px;
	line-height: 1.75em;
        margin-left: 15px;
        margin-bottom: 5px;
}