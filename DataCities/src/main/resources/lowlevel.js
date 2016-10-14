/**
 * Created by Lea on 12.05.2016.
 */

var scene, renderer, camera, directionalLight, controls, highlightedObj, currentTip;
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
  controls.maxDistance = 1000;
  controls.maxPolarAngle = Math.PI/2 - 0.1;
  controls.rotateSpeed = 0.17;
  controls.zoomSpeed = 1.4;


  document.addEventListener('mousedown', onDocumentMouseDown, false);
  document.addEventListener('mouseup', onDocumentMouseUp, false);
  window.addEventListener( 'resize', onWindowResize, false );
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
    if(event.ctrlKey){
      getDistrict(obj);
    }
    else{
      getTooltip(obj);
      highlight(obj);

    }
  }

}

function highlight(obj){
  if(obj.name.indexOf("City") == -1) {
    highlightedObj = obj;
    obj.material.color.r += 0.5;
    obj.material.color.g += 0.5;
    obj.material.color.b += 0.5;
  }
}

function removeHighlight(){
  if(highlightedObj != undefined) {
    highlightedObj.material.color.r -= 0.5;
    highlightedObj.material.color.g -= 0.5;
    highlightedObj.material.color.b -= 0.5;
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

function onDocumentMouseUp() {
  removeHighlight();
  UnTip();
}

function getTooltip (obj) {
  var str = obj.name;
  Tip(str);
}

function light_update() {
  directionalLight.position.copy(camera.position);
}

function render() {
  requestAnimationFrame(render);
  controls.update();
  renderer.render(scene, camera);
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

