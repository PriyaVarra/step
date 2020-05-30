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




/**
 * Fetches comments from the server and adds it to the DOM
 */
function getComments() {
    fetch("/comments").then(response => response.json()).then((commentsData) => {
        const commentsContainer = document.getElementById("comments-container");
        commentsContainer.innerHTML = '';
        for (var i = 0; i < commentsData.length; i++) {
            var commentData = commentsData[i];

            // Create new comment elements
            var commentHeader = createCommentHeader(commentData);
            var commentContent = createCommentContent(commentData);

            // Add new comment to comments section
            commentsContainer.appendChild(commentHeader);
            commentsContainer.appendChild(commentContent);
        }
    });

}

/* Creates header for comment containing name in bold and timestamp in italics */
function createCommentHeader(commentData) {
    var headerElement = document.createElement("h4");
    
    var nameElement = document.createElement("b");
    nameElement.appendChild(document.createTextNode(commentData.name));

    var sep = document.createTextNode(" ~ ");

    var tsElement = document.createElement("i");
    tsElement.appendChild(document.createTextNode(commentData.timeStamp));

    headerElement.appendChild(nameElement);
    headerElement.appendChild(sep);
    headerElement.appendChild(tsElement);
    
    return headerElement;
}

/* Creates element for comment content */
function createCommentContent(commentData) {
    var contentElement = document.createElement("h5");
    contentElement.appendChild(document.createTextNode(commentData.comment));

    // Add blank line after comment
    var brElement = document.createElement("br");
    contentElement.appendChild(brElement.cloneNode(true));
    contentElement.appendChild(brElement.cloneNode(true)); 

    return contentElement;
}

/**
 * Changes image and updates caption in gallery section
 * dir = 1 goes to next image
 * dir = -1 goes to previous image
 */
function changePhoto(dir) {
    // Image captions
    var captions = ["This is Snugglebuns! A winter white dwarf hamster and my first pet.",
                    "An up close view from a boat tour of the eruption of Kileaua on Big Island in 2018.",
                    "A picturesque view of Geirangerfjord on a road trip in Norway.",
                    "Taking in the sights at the Taj Mahal.",
                    "My sister and I enjoying ourselves in Oslo",
                    "\"Sledding\" at White Sands National Park in New Mexico"]

    // Image widths
    var widths = ["600", "600", "600", "350", "600", "600"]

    // Get current image id
    const curr_img = document.getElementById("current_image").src;
    var id = parseInt(curr_img.charAt(curr_img.length - 5));

    console.log("Current image " + curr_img);
    
    // Compute id of new image
    id = id + dir;

    if (id < 0) {
        id = 5;
    }

    if (id > 5) {
        id = 0;
    }

    
    // Create new image element
    const prev_img_src = "gallery/gallery" + id + ".jpg";

    const imgElement = document.createElement("img");
    imgElement.src = prev_img_src;
    imgElement.id = "current_image";
    imgElement.width = widths[id];
    imgElement.height = "450";
    imgElement.style = "border: 15px solid #008080";

    // Create new caption element
    const capElement = document.createElement("h4");
    var capNode = document.createTextNode(captions[id]);
    capElement.appendChild(capNode);

    // Remove previous image and caption and add new image and caption. 
    const imageContainer = document.getElementById("img-container");
    imageContainer.innerHTML = '';
    imageContainer.appendChild(imgElement);
    imageContainer.appendChild(capElement)
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
  var captionText = document.getElementById("caption");
  captionText.innerHTML = element.alt;
}

