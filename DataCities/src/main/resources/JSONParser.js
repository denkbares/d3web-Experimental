/**
 * Created by Lea on 30.05.2016.
 */
//bgz_city/bgz_city_sample
function start(){
  if(json == undefined) {
    startParser();
  }
  else startExistingJson();
}

function startParser (){
  readTextFile("Samples/table_sample.json", function(text){
    var json = JSON.parse(text);
    createCityFromJSON(json);
  });
}

function startExistingJson (){
  createCityFromJSON(json);
}

function createCityFromJSON (json){
  init();
  var color;
  if(json.color){
    color = json.color;
  }
  else color = "#002D29";
  var city = new City(40, 40, json.Label, color);
  //Go through the City's districts
  getDistricts(json, city);
  city.render(json.sorted, json.isTabel, json.columns);


  render();
}

function getBuildings(dataDistrict, district){
  var dataBuilding;
  var building;
  var color;
  dataDistrict.Buildings.sort(function(a, b){ return a.sortKey - b.sortKey});
  for (var j = 0; j < dataDistrict.Buildings.length; j++) {
    dataBuilding = dataDistrict.Buildings[j];
    if(dataBuilding.color) color = dataBuilding.color;
    else color = "#EFEFEF";
    building = new Building(dataBuilding.width, dataBuilding.depth, dataBuilding.height, district, dataBuilding.Label, dataBuilding.color);
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
      dis = new District(containingObj, dataDistrict.Label, color, true);
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
      dis = new District(containingObj, dataDistrict.Label, color, false);
      getBuildings(dataDistrict, dis);
    }
  }
}

start();
