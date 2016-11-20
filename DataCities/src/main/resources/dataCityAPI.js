/**
 * Created by Lea on 12.05.2016.
 */

//Checks whether an array of positions contains a given position
function containsPosition(arr, pos){
  for(var i = 0; i < arr.length; i++){
    if(arr[i].x == pos.x && arr[i].z == pos.z){
      return true;
    }
  }
  return false;
}

//City-Class-Constructor
function City (distanceBuildings, distanceDistricts, label, color){
  this.width = 1;
  this.depth = 1;
  this.label = label;
  this.color = color.replace("#", "0x");
  this.geometry;
  this.map = [[]];
  this.containedObjs = [];
  this.distanceBuildings = distanceBuildings;
  this.distanceDistricts = distanceDistricts;
}

City.prototype = {

  //adds a district to the Districts-Array of the City
  addDistrict: function (district) {
    this.containedObjs.push(district);
  },

  getDistanceDistricts: function (){
    return this.distanceDistricts;
  },

  getDistanceBuildings: function(){
    return this.distanceBuildings;
  },


  //Adjust the Size of the city for the given values
  adjustSize: function (width, depth) {
    if(width > 0){
      this.width += width;
      this.map.length = this.width;
    }
    if(depth > 0) {
      this.depth += depth;
    }
    for (var i = 0; i < this.map.length; i++) {
      if (this.map[i] == undefined) {
        this.map[i] = [];
      }
      this.map[i].length = this.depth;
    }

    changeSizeCity(this.geometry, this.width, this.depth);
    updateOrbitControls(this.width/2, this.depth/2);
  },

  // Creates parameters columnWidth, rowDepth, sumColumns and sumRows which are needed to render a table-city.
  prepareTableRender: function(columns){
    var current;
    var columnWidth = []; //Contains a number for each column that states the columns minimum width
    for(var i = 0; i < columns.length; i++){
      columnWidth.push(0);
    }
    var rowDepth = [];   //Contains a number for each row that states the rows minimum depth
    var sumColumns = 0;  // minimum total width of the table
    var sumRows = 0;     // minimum total depth of the table
    for(i = 0; i < this.containedObjs.length; i++){
      current = this.containedObjs[i];
      rowDepth.push(0);
      for(var j = 0; j < current.containedObjs.length; j++){
        if(current.containedObjs[j].depth > rowDepth[i]){
          rowDepth[i] = current.containedObjs[j].depth;
        }
        if(columnWidth.length < j){
          columnWidth.push(current.containedObjs[j].width);
        }
        else if(current.containedObjs[j].width > columnWidth[j]){
          columnWidth[j] = current.containedObjs[j].width;
        }
      }
      sumRows += rowDepth[i]*2 + this.distanceDistricts;
    }
    for(i = 0; i < columnWidth.length; i++){
      sumColumns += columnWidth[i] + this.distanceBuildings;
    }

    return {columnWidth: columnWidth, rowDepth: rowDepth, sumRows: sumRows, sumColumns: sumColumns};
  },

  //Rendering of a table-city
  renderTable: function(columns){
    var current;
    var columnWidth, rowDepth, sumColumns, sumRows;
    current = this.prepareTableRender(columns);
    columnWidth = current.columnWidth;  //Contains a number for each column that states the columns minimum width
    rowDepth = current.rowDepth;        //Contains a number for each row that states the rows minimum depth
    sumColumns = current.sumColumns;    // minimum total width of the table
    sumRows = current.sumRows;          // minimum total depth of the table
    var position = {x: 0, y: 0};
    //Successively rendering the rows / districts
    for(i = 0; i < this.containedObjs.length; i++){
      current = this.containedObjs[i];
      current.renderTable(columnWidth);
      //Sets each districts size to its minimum depth and the minimum width of the whole table
      current.adjustSize(sumColumns, rowDepth[i]*2);
      //Adjust the position to get a gap between the districts border and the buildings
      changePositionPlane(current.geometry, -this.distanceBuildings*0.5, -rowDepth[i]*0.5);
      current.move(position.x, position.y);
      position.y += rowDepth[i]*2 + this.distanceDistricts;
    }
    //Sets the city size
    this.adjustSize(sumColumns, sumRows);
    //Adjust the position to get a gap between the city border and the districts
    this.adjustSize(this.distanceDistricts, 0.5 * this.distanceDistricts);
    changePositionPlane(this.geometry, -this.distanceBuildings, -this.distanceBuildings);

    this.createTableLabels(columns, columnWidth);

  },
  //Creates the labels of the rows & columns
  createTableLabels: function (columns, columnWidth){
    //First column-label-position is set one distanceBuilding from the city border
    var position = {x: -this.distanceDistricts+this.distanceBuildings*0.5, y: 0, z: -(this.distanceDistricts+10)};
    for(var i = 0; i < columns.length; i++){
      createLabel(columns[i],{x: position.x, y: position.y, z: position.z - ((i % 2)*40)});
      position.x += columnWidth[i] + this.distanceBuildings;
    }
    var current;
    //Row-Labels; set to the left edge of the city and the middle of the related district
    for(i = 0; i < this.containedObjs.length; i++){
      current = this.containedObjs[i];
      createLabel(current.label.text, {x: current.position.x -400, y: 0, z: current.position.z + 15}, current.color);
    }
  },

  render: function(sorted, isTable, columns){
    this.geometry = createCity(this.label, this.color);
    var insertedObjs = [];
    var position;
    var current;
    var candidates;
    if(isTable){
      this.renderTable(columns);
    }
    else {
      if (sorted) {
        this.containedObjs.sort(function (a, b) {
          return (b.getHeight() - a.getHeight())
        });
      }
      //Go through the districts and insert them in the given order
      for (var i = 0; i < this.containedObjs.length; i++) {
        current = this.containedObjs[i];
        current.render(sorted);
        //Search for possible positions
        candidates = this.getPositionCandidates(this.containedObjs[i], insertedObjs);
        //Choose the best position (= smallest distance to (0|0)
        position = this.getBestCandidate(candidates, current);
        current.move(position.x, position.z);
        this.markMap(position, current.width, current.depth);
        insertedObjs.push(current);
      }
      //Adjust the position to get a gap between the districts border and the buildings
      this.adjustSize(Math.round(0.5 * this.distanceDistricts), Math.round(0.5 * this.distanceDistricts));
    }
  },

  //Marks the distance between districts around a given district / position. (Parameters: districts position, districts width, districts depth)
  markDistanceInMap: function(position, width, depth){
    var dis = this.distanceDistricts;
    var difX = (position.x + dis + width) - this.width;
    var difZ = (position.z + dis + depth) - this.depth;
    if(difX > 0 || difZ > 0){
      this.adjustSize(difX, difZ);
    }

    for(var i = position.x-dis; i < position.x; i++){
      for(var j = position.z - dis; j < (position.z + depth); j++){
        this.map[i][j] = true;
      }
    }

    for(i = position.x-dis; i < (position.x + width); i++){
      for(j = (position.z + depth); j < (position.z + depth+ dis); j++){
        this.map[i][j] = true;
      }
    }

    for(i = (position.x + width); i < (position.x + width + dis); i++){
      for(j = position.z; j < (position.z + depth + dis); j++){
        this.map[i][j] = true;
      }
    }

    for(i = position.x; i < (position.x + width + dis); i++){
      for(j = position.z - dis; j < (position.z); j++){
        this.map[i][j] = true;
      }
    }
  },

  //Marks a district in the map
  markMap: function(position, width, depth){
    //Set the district & the map to the minimal needed width & depth
    this.adjustSize((position.x +width - this.width), (position.z + depth  - this.depth));
    //Mark the district
    for(var i = position.x; i < position.x + width; i++){
      for(var j = position.z; j < position.z + depth; j++){
        this.map[i][j] = true;
      }
    }
    //Mark the distance
    this.markDistanceInMap(position, width, depth);
  },

  //Search for all Positions where the current building can be inserted
  getPositionCandidates: function (obj, insertedObjs){
    var x = 0;
    var z = 0;
    var candidates = [];
    var current;
    var position;
    //Go through the buildings already inserted & check whether there is a possible Position at their edges
    for(var i = 0; i < insertedObjs.length; i++){
      current = insertedObjs[i];
      x = current.position.x + current.width;
      z = current.position.z + current.depth;
      //For each possible Position at the edge of the current inserted building try putting the new building in
      for(var j = 0; j <= current.width; j++){
        position = {x: current.position.x + j, z: z  + this.distanceDistricts};
        if(this.checkPosition(obj, position.x, position.z)){
          //If the position fits put it into the candidates array and go on (As all the other possible positions would be worse)
          if(!containsPosition(candidates,position)) {
            candidates.push(position);
            break;
          }
        }
      }
      for(j = 0; j < current.depth; j++){
        position = {x: x + this.distanceDistricts, z: current.position.z + j};
        if(this.checkPosition(obj, position.x, position.z)){
          if(!containsPosition(candidates,position)) {
            candidates.push(position);
            break;
          }
        }
      }
    }
    //Check if there are positions at the edge of the map
    for(i = this.distanceDistricts; i < this.map.length; i++){
      position = {x: i, z: this.distanceDistricts};
      if(this.checkPosition(obj, position.x, position.z)){
        if(!containsPosition(candidates,position)) {
          candidates.push(position);
          break;
        }
      }
      position = {x: this.distanceDistricts, z: i};
      if(this.checkPosition(obj, this.distanceDistricts, i)){
        if(!containsPosition(candidates,position)) {
          candidates.push(position);
          break;
        }
      }
    }
    position = this.getNextPosition();
    if(!containsPosition(candidates, position)){
      candidates.push(position);
    }
    return candidates;
  },

  //Returns the next free position at the shortest edge of the city
  getNextPosition: function(){
    var position;
    if(this.map[0].length == 0){
      position = {x:this.distanceDistricts, z:this.distanceDistricts};
    }
    else {
      if (this.map[0].length <= this.map.length) {
        position = {x: this.distanceDistricts, z: this.map[0].length};
      }
      else {
        position = {x: this.map.length, z: this.distanceDistricts};
      }
    }
    return position;
  },

  //Check whether the new Building fits in the position
  checkPosition: function(obj, x, z){
    //Go through the rows and check whether there is space for the new building
    for(var i = 0; i < obj.width + this.distanceDistricts; i++){
      //If the end of the map is reached one can be sure the building fits in (in the current column / row)
      if(x+i >= this.map.length) break;
      for(var j = 0; j < obj.depth + this.distanceDistricts; j++){
        if(this.map[x+i] == undefined || z+j >= this.map[i].length) break;
        if(this.map[x+i][z+j] == true){
          return false;
        }
      }
    }
    return true;
  },

  /**Returns the candidate with the smallest distance.
   * If there is more than one opportunity the position that won't extend the city is choosen.
   * candidates is an array of positions
   * obj is the district that needs to be placed
   **/
  getBestCandidate: function(candidates, obj){
    var best = candidates[0];
    var bestDistance = Math.sqrt(Math.pow(best.x, 2) + Math.pow(best.z, 2));
    var bestExtends = this.extendsThis(best, obj); //True if for the best position the city needs to be extended
    var dis = 0;
    var current;
    var currentExtends; //True if for the current position the city needs to be extended
    for(var i = 1; i < candidates.length; i++){
      current = candidates[i];
      currentExtends = this.extendsThis(current, obj);
      dis = Math.sqrt(Math.pow(candidates[i].x, 2) + Math.pow(candidates[i].z, 2)); //Pythagoras' theorem

      //If both will extend the city the one with the smaller distance is choosen
      if(bestExtends && currentExtends && bestDistance > dis ){
        best = current;
        bestDistance = dis;
        bestExtends = currentExtends;
      }
      //The candidate that doesn't extend the city is chosen
      else if(bestExtends && !currentExtends ){
        best = current;
        bestDistance = dis;
        bestExtends = currentExtends;
      }
      //If both won't extend the city the one with the smaller distance is choosen
      else if(!bestExtends && !currentExtends && bestDistance > dis){
        best = current;
        bestDistance = dis;
        bestExtends = currentExtends;
      }
    }
    return best;
  },

  //Checks wether an object will extend the city if set to the given position
  extendsThis: function(position, obj){
    var width = position.x + this.distanceDistricts + obj.width;
    var depth = position.z + this.distanceDistricts + obj.depth;
    return ( width > this.map.length || depth > this.map[0].length);
  }
};

