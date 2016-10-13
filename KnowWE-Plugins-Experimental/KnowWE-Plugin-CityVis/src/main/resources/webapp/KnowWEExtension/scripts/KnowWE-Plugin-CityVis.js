/**
 * Created by Lea on 04.08.2016.
 */

/**
 * The KNOWWE global namespace object. If KNOWWE is already defined, the
 * existing KNOWWE object will not be overwritten so that defined namespaces are
 * preserved.
 */
if (typeof KNOWWE == "undefined" || !KNOWWE) {
    var KNOWWE = {};
}

/**
 * The KNOWWE.core global namespace object. If KNOWWE.core is already defined,
 * the existing KNOWWE.core object will not be overwritten so that defined
 * namespaces are preserved.
 */
if (typeof KNOWWE.plugin == "undefined" || !KNOWWE.plugin) {
    KNOWWE.plugin = {};
}

KNOWWE.plugin.cityVis = function() {

    return {
        render: function (file, sectionID) {
            KNOWWE.plugin.cityVis.import.parseJSON(file, sectionID);
        }
    }
}();

KNOWWE.plugin.cityVis.import = function(){
    function readTextFile(file, callback) {
        var rawFile = new XMLHttpRequest();
        rawFile.overrideMimeType("application/json");
        rawFile.open("GET", file, true);
        rawFile.onreadystatechange = function() {
            if (rawFile.readyState === 4 && rawFile.status == "200") {
                callback(rawFile.responseText);
            }
        };
        rawFile.send(null);
    }

    function createCityFromJSON (json, sectionID){
        KNOWWE.plugin.cityVis.renderer.init(sectionID);
        var color;
        if(json.color){
            color = json.color;
        }
        else{
            color = "#696969";
        }
        var city = KNOWWE.plugin.cityVis.renderer.newCity(4, 8, json.Label, color);
        //Go through the City's districts
        getDistrictsFromJSON(json, city);
        city.render(json.sorted);

        KNOWWE.plugin.cityVis.renderer.render();
    }

    function getBuildingsFromJSON(dataDistrict, district){
        var dataBuilding;
        var building;
        dataDistrict.Buildings.sort(function(a, b){ return a.sortKey - b.sortKey});
        for (var j = 0; j < dataDistrict.Buildings.length; j++) {
            dataBuilding = dataDistrict.Buildings[j];
            var color;
            if(dataBuilding.color){
                color = dataBuilding.color;
            }
            else{
                color = "#008080";
            }
            building = KNOWWE.plugin.cityVis.renderer.newBuilding(dataBuilding.width, dataBuilding.depth, dataBuilding.height, district, dataBuilding.Label, color);
        }
    }

    function getDistrictsFromJSON (data, containingObj){
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
                    color = "#a9a9a9";
                }
                dis = KNOWWE.plugin.cityVis.renderer.newDistrict(containingObj, dataDistrict.Label, color, true);
                getDistrictsFromJSON(dataDistrict, dis);
            }
            else{
                //Current districts doesn't contain other districts
                if(dataDistrict.color){
                    color = dataDistrict.color;
                }
                else{
                    color = "#C0C0C0";
                }
                dis = KNOWWE.plugin.cityVis.renderer.newDistrict(containingObj, dataDistrict.Label, color, false);
                getBuildingsFromJSON(dataDistrict, dis);
            }
        }
    }

    return{
        parseJSON: function(file, sectionID){
        readTextFile(file, function(text){
            var json = JSON.parse(text);
            createCityFromJSON(json, sectionID);
        });
    }
    }
}();

