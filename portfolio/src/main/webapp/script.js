// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
 
let map;
let editMarker;
let loggedIn;
let id;
let displayName;
 
/** 
 * Gets authentication status of user and loads map and comments section. If
 * user is logged in, comments form and login buttons are displayed. Otherwise, 
 * logout buttons are displayed.  
 */
function loadPage() {
  fetch("/authentication").then(response => response.json()).then((authData) => {
    const onClickUrl = "window.location.href=\'" + authData.url + "\'";
    loggedIn = authData.loggedIn;
 
    if (loggedIn) {
      document.getElementById("comments-input").hidden = false;
      document.getElementById("map-logout").hidden = false;
      
      document.getElementById("name").value = authData.displayName;
      
      document.getElementById("logout-comments").setAttribute("onClick", onClickUrl);
      document.getElementById("logout-maps").setAttribute("onClick", onClickUrl);
 
      id = authData.id;
      displayName = authData.displayName;
    
    } else {
      document.getElementById("login-container").hidden = false;
      document.getElementById("map-login").hidden = false;
 
      document.getElementById("login-comments").setAttribute("onClick", onClickUrl);
      document.getElementById("login-maps").setAttribute("onClick", onClickUrl);
      
      id = "";
      displayName = "";
    }
 
    createMap();
    getComments();
  });
}

/**
 * Creates HTML button that executes passed in function when clicked.
 * @param {string} label Text that appears in button
 * @param {function} func Function to be executed when button is clicked
 * @return {HTML Element} 
 */
function createButton(label, func) {
  const buttonElement = document.createElement("button");
    
  buttonElement.setAttribute("class", "w3-button w3-teal");
  buttonElement.addEventListener("click", func);
  buttonElement.appendChild(document.createTextNode(label));
 
  return buttonElement;
}

/**
 * Updates content for entity in Datastore and changes content in DOM
 * according to passed in function.
 * @param {string} key String representation of entity's key in Datastore
 * @param {function} func Function that updates edited content in DOM
 */
function editKey(key, editFunc) {
  const content = document.getElementById(key + "-content").value

  postBody = new URLSearchParams();
  postBody.append("key", key);
  postBody.append("content", content);
 
  const options = { 
    method: "POST",
    headers: {"Content-Type": "application/x-www-form-urlencoded"},
    body: postBody
  };
 
  fetch("/edit-data", options).then(_ => {editFunc(content);});
}
 
 
/**
 * Deletes entity in Datastore and removes corresponding elements in DOM.
 * @param {string} key String representation of entity's key in Datastore
 * @param {function} deleteFunc Function that updates deleted content in DOM
 */
function deleteKey(key, deleteFunc) {
  const postBody = "key=" + key;

  const options = { 
    method: "POST",
    headers: {"Content-Type": "application/x-www-form-urlencoded"},
    body: postBody
  };
 
  fetch("/delete-data", options).then(_ => {deleteFunc();});
} 
 
/**
 * Fetches up to max number of comments specified by user from the server and adds them to DOM.
 */
function getComments() {
  const fetchURL = "/comments?max-comments=" + document.getElementById("comments-select").value;
 
  fetch(fetchURL).then(response => response.json()).then((commentsData) => {
    const commentsContainer = document.getElementById("comments-container");
    commentsContainer.innerHTML = '';
    
    for (commentData of commentsData) {
      // Create new comment elements
      const commentHeader = createCommentHeader(commentData.displayName, commentData.utcDate);
      const commentContentElement = document.createElement("div");
      commentContentElement.id = commentData.key;
 
      // Add new comment elements to comments section
      commentsContainer.appendChild(commentHeader);
      commentsContainer.appendChild(commentContentElement);

      // Place content of user comment into commentContentElement;
      addCommentContent(commentData);
    }
  });
}
 
/**
* Converts date and time from UTC to local timezone
* @param {Date} utcDate
* @return {string} string in form M/dd/yyyy hh:mm a for local timezone
*/
function convertUTCDate(utcDate) {
  let localDate = new Date(utcDate);
    
  // Format local date to M/dd/yyyy hh:mm a
  const options = {year: "numeric", month: "numeric", day: "2-digit", hour: "2-digit", minute: "2-digit"}; 
  return localDate.toLocaleDateString("en-us", options);
}
 