//District-Class
function District(containingObj, label, color, containsDistricts){
  this.width = 1;
  this.depth = 1;
  this.position = {x: 0, y: 0, z: 0};
  this.label = label;
  this.color = color.replace("#", "0x");
  this.geometry;
  this.containedObjs = [];
  this.map = [[]];
  this.containingObj = containingObj;
  this.containsDistricts = containsDistricts;
  if(containingObj != null) {
    containingObj.addDistrict(this);
  }
}

District.prototype = {

  //Puts the given building in the Buildings-Array
  addBuilding: function (building) {
    this.containedObjs.push(building);
  },


  //Puts the given district in the Districts-Array
  addDistrict: function (district) {
    this.containedObjs
        .push(district);
  },

  //Adjust the size of the district for the given values
  adjustSize: function (width, depth) {
    width = Math.round(width);
    depth = Math.round(depth);
    //Adjust Attributes
    if(width > 0) {
      this.width += width;
      this.map.length = this.width;
    }
    if(depth >= 0) {
      this.depth += depth;
    }
    for (var i = 0; i < this.map.length; i++) {
      if (this.map[i] == undefined) {
        this.map[i] = [];
      }
      this.map[i].length = this.depth;
    }
    //Adjust Geometry
    changeSize(this.geometry, this.width, this.depth);

  },

  getDistance: function() {
    if(this.containsDistricts){
      return this.getDistanceDistricts();
    }
    else{
      return this.getDistanceBuildings();
    }
  },

  //Get the appointed distance between two buildings (contained only in the city)
  getDistanceBuildings: function () {
    return this.containingObj.getDistanceBuildings();
  },


  //Get the appointed distance between two districts
  getDistanceDistricts: function () {
    return this.containingObj.getDistanceDistricts();
  },


  //Return the average height of the district
  getHeight: function () {
    var height = 0;
    //If the district contains district: return the average of the districts' average hiehgts
    if (this.containsDistricts) {
      for (var i = 0; i < this.containedObjs
          .length; i++) {
        height += this.containedObjs
            [i].getHeight();
      }
      height = height / this.containedObjs
              .length;
      return height;
    }
    //Else: return the average of the buildings' heights
    else {
      for (var j = 0; j < this.containedObjs.length; j++) {
        height += this.containedObjs[j].getHeight();
      }
      height = height / this.containedObjs.length;
      return height;
    }


  },

  renderTable: function (columnWidth){
    this.geometry = createDistrict(this.position, this.label, this.color, this.containsDistricts);
    var position = {x: 0, y: 0};
    var current;
    for(var i = 0; i < this.containedObjs.length; i++){
      current = this.containedObjs[i];
      current.render();
      current.move(position.x, position.y);
      position.x += columnWidth[i] + this.getDistance();
    }
  },

  render: function (sorted) {
    this.geometry = createDistrict(this.position, this.label, this.color, this.containsDistricts);
    var insertedObjs = [];
    var position;
    var current;
    var candidates;
    if(sorted) {
      this.containedObjs.sort(function (a, b) {
        return (b.getHeight() - a.getHeight())
      });
    }
    for(var i = 0; i < this.containedObjs.length; i++){
      current = this.containedObjs[i];
      current.render(sorted);
      //Get all possible Positions
      candidates = this.getPositionCandidates(this.containedObjs[i], insertedObjs);
      //Pick out the best one (with the smallest distance to (0|0)
      position = this.getBestCandidate(candidates, current);
      current.move(position.x + this.position.x, position.z + this.position.z);
      this.markMap(position, current.width, current.depth);
      insertedObjs.push(current);
    }
    var distance = this.getDistance();
    this.adjustSize(distance*0.5, distance*0.5);
  },

  //Marks a building in the map (parameters: buildings position, buildings width, buildings depth)
  markMap: function(position, width, depth){
    //Set the district & the map to the minimal needed width & depth
    this.adjustSize((position.x + width - this.width), (position.z + depth  - this.depth));
    for(var i = position.x; i < position.x + width; i++){
      for(var j = position.z; j < position.z + depth; j++){
        this.map[i][j] = true;
      }
    }
    this.markDistanceInMap(position, width, depth);
  },

  //Marks the related distance of a building or a district in the map
  markDistanceInMap: function(position, width, depth){
    var dis = this.getDistance();
    var difX = (position.x + dis + width) - this.width;
    var difZ = (position.z + dis + depth) - this.depth;
    if(difX > 0 || difZ > 0){
      this.adjustSize(difX, difZ);
    }

    for(var i = position.x-dis; i < position.x; i++){
      for(var j = position.z - dis; j < (position.z + depth); j++){
        this.map[i][j] = true;
      }
    }

    for(i = position.x-dis; i < (position.x + width); i++){
      for(j = (position.z + depth); j < (position.z + depth+ dis); j++){
        this.map[i][j] = true;
      }
    }

    for(i = (position.x + width); i < (position.x + width + dis); i++){
      for(j = position.z; j < (position.z + depth + dis); j++){
        this.map[i][j] = true;
      }
    }

    for(i = position.x; i < (position.x + width + dis); i++){
      for(j = position.z - dis; j < (position.z); j++){
        this.map[i][j] = true;
      }
    }


  },

  //Search for all Positions where the current building can be inserted
  getPositionCandidates: function (obj, insertedObjs){
    var x = 0;
    var z = 0;
    var candidates = [];
    var current;
    var position;
    var dis = this.getDistance();
    //Go through the buildings already inserted & check whether there is a possible Position at their edges
    for(var i = 0; i < insertedObjs.length; i++){
      current = insertedObjs[i];
      x = current.position.x + current.width;
      z = current.position.z + current.depth;
      //For each possible Position at the edge of the current inserted building try putting the new building in
      for(var j = 0; j <= current.width; j++){
        position = {x: current.position.x + j, z: z  + dis};
        if(this.checkPosition(obj, position.x, position.z)){
          //If the position fits put it into the candidates array and go on (As all the other possible positions would be worse)
          if(!containsPosition(candidates,position)) {
            candidates.push(position);
            break;
          }
        }
      }
      for(j = 0; j < current.depth; j++){
        position = {x: x + dis, z: current.position.z + j};
        if(this.checkPosition(obj, position.x, position.z)){
          if(!containsPosition(candidates,position)) {
            candidates.push(position);
            break;
          }
        }
      }
    }
    //Check if there are positions at the edge of the map
    for(i = dis; i < this.map.length; i++){
      position = {x: i, z: dis};
      if(this.checkPosition(obj, position.x, position.z)){
        if(!containsPosition(candidates,position)) {
          candidates.push(position);
          break;
        }
      }
      position = {x: dis, z: i};
      if(this.checkPosition(obj, dis, i)){
        if(!containsPosition(candidates,position)) {
          candidates.push(position);
          break;
        }
      }
    }
    position = this.getNextPosition();
    if(!containsPosition(candidates, position)){
      candidates.push(position);
    }
    return candidates;
  },

  //Returns the next free position at the shortest edge of the district
  getNextPosition: function(){
    var position;
    var dis = this.getDistance();
    if(this.map[0].length == 0){
      position = {x:dis, z:dis};
    }
    else {
      if (this.map[0].length <= this.map.length) {
        position = {x: dis, z: this.map[0].length};
      }
      else {
        position = {x: this.map.length, z: dis};
      }
    }
    return position;
  },

  //Check whether the new Building fits in the position
  checkPosition: function(obj, x, z){
    //Go through the rows and check whether there is space for the new building
    for(var i = 0; i < obj.width; i++){
      //If the end of the map is reached one can be sure the building fits in (in the current column / row)
      if(x+i >= this.map.length) break;
      for(var j = 0; j < obj.depth; j++){
        if(z+j >= this.map[i].length) break;
        if(this.map[x+i][z+j]){
          return false;
        }
      }
    }
    return true;
  },

  /**Returns the position-candidate with the smallest distance.
   * If there is more than one opportunity the position that won't extend the district is choosen.
   * candidates is an array of positions
   * obj is the district or building that needs to be placed
  **/
   getBestCandidate: function(candidates, obj){
    var best = candidates[0];
    var currDistance = Math.sqrt(Math.pow(best.x, 2) + Math.pow(best.z, 2));
    var bestExtends = this.extendsThis(best, obj);
    var dis = 0;
    var current;
    var currentExtends;
    for(var i = 1; i < candidates.length; i++){
      current = candidates[i];
      currentExtends = this.extendsThis(current, obj);
      dis = Math.sqrt(Math.pow(candidates[i].x, 2) + Math.pow(candidates[i].z, 2));
      if(bestExtends && currentExtends && currDistance > dis ){
        best = current;
        currDistance = dis;
        bestExtends = currentExtends;
      }
      else if(bestExtends && !currentExtends ){
        best = current;
        currDistance = dis;
        bestExtends = currentExtends;
      }
      else if(!bestExtends && !currentExtends && currDistance > dis){
        best = current;
        currDistance = dis;
        bestExtends = currentExtends;
      }
    }
    return best;
  },

  //Checks whether the object (building or district) will extend the district if set to this position
  extendsThis: function(position, obj){
    var width = position.x + this.getDistance() + obj.width;
    var depth = position.z + this.getDistance() + obj.depth;
    return ( width > this.map.length || depth > this.map[0].length);
  },

  //MOves the district for the guven values
  move: function(x, z){
    //adjust attributes
    this.position.x += x;
    this.position.z += z;
    //Move geometry
    changePositionPlane(this.geometry, x, z);
    //Move the containedObjects
    for(var i = 0; i < this.containedObjs.length; i++){
      this.containedObjs[i].move(x, z);
    }
  }
};

