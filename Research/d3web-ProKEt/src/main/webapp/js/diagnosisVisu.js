/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function openFirstCloseSecond(firstPopUp,secondPopUp){
      $("#"+secondPopUp+"_popup").removeClass("selected");
      $("#"+firstPopUp+"_popup").addClass("selected");
}
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