/**
 * Creates header for comment containing name and timestamp corresponding to comment 
 * @param {string} name Name of person who left comment
 * @param {string} utcDate String denoting time comment was left
 * @return {HTML Element} An h4 text header containing name in bold, a ~ for separation, and timestamp in italics
 */
function createCommentHeader(name, utcDate) {
  const localDate = convertUTCDate(utcDate);
 
  const headerElement = document.createElement("h4");
    
  // Put commenter's name in bold
  const nameElement = document.createElement("b");
  nameElement.appendChild(document.createTextNode(name));
 
  // Put timestamp of comment in italics
  const tsElement = document.createElement("i");
  tsElement.appendChild(document.createTextNode(localDate));
 
  // Separate name and timestamp with ~
  headerElement.appendChild(nameElement);
  headerElement.appendChild(document.createTextNode(" ~ "));
  headerElement.appendChild(tsElement);
    
  return headerElement;
}
 
/**
 * Adds content of comment left by user to contentElement. If current, logged in
 * user previously wrote comment, edit and delete buttons are also added to
 * content element.
 * @param {obj} commentData Contains information about comment left by user
 */
function addCommentContent(commentData) {
  // Get div identified by key
  const divElement = document.getElementById(commentData.key);
  divElement.innerHTML = "";
  
  // Place text in h5 header
  const contentElement = document.createElement("h5");
  contentElement.appendChild(document.createTextNode(commentData.comment));
 
  // Add linebreak after text
  contentElement.appendChild(document.createElement("br"));
 
  divElement.appendChild(contentElement);

  // If current user also wrote comment, add edit and delete buttons
  if (loggedIn && commentData.id == id) {      
    const editFunc = () => editCommentElement(commentData);
    divElement.appendChild(createButton("Edit", editFunc));

    const nbsp = "\u00A0"; // non-breaking space
    divElement.appendChild(document.createTextNode(nbsp));
    
    const deleteFunc = () => deleteKey(commentData.key, getComments);
    divElement.appendChild(createButton("Delete", deleteFunc));
  }
}


/**
 * Creates textarea populated with existing comment for user to edit and
 * buttons to cancel or submit changes.
 * @param {string} key String representation of comment's key in Datastore
 * @param {obj} commentData Contains information about comment
 */
function editCommentElement(commentData) {   
  // Get and clear div containing current comment, edit, and delete buttons
  const contentElement = document.getElementById(commentData.key);  
  contentElement.innerHTML = "";
    
  // Create textarea and populate with existing comment
  const editBox = document.createElement("textarea");
  editBox.id = commentData.key + "-content";
  editBox.value = commentData.comment;

  // If user edits comment, commentData and element containing comment are updated.
  const editSubmitFunc = (content) => {
    commentData.comment = content;
    addCommentContent(commentData);
  }

  // If user cancels edit, re-fill element containing comment with original data.
  const cancelFunc = () => {addCommentContent(commentData)};
 
  const submitFunc = () => {editKey(commentData.key, editSubmitFunc)};
  
  const submitButton = createButton("Submit", submitFunc);
  const cancelButton = createButton("Cancel", cancelFunc);
 
  contentElement.appendChild(editBox);

  contentElement.appendChild(document.createElement("br"));

  contentElement.appendChild(cancelButton);

  const nbsp = "\u00A0"; // non-breaking space
  
  contentElement.appendChild(document.createTextNode(nbsp));
  contentElement.appendChild(submitButton);
}
 
/** Saves submitted information from comment form in Datastore.*/
function postComment() {
  const name = document.getElementById("name").value;
  const comment = document.getElementById("comment").value;

  displayName = name;

  const postBody = new URLSearchParams();
  postBody.append("Name", name);
  postBody.append("Comment", comment);
 
  const options = { 
    method: "POST",
    headers: {"Content-Type": "application/x-www-form-urlencoded"},
    body: postBody
  };
 
  // Reload comments section of page after processing new comment
  fetch("/comments", options).then(_ => {
    document.getElementById("name").value = displayName;
    getComments();
  });
}
 
/** Creates a map and adds it to the page. */
function createMap() {
  map = new google.maps.Map(
    document.getElementById("map-container"),
    {center: {lat: 36.150813, lng: -40.352239}, zoom: 2});
 
  if (loggedIn) {
    map.addListener("click", (event) => {
      createMarkerForEdit(event.latLng.lat(), event.latLng.lng());
    });
  }
 
  fetchMarkers();
}
 
