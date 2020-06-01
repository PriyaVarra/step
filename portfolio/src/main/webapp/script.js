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
 * Fetches up to maxComments comments from the server and adds it to the DOM
 * @param {string} maxComments string representation of a positive integer
 */
function getComments(maxComments) {
    const fetchURL = "/comments?max-comments=" + maxComments;

    fetch(fetchURL).then(response => response.json()).then((commentsData) => {
        const commentsContainer = document.getElementById("comments-container");
        commentsContainer.innerHTML = '';
        for (var i = 0; i < commentsData.length; i++) {
            const commentData = commentsData[i];

            // Create new comment elements
            const commentHeader = createCommentHeader(commentData.name, commentData.timeStamp);
            const commentContent = createCommentContent(commentData.comment);

            // Add new comment to comments section
            commentsContainer.appendChild(commentHeader);
            commentsContainer.appendChild(commentContent);
        }
    });
}

/**
 * Extracts number selected by user and reloads up to this number of comments to the DOM
 */
function refreshComments() {
    const selectedNumberString = document.getElementById("comments-select").value;
    getComments(selectedNumberString)
}

/**
 * Creates header for comment containing name and timestamp corresponding to comment 
 * @param {string} name Name of person who left comment
 * @param {string} timestamp String in format "mm/dd/yyyy hh:mm" denoting time comment was left
 * @return {HTML Element} An h4 text header containing name in bold, a ~ for seperation, and timestamp in italics
 */
function createCommentHeader(name, timeStamp) {
    const headerElement = document.createElement("h4");
    
    // Put commenter's name in bold
    const nameElement = document.createElement("b");
    nameElement.appendChild(document.createTextNode(name));

    // Put timestamp of comment int italics
    const tsElement = document.createElement("i");
    tsElement.appendChild(document.createTextNode(timeStamp));

    // Separate name and timestamp with ~
    headerElement.appendChild(nameElement);
    headerElement.appendChild(document.createTextNode(" ~ "));
    headerElement.appendChild(tsElement);
    
    return headerElement;
}

/**
 * Creates HTML element for comment content  
 * @param {string} content 
 * @return {HTML Element} An h5 text header containing text from content followed by two line breaks 
 */
function createCommentContent(content) {
    const contentElement = document.createElement("h5");
    contentElement.appendChild(document.createTextNode(content));

    // Add blank line after comment
    const brElement = document.createElement("br");
    contentElement.appendChild(brElement.cloneNode(true));
    contentElement.appendChild(brElement.cloneNode(true)); 

    return contentElement;
}

/**
 * Changes image and updates caption in gallery section
 * @param {number} dir -1 displays previous image and 1 displays next image
 */
function changePhoto(dir) {
    // Image captions
    const captions = ["This is Snugglebuns! A winter white dwarf hamster and my first pet.",
                    "An up close view from a boat tour of the eruption of Kileaua on Big Island in 2018.",
                    "A picturesque view of Geirangerfjord on a road trip in Norway.",
                    "Taking in the sights at the Taj Mahal.",
                    "My sister and I enjoying ourselves in Oslo",
                    "\"Sledding\" at White Sands National Park in New Mexico"]

    // Image widths
    const widths = ["600", "600", "600", "350", "600", "600"]

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
    const capNode = document.createTextNode(captions[id]);
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
  const captionText = document.getElementById("caption");
  captionText.innerHTML = element.alt;
}

