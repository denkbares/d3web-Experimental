/**
 * Created by Lea on 12.05.2016.
 */

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
    this.containedObjs
        .push(district);
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

  render: function(sorted){
    this.geometry = createCity(this.label, this.color);
    var insertedObjs = [];
    var position;
    var current;
    var candidates;
    if(sorted) {
      this.containedObjs.sort(function (a, b) {
        return (b.getHeight() - a.getHeight())
      });
    }
    //Go through the districts and insert them in the given order
    for(var i = 0; i < this.containedObjs.length; i++){
      current = this.containedObjs[i];
      current.render(sorted);
      //Search for possible positions
      candidates = this.getPositionCandidates(this.containedObjs[i], insertedObjs);
      //Choose the best position (= smallest distance to (0|0)
      position = this.getBestCandidate(candidates, current);
      current.move(position.x , position.z );
      this.markMap(position, current.width, current.depth);
      insertedObjs.push(current);
    }
    this.adjustSize(0.5 * this.distanceDistricts, 0.5 * this.distanceDistricts);
  },

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
      x = current.positionX + current.width;
      z = current.positionZ + current.depth;
      //For each possible Position at the edge of the current inserted building try putting the new building in
      for(var j = 0; j <= current.width; j++){
        position = {x: current.positionX + j, z: z  + this.distanceDistricts};
        if(this.checkPosition(obj, position.x, position.z)){
          //If the position fits put it into the candidates array and go on (As all the other possible positions would be worse)
          if(!containsPosition(candidates,position)) {
            candidates.push(position);
            break;
          }
        }
      }
      for(j = 0; j < current.depth; j++){
        position = {x: x + this.distanceDistricts, z: current.positionZ + j};
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
        if(z+j >= this.map[i].length) break;
        if(this.map[x+i][z+j] == true){
          return false;
        }
      }
    }
    return true;
  },

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
  this.positionX = 0;
  this.positionZ = 0;
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

  render: function (sorted) {
    this.geometry = createDistrict(this.positionX, this.positionZ, this.label, this.color, this.containsDistricts);
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
      current.move(position.x + this.positionX, position.z + this.positionZ);
      this.markMap(position, current.width, current.depth);
      insertedObjs.push(current);
    }
    var distance = this.getDistance();
    this.adjustSize(distance*0.5, distance*0.5);
  },

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
      x = current.positionX + current.width;
      z = current.positionZ + current.depth;
      //For each possible Position at the edge of the current inserted building try putting the new building in
      for(var j = 0; j <= current.width; j++){
        position = {x: current.positionX + j, z: z  + dis};
        if(this.checkPosition(obj, position.x, position.z)){
          //If the position fits put it into the candidates array and go on (As all the other possible positions would be worse)
          if(!containsPosition(candidates,position)) {
            candidates.push(position);
            break;
          }
        }
      }
      for(j = 0; j < current.depth; j++){
        position = {x: x + dis, z: current.positionZ + j};
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

  extendsThis: function(position, obj){
    var width = position.x + this.getDistance() + obj.width;
    var depth = position.z + this.getDistance() + obj.depth;
    return ( width > this.map.length || depth > this.map[0].length);
  },

  move: function(x, z){
    //adjust attributes
    this.positionX += x;
    this.positionZ += z;
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
  this.positionX = 0;
  this.positionZ = 0;
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
    if(this.height instanceof Array) {
      var heights = this.smoothBigHeights();
      // var heights = this.height;
      //  var heights = this.smoothHeightsRatio();
      var y = 0;
      for (var i = 0; i < heights.length; i++) {
        if (i != 0) {
          y += heights[i - 1];
        }
        if(heights[i] > 0) {
          this.geometry.push(createBuilding(this.width, this.depth, heights[i], 0, 0, y, this.label[i], this.color[i]));
        }
      }
    }
    else{
      this.geometry = createBuilding(this.width, this.depth, this.height, 0, 0, 0, this.label, this.color);
    }
  },

  smoothBigHeights: function(){
    if(this.getHeight() > 50){
      var ratio = this.getHeight() / this.height.length;
      var smoothedHeights = [];
      var smoothed = 0;
      for(var i = 0; i < this.height.length; i++){
        if(this.height[i] > ratio){
          smoothedHeights.push(ratio);
          smoothedHeights.push(0.4);
          this.color.splice(i + smoothed + 1, 0, "0xF3F3F3");
          this.label.splice(i+1 + smoothed, 0, "Smoothed");
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
  move: function(x, z){
    this.positionX += x;
    this.positionZ += z;
    if(this.geometry instanceof Array){
      for(var i = 0; i < this.geometry.length; i++){
        changePositionBox(this.geometry[i], x, z);
      }
    }
    else {
      changePositionBox(this.geometry, x, z);
    }
  }
};