KNOWWE.plugin.cityVis.renderer = function(){

    var scene, renderer, camera, directionalLight, controls, highlightedObj, container, tip, containerWidth, containerHeight, containerLeft, containerTop, rect;

    function onDocumentResize(){
        rect = container.getBoundingClientRect();

        containerHeight = rect.bottom - rect.top;
        containerWidth = rect.right - rect.left;

        camera.aspect = containerWidth / containerHeight;
        camera.updateProjectionMatrix();

        renderer.setSize( containerWidth, containerHeight);
    }

    function checkResize(){
        var newRect = container.getBoundingClientRect();
        var newWidth = newRect.right - newRect.left;
        var newHeight = newRect.bottom - newRect.top;
        if(newWidth != containerWidth || newHeight != containerHeight){
            containerWidth = newWidth;
            containerHeight = newHeight;
            containerLeft = newRect.left;
            containerTop = newRect.top + window.scrollY;

            camera.aspect = containerWidth / containerHeight;
            camera.updateProjectionMatrix();

            renderer.setSize( containerWidth, containerHeight);
            return true;
        }
        else{
            return false;
        }
    }

    function onDocumentMouseDown(event) {
        if(!checkResize()){
            var raycaster = new THREE.Raycaster();
            //Transfer from mouse-coordinates to window-coordinates
            var mouseX = event.clientX - containerLeft;
            var mouseY = event.clientY - containerTop  + window.scrollY;
            var mouse3D = new THREE.Vector3((mouseX / containerWidth)*2-1, - (mouseY/containerHeight)*2+1, 0.5);
            raycaster.setFromCamera(mouse3D, camera);
            var intersects = raycaster.intersectObjects(scene.children);
            if (intersects.length > 0) {
                var obj = intersects[0].object;
                if(event.ctrlKey){
                    updateOrbitControls(obj.position.x, obj.position.z);
                    camera.position.z = obj.position.z + (obj.geometry.parameters.height / 2);
                    camera.position.x = obj.position.x + (obj.geometry.parameters.width / 2);
                    camera.position.y = 15;
                }
                removeHighlight();
                tip.style.position = "fixed";
                tip.style.left = containerWidth + rect.left + 30 + 'px';
                tip.style.top = rect.top +'px';
                getTooltip(obj);
                highlight(obj);
            }
            else {
                removeHighlight();
                jq$(tip).tooltipster('hide');
            }
        }
    }

    function highlight(obj){
            highlightedObj = obj;
            obj.material.color.r += 0.5;
            obj.material.color.g += 0.5;
            obj.material.color.b += 0.5;
    }

    function removeHighlight(){
        if(highlightedObj != undefined) {
            highlightedObj.material.color.r -= 0.5;
            highlightedObj.material.color.g -= 0.5;
            highlightedObj.material.color.b -= 0.5;
            highlightedObj = undefined;
        }
    }

    function getTooltip (obj) {
        jq$(tip).tooltipster({contentAsHTML: true});
        jq$(tip).tooltipster('content', obj.name);
        jq$(tip).tooltipster('show');
    }

    function light_update() {
        directionalLight.position.copy(camera.position);
    }

    function updateOrbitControls(positionX, positionZ) {
        controls.target.x = positionX;
        controls.target.z = positionZ;

    }

    function createCity(label, color) {
        var city = addPlane(1, 1, 0, 0);
        city.material.color.setHex(color);
        city.name = label;
        return city;
    }

    function createDistrict(positionX, positionZ, label, color, containsDistricts) {
        var district = addPlane(1, 1, positionX, positionZ);
        district.material.color.setHex(color);
        if (containsDistricts) {
            district.position.y = 0.02;
        }
        else {
            district.position.y = 0.04;
        }

        district.name = label;
        return district;
    }

    function createBuilding(width, depth, height, positionX, positionZ, positionY, label, color) {
        var building = addBox(width, depth, height, positionX, positionZ, positionY);
        building.position.y += 0.06;
        building.material.color.setHex(color);
        if(color == "0xFE2E2E"){
            building.material.opacity = 0.5;
            building.material.transparent = true;
        }
        building.name = label;
        return building;
    }

    function changeSizeCity(object, width, depth) {
        var difWidth, difDepth;
        difWidth = 0;
        difDepth = 0;
        if (width > 0) {
            difWidth = width - object.scale.x;
            object.scale.x = width;
            object.geometry.parameters.width = width;
        }
        if (depth > 0) {
            //At this point scale.y has to be used, because the plane hat been rotated
            difDepth = depth - object.scale.y;
            object.scale.y = depth;
            object.geometry.parameters.height = depth;
        }

        changePositionPlane(object, (difWidth / 2), (difDepth / 2));
    }

    function changeSize(object, width, depth) {
        var difWidth, difDepth;
        difWidth = 0;
        difDepth = 0;
        if (width > 0) {
            difWidth = width - object.scale.x;
            object.scale.x = width;
            object.geometry.parameters.width = width;
        }
        if (depth > 0) {
            //At this point scale.y has to be used, because the plane hat been rotated
            difDepth = depth - object.scale.y;
            object.scale.y = depth;
            object.geometry.parameters.height = depth;
        }

        changePositionPlane(object, (difWidth / 2), (difDepth / 2));
    }

    function changePositionPlane(object, x, z) {
        object.position.x += x;
        object.position.z += z;
    }


        function changePositionBox(object, x, z) {
            object.position.x += x;
            object.position.z += z;
        }

        function addPlane(width, depth, positionX, positionZ) {
            //Geometry + Material = plane

            var planeGeometry = new THREE.PlaneGeometry(width, depth);
            var planeMaterial = new THREE.MeshLambertMaterial();
            var plane = new THREE.Mesh(planeGeometry, planeMaterial);

            //Position
            plane.position.x = positionX + (0.5 * width);
            plane.position.z = positionZ + (0.5 * depth);

            //Rotation
            plane.rotateX(-Math.PI / 2);

            // Make 2nd side visible
            plane.material.side = THREE.DoubleSide;
            //Make changeable
            plane.geometry._dirtyPosition = true;
            plane.geometry._dirtyVertices = true;
            plane.geometry.dynamic = true;

            //add
            scene.add(plane);
            return plane;
        }

        function addBox(width, depth, height, positionX, positionZ, positionY) {
            //Geometry + Material = Box
            var boxGeometry = new THREE.BoxGeometry(width, height, depth);
            var boxMaterial = new THREE.MeshLambertMaterial();
            var box = new THREE.Mesh(boxGeometry, boxMaterial);

            //Position
            box.position.x = positionX + (width * 0.5);
            box.position.z = positionZ + (depth * 0.5);
            box.position.y = positionY + (height * 0.5);
            //Make changeable
            box.geometry._dirtyPosition = true;
            box.geometry._dirtyVertices = true;
            box.geometry.dynamic = true;

            //Add
            scene.add(box);
            return box;
        }

        function containsPosition(arr, pos) {
            for (var i = 0; i < arr.length; i++) {
                if (arr[i].x == pos.x && arr[i].z == pos.z) {
                    return true;
                }
            }
            return false;
        }

        function City(distanceBuildings, distanceDistricts, label, color) {
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

            getDistanceDistricts: function () {
                return this.distanceDistricts;
            },

            getDistanceBuildings: function () {
                return this.distanceBuildings;
            },


            //Adjust the Size of the city for the given values
            adjustSize: function (width, depth) {
                if (width > 0) {
                    this.width += width;
                    this.map.length = this.width;
                }
                if (depth > 0) {
                    this.depth += depth;
                }
                for (var i = 0; i < this.map.length; i++) {
                    if (this.map[i] == undefined) {
                        this.map[i] = [];
                    }
                    this.map[i].length = this.depth;
                }

                changeSizeCity(this.geometry, this.width, this.depth);
                updateOrbitControls(this.width / 2, this.depth / 2);
            },

            render: function (sorted) {
                this.geometry = createCity(this.label, this.color);
                var insertedObjs = [];
                var position;
                var current;
                var candidates;
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
                this.adjustSize(0.5 * this.distanceDistricts, 0.5 * this.distanceDistricts);
            },

            markDistanceInMap: function (position, width, depth) {
                var dis = this.distanceDistricts;
                var difX = (position.x + dis + width) - this.width;
                var difZ = (position.z + dis + depth) - this.depth;
                if (difX > 0 || difZ > 0) {
                    this.adjustSize(difX, difZ);
                }

                for (var i = position.x - dis; i < position.x; i++) {
                    for (var j = position.z - dis; j < (position.z + depth); j++) {
                        this.map[i][j] = true;
                    }
                }

                for (i = position.x - dis; i < (position.x + width); i++) {
                    for (j = (position.z + depth); j < (position.z + depth + dis); j++) {
                        this.map[i][j] = true;
                    }
                }

                for (i = (position.x + width); i < (position.x + width + dis); i++) {
                    for (j = position.z; j < (position.z + depth + dis); j++) {
                        this.map[i][j] = true;
                    }
                }

                for (i = position.x; i < (position.x + width + dis); i++) {
                    for (j = position.z - dis; j < (position.z); j++) {
                        this.map[i][j] = true;
                    }
                }
            },

            markMap: function (position, width, depth) {
                //Set the district & the map to the minimal needed width & depth
                this.adjustSize((position.x + width - this.width), (position.z + depth - this.depth));
                //Mark the district
                for (var i = position.x; i < position.x + width; i++) {
                    for (var j = position.z; j < position.z + depth; j++) {
                        this.map[i][j] = true;
                    }
                }
                //Mark the distance
                this.markDistanceInMap(position, width, depth);
            },

            //Search for all Positions where the current building can be inserted
            getPositionCandidates: function (obj, insertedObjs) {
                var x = 0;
                var z = 0;
                var candidates = [];
                var current;
                var position;
                //Go through the buildings already inserted & check whether there is a possible Position at their edges
                for (var i = 0; i < insertedObjs.length; i++) {
                    current = insertedObjs[i];
                    x = current.positionX + current.width;
                    z = current.positionZ + current.depth;
                    //For each possible Position at the edge of the current inserted building try putting the new building in
                    for (var j = 0; j <= current.width; j++) {
                        position = {x: current.positionX + j, z: z + this.distanceDistricts};
                        if (this.checkPosition(obj, position.x, position.z)) {
                            //If the position fits put it into the candidates array and go on (As all the other possible positions would be worse)
                            if (!containsPosition(candidates, position)) {
                                candidates.push(position);
                                break;
                            }
                        }
                    }
                    for (j = 0; j < current.depth; j++) {
                        position = {x: x + this.distanceDistricts, z: current.positionZ + j};
                        if (this.checkPosition(obj, position.x, position.z)) {
                            if (!containsPosition(candidates, position)) {
                                candidates.push(position);
                                break;
                            }
                        }
                    }
                }
                //Check if there are positions at the edge of the map
                for (i = this.distanceDistricts; i < this.map.length; i++) {
                    position = {x: i, z: this.distanceDistricts};
                    if (this.checkPosition(obj, position.x, position.z)) {
                        if (!containsPosition(candidates, position)) {
                            candidates.push(position);
                            break;
                        }
                    }
                    position = {x: this.distanceDistricts, z: i};
                    if (this.checkPosition(obj, this.distanceDistricts, i)) {
                        if (!containsPosition(candidates, position)) {
                            candidates.push(position);
                            break;
                        }
                    }
                }
                position = this.getNextPosition();
                if (!containsPosition(candidates, position)) {
                    candidates.push(position);
                }
                return candidates;
            },

            getNextPosition: function () {
                var position;
                if (this.map[0].length == 0) {
                    position = {x: this.distanceDistricts, z: this.distanceDistricts};
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
            checkPosition: function (obj, x, z) {
                //Go through the rows and check whether there is space for the new building
                for (var i = 0; i < obj.width + this.distanceDistricts; i++) {
                    //If the end of the map is reached one can be sure the building fits in (in the current column / row)
                    if (x + i >= this.map.length) break;
                    for (var j = 0; j < obj.depth + this.distanceDistricts; j++) {
                        if (z + j >= this.map[i].length) break;
                        if (this.map[x + i][z + j] == true) {
                            return false;
                        }
                    }
                }
                return true;
            },

            getBestCandidate: function (candidates, obj) {
                var best = candidates[0];
                var currDistance = Math.sqrt(Math.pow(best.x, 2) + Math.pow(best.z, 2));
                var bestExtends = this.extendsThis(best, obj);
                var dis = 0;
                var current;
                var currentExtends;
                for (var i = 1; i < candidates.length; i++) {
                    current = candidates[i];
                    currentExtends = this.extendsThis(current, obj);
                    dis = Math.sqrt(Math.pow(candidates[i].x, 2) + Math.pow(candidates[i].z, 2));
                    if (bestExtends && currentExtends && currDistance > dis) {
                        best = current;
                        currDistance = dis;
                        bestExtends = currentExtends;
                    }
                    else if (bestExtends && !currentExtends) {
                        best = current;
                        currDistance = dis;
                        bestExtends = currentExtends;
                    }
                    else if (!bestExtends && !currentExtends && currDistance > dis) {
                        best = current;
                        currDistance = dis;
                        bestExtends = currentExtends;
                    }
                }
                return best;
            },

            extendsThis: function (position, obj) {
                var width = position.x + this.distanceDistricts + obj.width;
                var depth = position.z + this.distanceDistricts + obj.depth;
                return ( width > this.map.length || depth > this.map[0].length);
            }
        };

        function District(containingObj, label, color, containsDistricts) {
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
            if (containingObj != null) {
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
                if (width > 0) {
                    this.width += width;
                    this.map.length = this.width;
                }
                if (depth >= 0) {
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

            getDistance: function () {
                if (this.containsDistricts) {
                    return this.getDistanceDistricts();
                }
                else {
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
                if (sorted) {
                    this.containedObjs.sort(function (a, b) {
                        return (b.getHeight() - a.getHeight())
                    });
                }
                for (var i = 0; i < this.containedObjs.length; i++) {
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
                this.adjustSize(distance * 0.5, distance * 0.5);
            },

            markMap: function (position, width, depth) {
                //Set the district & the map to the minimal needed width & depth
                this.adjustSize((position.x + width - this.width), (position.z + depth - this.depth));
                for (var i = position.x; i < position.x + width; i++) {
                    for (var j = position.z; j < position.z + depth; j++) {
                        this.map[i][j] = true;
                    }
                }
                this.markDistanceInMap(position, width, depth);
            },
            markDistanceInMap: function (position, width, depth) {
                var dis = this.getDistance();
                var difX = (position.x + dis + width) - this.width;
                var difZ = (position.z + dis + depth) - this.depth;
                if (difX > 0 || difZ > 0) {
                    this.adjustSize(difX, difZ);
                }

                for (var i = position.x - dis; i < position.x; i++) {
                    for (var j = position.z - dis; j < (position.z + depth); j++) {
                        this.map[i][j] = true;
                    }
                }

                for (i = position.x - dis; i < (position.x + width); i++) {
                    for (j = (position.z + depth); j < (position.z + depth + dis); j++) {
                        this.map[i][j] = true;
                    }
                }

                for (i = (position.x + width); i < (position.x + width + dis); i++) {
                    for (j = position.z; j < (position.z + depth + dis); j++) {
                        this.map[i][j] = true;
                    }
                }

                for (i = position.x; i < (position.x + width + dis); i++) {
                    for (j = position.z - dis; j < (position.z); j++) {
                        this.map[i][j] = true;
                    }
                }


            },

            //Search for all Positions where the current building can be inserted
            getPositionCandidates: function (obj, insertedObjs) {
                var x = 0;
                var z = 0;
                var candidates = [];
                var current;
                var position;
                var dis = this.getDistance();
                //Go through the buildings already inserted & check whether there is a possible Position at their edges
                for (var i = 0; i < insertedObjs.length; i++) {
                    current = insertedObjs[i];
                    x = current.positionX + current.width;
                    z = current.positionZ + current.depth;
                    //For each possible Position at the edge of the current inserted building try putting the new building in
                    for (var j = 0; j <= current.width; j++) {
                        position = {x: current.positionX + j, z: z + dis};
                        if (this.checkPosition(obj, position.x, position.z)) {
                            //If the position fits put it into the candidates array and go on (As all the other possible positions would be worse)
                            if (!containsPosition(candidates, position)) {
                                candidates.push(position);
                                break;
                            }
                        }
                    }
                    for (j = 0; j < current.depth; j++) {
                        position = {x: x + dis, z: current.positionZ + j};
                        if (this.checkPosition(obj, position.x, position.z)) {
                            if (!containsPosition(candidates, position)) {
                                candidates.push(position);
                                break;
                            }
                        }
                    }
                }
                //Check if there are positions at the edge of the map
                for (i = dis; i < this.map.length; i++) {
                    position = {x: i, z: dis};
                    if (this.checkPosition(obj, position.x, position.z)) {
                        if (!containsPosition(candidates, position)) {
                            candidates.push(position);
                            break;
                        }
                    }
                    position = {x: dis, z: i};
                    if (this.checkPosition(obj, dis, i)) {
                        if (!containsPosition(candidates, position)) {
                            candidates.push(position);
                            break;
                        }
                    }
                }
                position = this.getNextPosition();
                if (!containsPosition(candidates, position)) {
                    candidates.push(position);
                }
                return candidates;
            },

            getNextPosition: function () {
                var position;
                var dis = this.getDistance();
                if (this.map[0].length == 0) {
                    position = {x: dis, z: dis};
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
            checkPosition: function (obj, x, z) {
                //Go through the rows and check whether there is space for the new building
                for (var i = 0; i < obj.width; i++) {
                    //If the end of the map is reached one can be sure the building fits in (in the current column / row)
                    if (x + i >= this.map.length) break;
                    for (var j = 0; j < obj.depth; j++) {
                        if (z + j >= this.map[i].length) break;
                        if (this.map[x + i][z + j]) {
                            return false;
                        }
                    }
                }
                return true;
            },

            getBestCandidate: function (candidates, obj) {
                var best = candidates[0];
                var currDistance = Math.sqrt(Math.pow(best.x, 2) + Math.pow(best.z, 2));
                var bestExtends = this.extendsThis(best, obj);
                var dis = 0;
                var current;
                var currentExtends;
                for (var i = 1; i < candidates.length; i++) {
                    current = candidates[i];
                    currentExtends = this.extendsThis(current, obj);
                    dis = Math.sqrt(Math.pow(candidates[i].x, 2) + Math.pow(candidates[i].z, 2));
                    if (bestExtends && currentExtends && currDistance > dis) {
                        best = current;
                        currDistance = dis;
                        bestExtends = currentExtends;
                    }
                    else if (bestExtends && !currentExtends) {
                        best = current;
                        currDistance = dis;
                        bestExtends = currentExtends;
                    }
                    else if (!bestExtends && !currentExtends && currDistance > dis) {
                        best = current;
                        currDistance = dis;
                        bestExtends = currentExtends;
                    }
                }
                return best;
            },

            extendsThis: function (position, obj) {
                var width = position.x + this.getDistance() + obj.width;
                var depth = position.z + this.getDistance() + obj.depth;
                return ( width > this.map.length || depth > this.map[0].length);
            },

            move: function (x, z) {
                //adjust attributes
                this.positionX += x;
                this.positionZ += z;
                //Move geometry
                changePositionPlane(this.geometry, x, z);
                //Move the containedObjects
                for (var i = 0; i < this.containedObjs.length; i++) {
                    this.containedObjs[i].move(x, z);
                }
            }
        };

        function Building(width, depth, height, district, label, color) {

            this.width = width;
            this.depth = depth;
            this.height = height;
            this.label = label;
            this.positionX = 0;
            this.positionZ = 0;
            if (this.height instanceof Array) {
                this.geometry = [];
                this.color = [];
                for (var i = 0; i < color.length; i++) {
                    this.color.push(color[i].replace("#", "0x"));
                }
            }
            else {
                this.geometry;
                this.color = color.replace("#", "0x");
                this.color = this.color.replace("\r", "");
            }
            this.smoothWidth();
            this.smoothDepth();
            district.addBuilding(this);
        }

        Building.prototype = {
            //Renders the building
            render: function () {
                if (this.height instanceof Array) {
                    var heights = this.smoothBigHeights();
                    // var heights = this.height;
                    //  var heights = this.smoothHeightsRatio();
                    var y = 0;
                    for (var i = 0; i < heights.length; i++) {
                        if (i != 0) {
                            y += heights[i - 1];
                        }
                        if (heights[i] > 0) {
                            this.geometry.push(createBuilding(this.width, this.depth, heights[i], 0, 0, y, this.label[i], this.color[i]));
                        }
                    }
                }
                else {
                    this.geometry = createBuilding(this.width, this.depth, this.height, 0, 0, 0, this.label, this.color);
                }


            },

            smoothWidth: function () {
              if(this.width > 50){
                  this.width = 50;
                  this.label += " (width smoothed) ";
              }
            },

            smoothDepth: function () {
              if(this.depth > 50){
                  this.depth = 50;
                  this.label += " (depth smoothed)";
              }
            },

            smoothBigHeights: function () {
                if (this.getHeight() > 100) {
                    var ratio = this.getHeight() / this.height.length;
                    if(ratio > 100){
                        ratio = 100;
                    }
                    var smoothedHeights = [];
                    var smoothed = 0;
                    var labels = [];
                    for (var i = 0; i < this.height.length; i++) {
                        if (this.height[i] > ratio) {
                            smoothedHeights.push(ratio);
                            smoothedHeights.push(0.4);
                            this.color.splice(i + smoothed + 1, 0, "0xF3F3F3");
                            if(this.label instanceof Array) {
                                this.label.splice(i + 1 + smoothed, 0, "Smoothed");
                            }
                            else{
                                for(var j = 0; j < this.height.length; j++) {
                                    labels.push(this.label)
                                }
                                labels.splice(i + 1 + smoothed, 0, "Smoothed");
                                this.label = labels;
                            }
                                smoothed++;

                        }
                        else {
                            smoothedHeights.push(this.height[i]);
                        }
                    }
                    return smoothedHeights;
                }
                else {
                    return this.height;
                }
            },

            //Returns the height of the building
            getHeight: function () {
                if (this.height instanceof Array) {
                    var height = 0;
                    for (var i = 0; i < this.height.length; i++) {
                        height += this.height[i];
                    }
                    return height;
                }
                else return this.height;
            },

            //Moves the building for the given values
            move: function (x, z) {
                this.positionX += x;
                this.positionZ += z;
                if (this.geometry instanceof Array) {
                    for (var i = 0; i < this.geometry.length; i++) {
                        changePositionBox(this.geometry[i], x, z);
                    }
                }
                else {
                    changePositionBox(this.geometry, x, z);
                }
            }
        };

        return {
            init: function (sectionID) {

                scene = new THREE.Scene();
                container = document.createElement("DIV");
                document.getElementById(sectionID).appendChild(container);
                container.style.height = "600px";
                rect = container.getBoundingClientRect();

                container.style.padding = "0px 0px 0px 0px";
                containerLeft = rect.left;
                containerTop = rect.top;
                containerWidth = rect.right - rect.left;
                containerHeight = rect.bottom - rect.top;

                camera = new THREE.PerspectiveCamera(75, containerWidth / containerHeight, 10, 10000);
                camera.position.z = 50;
                camera.aspect = containerWidth / containerHeight;
                camera.updateProjectionMatrix();

                renderer = new THREE.WebGLRenderer({alpha: true});
                renderer.setSize(containerWidth, containerHeight);
                renderer.setClearColor(0xffffff, 0);

                container.appendChild(renderer.domElement);

                directionalLight = new THREE.HemisphereLight(0xFFFFFF, 1);
                directionalLight.position = camera.position;
                scene.add(directionalLight);

                controls = new THREE.OrbitControls(camera, renderer.domElement);
                controls.enableDamping = true;
                controls.dampingFactor = 0.25;
                controls.enableZoom = true;
                controls.addEventListener('change', light_update);
                controls.minDistance = 20;
                controls.maxDistance = 1000;
                controls.maxPolarAngle = Math.PI/2 - 0.1;
                controls.rotateSpeed = 0.17;
                controls.zoomSpeed = 1.4;

                tip = document.createElement("DIV");
                jq$("body").append(tip);

                document.addEventListener('mousedown', onDocumentMouseDown, false);
                document.addEventListener('resize', onDocumentResize, false);
            },
            render: function () {
                requestAnimationFrame(KNOWWE.plugin.cityVis.renderer.render);
                controls.update();
                renderer.render(scene, camera);
            },
            newCity: function (distanceBuildings, distanceDistricts, label, color) {
                return new City(distanceBuildings, distanceDistricts, label, color);
            },
            newBuilding: function (width, depth, height, district, label, color) {
                return new Building(width, depth, height, district, label, color);
            },
            newDistrict: function (containingObj, label, color, containsDistricts) {
                return new District(containingObj, label, color, containsDistricts);
            }

        }
}();

