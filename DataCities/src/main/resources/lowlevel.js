/**
 * Created by Lea on 12.05.2016.
 */

var scene, renderer, camera, directionalLight, controls, highlightedObj, textLabels, fontLoader, cityPosiion;
function init() {
    scene = new THREE.Scene();
 
  camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 10, 10000);
  camera.position.z = 50;
  window.
  renderer = new THREE.WebGLRenderer({alpha: true});
  renderer.setSize(window.innerWidth, window.innerHeight);
  renderer.setClearColor(0xffffff, 0);
  document.body.appendChild(renderer.domElement);

  directionalLight = new THREE.HemisphereLight(0xFFFFFF, 1);
  directionalLight.position = camera.position;
  scene.add(directionalLight);

  controls = new THREE.OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true;
  controls.dampingFactor = 0.25;
  controls.enableZoom = true;
  controls.addEventListener('change', light_update);
  controls.minDistance = 20;
  controls.maxDistance = 10000;
  controls.maxPolarAngle = Math.PI/2 - 0.1;
  controls.rotateSpeed = 0.17;
  controls.zoomSpeed = 1.4;

textLabels = [];

  document.addEventListener('mousedown', onDocumentMouseDown, false);
  document.addEventListener('mouseup', onDocumentMouseUp, false);
  window.addEventListener( 'resize', onWindowResize, false );
}

function render() {
  requestAnimationFrame(render);
  controls.update();
  labelUpdate();
  renderer.render(scene, camera);
}

function light_update() {
  directionalLight.position = cityPosiion;
  directionalLight.position.y = 100;
}

function labelUpdate () {
  for(var i = 0; i < textLabels.length; i++) {
    textLabels[i].quaternion.x = camera.quaternion.x;
  }
}

function updateOrbitControls(positionX, positionZ) {
  controls.target.x = positionX;
  controls.target.z = positionZ;

}

function onWindowResize(){

  camera.aspect = window.innerWidth / window.innerHeight;
  camera.updateProjectionMatrix();

  renderer.setSize( window.innerWidth, window.innerHeight );

}

function onDocumentMouseDown(event) {
  var raycaster = new THREE.Raycaster();
  //Transfer from mouse-coordinates to window-coordinates
  var mouseX = event.clientX + window.scrollX - 3;
  var mouseY = event.clientY + window.scrollY - 5;
  var mouse3D = new THREE.Vector3(( (mouseX / window.innerWidth ) * 2 - 1), ( -( mouseY / window.innerHeight) * 2 + 1 ), 0.5);

  raycaster.setFromCamera(mouse3D, camera);
  var intersects = raycaster.intersectObjects(scene.children);

  if (intersects.length > 0) {
    var obj = intersects[0].object;
    if (event.ctrlKey) {
      getDistrict(obj);
    }
    else {
      if (obj.name.isURL) {
        openURL(obj.name.text);
      }
      else if (obj.name) {
        getTooltip(obj);
        highlight(obj);
      }
      else {
        removeHighlight();
        UnTip();
      }
    }
  }
}

function onDocumentMouseUp() {
  UnTip();
  removeHighlight();
}

function openURL(url){
  window.open(url);
}

function getTooltip (obj) {
  UnTip();
  var str = obj.name.text;
  var tip = Tip(str);
}

function highlight(obj){
  removeHighlight();
    highlightedObj = obj;
    obj.material.color.r += 1;
    obj.material.color.g += 1;
    obj.material.color.b += 1;
}

function removeHighlight(){
  if(highlightedObj != undefined) {
    highlightedObj.material.color.r -= 1;
    highlightedObj.material.color.g -= 1;
    highlightedObj.material.color.b -= 1;
    highlightedObj = undefined;
  }
}

function getDistrict(obj){
  if(obj.name) {
    updateOrbitControls(obj.position.x, obj.position.z);
    camera.position.z = obj.position.z + (obj.geometry.parameters.height / 2);
    camera.position.x = obj.position.x + (obj.geometry.parameters.width / 2);
    camera.position.y = 15;
  }
  else{
    updateOrbitControls(obj.position.x, obj.position.z);
    camera.position.z = obj.position.z + (obj.geometry.parameters.height / 2) + 20;
    camera.position.x = obj.position.x ;
    camera.position.y = 50
  }
}

function createCity(label, color) {
  var city = addPlane(1, 1, {x:0, z:0});
  city.material.color.setHex(color);
  city.name = label;
  return city;
}

function createDistrict(position, label, color, containsDistricts) {
  var district = addPlane(1, 1, position);
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

function createBuilding(width, depth, height, position, label, color) {
  var building = addBox(width, depth, height, position);
  building.position.y += 0.06;
  building.material.color.setHex(color);
  if(color == "0xFE2E2E"){
    building.material.opacity = 0.5;
    building.material.transparent = true;
  }
  building.name = label;
  return building;
}

function createLabel (text, position, color){
  var loader = new THREE.FontLoader();
  loader.load("fonts/Open Sans_Regular.js", function(font){
    var textGeo = new THREE.TextGeometry(text, {
      font: font,
      size: 20,
      height: 2,
      curveSegments: 12,
      bevelThickness: 0.5,
      bevelSize: 0.5,
      bevelEnabled: true
    })
    var textMaterial = new THREE.MeshPhongMaterial();

    if (color == undefined) {
      color = "0x242424"
    }

    var textObj = new THREE.Mesh(textGeo, textMaterial);
    textObj.position.set(position.x, position.y, position.z);
    textObj.rotateX(-Math.PI / 2);
    textLabels.push(textObj);

    textObj.material.color.setHex(color);

    scene.add(textObj);
    });
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

  cityPosiion = changePositionPlane(object, (difWidth / 2), (difDepth / 2));

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
  return object.position;
}

function changePositionBox(object, x, z) {
  object.position.x += x;
  object.position.z += z;
}

function addPlane(width, depth, position) {
  //Geometry + Material = plane

  var planeGeometry = new THREE.PlaneGeometry(width, depth);
  var planeMaterial = new THREE.MeshLambertMaterial();
  var plane = new THREE.Mesh(planeGeometry, planeMaterial);

  //Position
  plane.position.x = position.x + (0.5 * width);
  plane.position.z = position.z + (0.5 * depth);

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

function addBox(width, depth, height, position) {
  //Geometry + Material = Box
  var boxGeometry = new THREE.BoxGeometry(width, height, depth);
  var boxMaterial = new THREE.MeshLambertMaterial();
  var box = new THREE.Mesh(boxGeometry, boxMaterial);

  //Position
  box.position.x = position.x + (width * 0.5);
  box.position.z = position.z + (depth * 0.5);
  box.position.y = position.y + (height * 0.5);
  //Make changeable
  box.geometry._dirtyPosition = true;
  box.geometry._dirtyVertices = true;
  box.geometry.dynamic = true;

  //Add
  scene.add(box);
  return box;
}