function Building (width, depth, height, district, label, color){

  this.width = width;
  this.depth = depth;
  this.height = height;
  this.label = label;
  this.position = {x: 0, y: 0, z: 0};
  if(this.height instanceof Array) {
    this.geometry = [];
    this.color = [];
    for(var i = 0; i < color.length; i++){
      this.color.push(color[i].replace("#", "0x"));
    }
  }
  else{
    this.geometry;
    this.color = color.replace("#", "0x");
    this.color = this.color.replace("\r", "");
  }
  district.addBuilding(this);
}

Building.prototype = {
  //Renders the building
  render: function() {
    if(!(this.height == 0 || this.width == 0 || this.depth == 0)) {
      //If there is more than one floor, the floor are rendered successively
      if (this.height instanceof Array) {
        var heights = this.smoothBigHeights(); //Heights are smoothed as needed
        var y = 0;
        for (var i = 0; i < heights.length; i++) {
          if (i != 0) {
            y += heights[i - 1];
          }
          if (heights[i] > 0) {
            if(this.label[i].text == "Smoothed") this.geometry.push(createBuilding(this.width, this.depth, heights[i], {x: 0, y: y, z: 0}, this.label[i], this.color[i], true));
            else this.geometry.push(createBuilding(this.width, this.depth, heights[i], {x: 0, y: y, z: 0}, this.label[i], this.color[i], false));
          }
        }
      }
      else {
        this.geometry = createBuilding(this.width, this.depth, this.height, this.position, this.label, this.color, false);
      }
    }
  },

  //Heights that are bigger than the ratio of hieghts are smoothed to the ratio & a marking is added
  smoothBigHeights: function(){
    if(this.getHeight() > 50){
      var ratio = this.getHeight() / this.height.length;
      var smoothedHeights = [];
      var smoothed = 0;
      for(var i = 0; i < this.height.length; i++){
        if(this.height[i] > ratio){
          smoothedHeights.push(ratio);
          smoothedHeights.push(0.4);
          this.color.splice((i+smoothed+1), 0, "0xF3F3F3");
          this.label.splice((i+smoothed+1), 0, {"text": "Smoothed"});
          smoothed++;
        }
        else{
          smoothedHeights.push(this.height[i]);
        }
      }
      return smoothedHeights;
    }
    else{
      return this.height;
    }
  },

  //Returns the height of the building
  getHeight: function(){
    if(this.height instanceof Array){
      var height = 0;
      for(var i = 0; i < this.height.length; i++){
        height += this.height[i];
      }
      return height;
    }
    else return this.height;
  },

  //Moves the building for the given values
  move: function(x, z) {
    if (!(this.height == 0 || this.width == 0 || this.depth == 0)) {
      this.position.x += x;
      this.position.z += z;
      if (this.geometry instanceof Array) {
        for (var i = 0; i < this.geometry.length; i++) {
          changePositionBox(this.geometry[i], x, z);
        }
      }
      else {
        changePositionBox(this.geometry, x, z);
      }
    }
  }
};