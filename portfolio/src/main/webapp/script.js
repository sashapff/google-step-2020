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
  getMessages();
}

/** Fetches messages from the servers and adds them to the DOM. */
function getMessages() {
  fetch('/data').then(response => response.json()).then((messages) => {
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
  const data = google.visualization.arrayToDataTable([
    ['App', 'Minutes'],
    ['Youtube', 40],
    ['Tik-Tok', 20],
    ['Instagram', 100]
  ]);

  const options = {
    'title': 'Screen Time',
    'width':500,
    'height':400
  };

  const chart = new google.visualization.ColumnChart(
      document.getElementById('chart-container'));
  chart.draw(data, options);
}
