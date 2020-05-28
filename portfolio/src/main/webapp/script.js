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
 * Adds a random greeting to the page.
 */
function changePhoto(dir) {
    // Image captions
    var captions = ["This is Snugglebuns! She is a winter white dwarf hamster, and she was my first pet.", "An up close view of the eruption of Kileaua on Big Island in 2018 "]


    // Get current image id
    const curr_img = document.getElementById("current_image").src;
    var id = parseInt(curr_img.charAt(curr_img.length - 5));

    console.log("Current image " + curr_img);
    // Compute id of previous image
    id = id + dir;

    if (id < 0) {
        id = 4;
    }

    if (id > 4) {
        id = 0;
    }

    
    // Create new image element
    const prev_img_src = "gallery/gallery" + id + ".jpg";
    const imgElement = document.createElement('img');
    imgElement.src = prev_img_src;
    imgElement.id = "current_image";
    imgElement.width = "600";
    imgElement.height = "450";

    const imageContainer = document.getElementById('img-container');
    // Remove the previous image.
    imageContainer.innerHTML = '';
    imageContainer.appendChild(imgElement);
}




/**
 * Functions from w3css
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

