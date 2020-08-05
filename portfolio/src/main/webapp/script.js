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

google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

/** Starts all function to onload. */
function start() {
  addClosableItems();
  getMessages();
}

/** Add onClick action to some elements. */
function addClosableItems() {
  let closable = document.getElementsByClassName("closable");
  for (let i = 0; i < closable.length; i++) {
    closable[i].onclick = function() {
      closeNavigation();
    };
  }
}

/** Fetches messages from the servers and adds them to the DOM. */
function getMessages() {
  fetch('/messages-data').then(response => response.json()).then((messages) => {
    // messages is an object, not a string, so we have to
    // reference its fields to create HTML content
    const messagesElement = document.getElementById('messages-container');
    messagesElement.innerHTML = '';
    messages.forEach((message) => messagesElement.appendChild(
      createListElement(message.sender + " (" + message.email + ")" + ": " + message.content)));
  });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

/** Creates a chart and adds it to the page. */
function drawChart() {
  fetch('/color-data').then(response => response.json()).then((colorVotes) => {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Color');
    data.addColumn('number', 'Votes');
    Object.keys(colorVotes).forEach((color) => {
      data.addRow([color, colorVotes[color]]);
    });

    const options = {
      'width':500,
      'height':500,
      'backgroundColor':'transparent'
    };

    const chart = new google.visualization.PieChart(
        document.getElementById('chart-container'));
    chart.draw(data, options);
  });
}

/** Opens an animated side navigation. */
function openNavigation() {
  document.getElementById("nav").style.width = "15%";
}

/** Closes an animated side navigation. */
function closeNavigation() {
  document.getElementById("nav").style.width = "0";
}
