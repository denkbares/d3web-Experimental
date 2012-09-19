/**
* General basic CSS statements for the DocLoad Module
*
* @author	Martina Freiberg
*/



.row{display: table-row;}

.cell{display: table-cell; }

.labelcellFirstRow{margin-top:-20px;}

.labelcell{font-family: fantasy;
    font-size: 1.1em;
    font-weight: bold;
    padding-bottom: 10px;
    padding-top: 20px;}

.selectcell{min-width: 200px; padding: 5px; align: center; vertical-align: middle;}

.firstcell{background: white; -moz-border-radius: 15px 0px 0px 15px; 
    border-radius: 15px 0px 0px 15px; border: 2px solid white;
     }

.lastcell{background: white; -moz-border-radius: 0px 15px 15px 0px; 
    border-radius: 0px 15px 15px 0px; border: 2px solid white;}

.buttoncell{min-width: 50px; padding: 5px;}

#docloadKBName{color:#777777; font-weight: bold; 
    padding: 5px; padding-left:15px;}

#docloader{display: table; background-color:	#CCCC99; padding: 10px; 
    -moz-border-radius: 15px; border-radius: 15px; border: 2px solid  	#CCCC99;}

input{border: 1px dotted darkgrey;
    border-radius: 5px 5px 5px 5px;
    height: 20px;
    width: 240px;
    padding-left: 5px;}

.imgButtonQuad img{margin-top: 8px; width: 50px; height: 50px;}

.spacerow{height: 10px;}

.baseDTBrowser{margin-left:10px; }

.baseDTBrowser select{font-family: Verdana; height: 30px; min-height:30px;
    padding-top:5px; padding-left: 10px; min-width: 250px; line-height: 30px;}

.baseDomainBrowser{margin-left:10px;}

.baseFileBrowser{margin-left:10px; position: relative;margin-top:-10px;}

.baseDomainBrowser input{width:240px;}


#docfilename{left: -1px;
    min-width: 200px;
    position: absolute;
    top: -1px;
    margin-top: 10px;
    
}

#specfilename{left: -1px;
    min-width: 200px;
    position: absolute;
    top:31px;
    margin-top: 10px;
    }

#docfilebutton{width: 70px; height: 24px;
    left: 220px;
    min-width: 25px;
    position: absolute;
    top: -1px;
margin-top: 10px;}

#specfilebutton{width: 70px;
    height: 24px;
    left: 220px;
    min-width: 25px;
    position: absolute;
    top:31px;
    margin-top: 10px;
    }

.opacityCell{opacity: 0; position: absolute; z-index: 2;
    height: 50px;
    left: 368px;
    position: absolute;
    top: 85px;
    width: 50px;}

#KBUploadStatus{display: none; font-family: Verdana;  font-size: 0.7em;
    margin-top: 5px;
    padding-left: 15px; color: darkred;}

#SPECUploadStatus{display: none; font-family: Verdana;  font-size: 0.7em;
    margin-top: 5px;
    padding-left: 15px; color: darkred;}

#latestDialogLink{display: none; font-family: Verdana;  font-size: 0.7em;
    margin-top: 5px;
    padding-left: 15px; color: blue;}

#origKBUploadField,#origSpecUploadField {width: 220px; position: relative; z-index:2; opacity:0;}

#origKBUploadField{margin-top:15px;}

#origSpecUploadField{margin-top:10px;}

#origKBUploadButton,#origSpecUploadButton {width: 60px; position: relative; z-index:2; opacity:0;}

#specUploadRow{margin-top:10px;}

#uploadRow{height: 80px;}