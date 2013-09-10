/**
 * Alina Coca
 * BA - Diagnose Visualisierung
 */

//funtion for opening a Clarification Dialog from another
function openFirstCloseSecond(firstPopUp,secondPopUp){
      $("#"+secondPopUp+"_popup").removeClass("selected");
      $("#"+firstPopUp+"_popup").addClass("selected");
}

//funtion for opening a treemap in a new tab-window: 
//sends the explanation string to the url of the new tab
function openPopupContent(contentx){
   contentx = contentx.split("ä").join("#0");
   contentx = contentx.split("ö").join("#1");
   contentx = contentx.split("ü").join("#2");
   contentx = contentx.split("Ä").join("#3");
   contentx = contentx.split("Ö").join("#4");
   contentx = contentx.split("Ü").join("#5");
   contentx = contentx.split("ß").join("#6");
   window.open("js/treemap/treemapVisualisation.html?"+contentx);
  
}