/** Fetches markers from Datastore and adds them to the map. */
function fetchMarkers() {
  fetch("/markers").then(response => response.json()).then((markersData) => {
    markersData.forEach((markerData) => {createMarkerForDisplay(markerData)});
  });
}
 
/**
 * Creates a marker that shows a read-only info window when clicked. If current,
 * logged-in user added marker, also adds edit and delete buttons to window.
 * @param {obj} markerData Contains information about marker from Datastore
 */
function createMarkerForDisplay(markerData) {
  const marker = new google.maps.Marker({
    position: {lat: markerData.lat, lng: markerData.lng},
    map: map,
  });
 
  const nameNode = document.createTextNode(markerData.displayName + " says:");
  const boldElement = document.createElement("b");
  boldElement.appendChild(nameNode);
 
  const contentContainer = document.createElement("p");
  contentContainer.appendChild(boldElement);
  contentContainer.appendChild(document.createElement("br"));
  contentContainer.appendChild(document.createTextNode(markerData.content));
  contentContainer.appendChild(document.createElement("br"));
 
  let infoWindow = new google.maps.InfoWindow();

  if (loggedIn && markerData.id == id) {
    const editFunc = () => editMapElement(marker, infoWindow, markerData);
    contentContainer.appendChild(createButton("Edit", editFunc));

    const nbsp = "\u00A0" // non-breaking space
    contentContainer.appendChild(document.createTextNode(nbsp));
    
    const closeFunc = () => {infoWindow.close(); marker.setMap(null);};
    const deleteFunc = () => deleteKey(markerData.key, closeFunc);
    contentContainer.appendChild(createButton("Delete", deleteFunc));
  }
 
  infoWindow.setContent(contentContainer);

  marker.addListener("click", () => {
    infoWindow.open(map, marker);
  });
}

/**
 * Creates textarea populated with existing description for user to edit and
 * buttons to cancel or submit changes.
 * @param {Marker} marker Marker on map
 * @param {InfoWindow} infoWindow InfoWindow corresponding to marker
 * @param {obj} markerData Contains information about marker from Datastore
 */
function editMapElement(marker, infoWindow, markerData) {
  const containerDiv = document.createElement("div");
    
  const editBox = document.createElement("textarea");
  editBox.id = markerData.key + "-content";
  editBox.value = markerData.content;

  // If user submits edit, close edit infoWindow and marker 
  // and create new marker and infoWindow with updated content
  const editSubmitFunc = (content) => {
    marker.setMap(null);
    infoWindow.close();
    markerData.content = content;
    createMarkerForDisplay(markerData);
  }

  // If user cancels edit, close edit infoWindow and marker 
  // and create new marker and infoWindow with original content
  const cancelFunc = () => {
    marker.setMap(null);
    infoWindow.close();
    createMarkerForDisplay(markerData);
  }

  const submitFunc = () => {editKey(markerData.key, editSubmitFunc)};

  const submitButton = createButton("Submit", submitFunc);
  const cancelButton = createButton("Cancel", cancelFunc);
 
  containerDiv.appendChild(editBox);
  containerDiv.appendChild(document.createElement("br"));
  containerDiv.appendChild(cancelButton);
    
  const nbsp = "\u00A0"; // non-breaking space
  containerDiv.appendChild(document.createTextNode(nbsp));
 
  containerDiv.appendChild(submitButton);
 
  infoWindow.setContent(containerDiv);
}
 
/** 
 * Creates a marker that shows a textbox the user can edit.
 * @param {number} lat Latitude of marker on map
 * @param {number} lng Longitude of marker on map
 */
function createMarkerForEdit(lat, lng) {
  // If we're already showing an editable marker, then remove it.
  if (editMarker) {
    editMarker.setMap(null);
  }
 
  editMarker =
      new google.maps.Marker({position: {lat: lat, lng: lng}, map: map});
 
  const infoWindow =
      new google.maps.InfoWindow({content: buildInfoWindowInput(lat, lng)});
 
  // When the user closes the editable info window, remove the marker.
  google.maps.event.addListener(infoWindow, 'closeclick', () => {
    editMarker.setMap(null);
  });
 
  infoWindow.open(map, editMarker);
}
 
