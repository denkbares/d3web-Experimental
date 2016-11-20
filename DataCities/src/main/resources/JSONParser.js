/**
 * Created by Lea on 30.05.2016.
 */
//bgz_city/bgz_city_sample
function start(){
  if(json == undefined) {
    startParser();
  }
  else  createCityFromJSON(json);
}

function startParser (){
  readTextFile("Samples/bgz_city_full_sample.json", function(text){
    var json = JSON.parse(text);
    createCityFromJSON(json);
  });
}



function createCityFromJSON (json){
  init();
  var color, distanceBuildings, distanceDistricts, label;

  if(json.label) label = json.label;
  else label = {text: "city"};

  if(json.color) color = json.color;
  else color = "#002D29";

  if(json.distanceBuildings) distanceBuildings = json.distanceBuildings;
  else distanceBuildings = 5;

  if(json.distanceDistricts) distanceDistricts = json.distanceDistricts;
  else distanceDistricts = 10;

  var city = new City(distanceBuildings, distanceDistricts, label, color);
  //Go through the City's districts
  getDistricts(json, city);
  city.render(json.sorted, json.isTable, json.columns);


  render();
}

function getBuildings(dataDistrict, district){
  var dataBuilding;
  var building;
  var color, width, height, depth;
  dataDistrict.Buildings.sort(function(a, b){ return a.sortKey - b.sortKey});
  for (var j = 0; j < dataDistrict.Buildings.length; j++) {
    dataBuilding = dataDistrict.Buildings[j];
    if(dataBuilding.color) color = dataBuilding.color;
    else color = "#EFEFEF";
    
    if(dataBuilding.width) width = dataBuilding.width;
    else width = 1;
    
    if(dataBuilding.depth) depth = dataBuilding.depth;
    else depth = 1;
    
    if(dataBuilding.height) height = dataBuilding.height;
    else height = 1;
    
    building = new Building(width, depth, height, district, dataBuilding.label, color);
  }
}

function getDistricts (data, containingObj){
  var dis;
  var dataDistrict;
  var color;
  data.Districts.sort(function(a, b){ return a.sortKey - b.sortKey});
  for(var i = 0; i < data.Districts.length; i++){
    dataDistrict = data.Districts[i];
    if(dataDistrict.Districts){
      //current District contains other districts
      if(dataDistrict.color){
        color = dataDistrict.color;
      }
      else{
        color = "#808080";
      }
      dis = new District(containingObj, dataDistrict.label, color, true);
      getDistricts(dataDistrict, dis);
    }
    else{
      //Current districts doesn't contain other districts
      if(dataDistrict.color){
        color = dataDistrict.color;
      }
      else{
        color = "#808080";
      }
      dis = new District(containingObj, dataDistrict.label, color, false);
      getBuildings(dataDistrict, dis);
    }
  }
}

start();