/**
 * Builds and returns HTML elements that show editable textboxes and submit button.
 * @param {number} lat Latitude of marker on map
 * @param {number} lng Longitude of marker on map
 * @return {HTML element} Div containing text boxes and submit button
 */
function buildInfoWindowInput(lat, lng) {
  const nameBox = document.createElement("input");
  nameBox.type = "text";
  nameBox.value = displayName.length == 0 ? "Your Name": displayName;
  nameBox.style = "text-align:center";
 
  const textBox = document.createElement("textarea");
  textBox.appendChild(document.createTextNode("Description of this location"))
  
  const submitFunc = () => {
    editMarker.setMap(null);  
    postMarker(lat, lng, nameBox.value, textBox.value);
  };

  const submitButton = createButton("Submit", submitFunc);
 
  const containerDiv = document.createElement("div");
  containerDiv.appendChild(nameBox);
  containerDiv.appendChild(document.createElement("br"));
  containerDiv.appendChild(textBox);
  containerDiv.appendChild(document.createElement("br"));
  containerDiv.appendChild(submitButton);
 
  return containerDiv;
}
 
/**
 * Saves submitted information from editable InfoWindow in Datastore.
 * @param {number} lat Latitude of marker on map
 * @param {number} lng Longitude of marker on map
 * @param {string} name Displayname entered by user
 * @param {string} content Content of comment left by user
 */
function postMarker(lat, lng, name, content) {
  const postBody = new URLSearchParams();
  postBody.append("lat", lat);
  postBody.append("lng", lng);
  postBody.append("name", name);
  postBody.append("content", content);
  
  displayName = name;

  const options = { 
    method: "POST",
    headers: {"Content-Type": "application/x-www-form-urlencoded"},
    body: postBody
  };
 
  // Reload maps section of page after processing new marker
  fetch("/markers", options).then(_ => {createMap();});
}

/**
 * Changes image and updates caption in gallery section
 * @param {number} dir -1 displays previous image and 1 displays next image
 */
function changeImage(dir) {
  // Image captions
  const captions = [
    "This is Snugglebuns! A winter white dwarf hamster and my first pet.",
    "An up close view from a boat tour of the eruption of Kileaua on Big Island in 2018.",
    "A picturesque view of Geirangerfjord on a road trip in Norway.",
    "Taking in the sights at the Taj Mahal.",
    "My sister and I enjoying ourselves in Oslo",
    "\"Sledding\" at White Sands National Park in New Mexico",
  ];
 
  // Image widths
  const widths = ["600", "600", "600", "350", "600", "600"]
 
  // Get current image id
  const curr_img = document.getElementById("current_image").src;
  let id = parseInt(curr_img.charAt(curr_img.length - 5));
 
  // Compute id of new image. Keeps index in range [0,5]
  id = ((id + dir) + 6) % 6;
 
  // Create new image element
  const new_img_src = "gallery/gallery" + id + ".jpg";
 
  const imgElement = document.createElement("img");
  imgElement.src = new_img_src;
  imgElement.id = "current_image";
  imgElement.width = widths[id];
  imgElement.height = "450";
  imgElement.style = "border: 15px solid #008080";
 
  // Create new caption element
  const captionElement = document.createElement("h4");
  captionElement.appendChild(document.createTextNode(captions[id]));

  // Remove previous image and caption and add new image and caption. 
  const imageContainer = document.getElementById("img-container");
  imageContainer.innerHTML = '';
  imageContainer.appendChild(imgElement);
  imageContainer.appendChild(captionElement);
}
 
/**
 * Functions from w3CSS: https://www.w3schools.com/w3css/w3css_templates.asp
 */
 
// Script to open and close sidebar
function w3_open() {
  document.getElementById("mySidebar").style.display = "block";
  document.getElementById("myOverlay").style.display = "block";
}
 
function w3_close() {
  document.getElementById("mySidebar").style.display = "none";
  document.getElementById("myOverlay").style.display = "none";
}
 
// Modal Image Gallery
function onClick(element) {
  document.getElementById("img01").src = element.src;
  document.getElementById("modal01").style.display = "block";
  const captionText = document.getElementById("caption");
  captionText.innerHTML = element.alt;
}
 