var source = new EventSource('/sse/users/events');
var isOpenOnce = false;
source.onopen = function() {
    if(isOpenOnce) {
        source.close();
    } else {
        isOpenOnce = true;
    }
};
source.onmessage = function(event) {
    document.getElementById("result").innerHTML += event.data + "<br>";
};


var userSocket = new WebSocket('ws://localhost:8080/ws/users');

userSocket.onmessage = function (evt) {
    console.log("User is received...", evt.data);
};

var socket = new WebSocket('ws://localhost:8080/ws/ticks');

socket.onopen = function(evt) {
    console.log("Opened...", evt);
    socket.send("2");
    console.log("Message is sent...");
};

socket.onmessage = function (evt) {
    console.log("Message is received...", evt);
};

socket.onerror = function (err) {
    console.log("Error...", err);
};

socket.onclose = function (evt) {
    console.log("Closed...", evt);
};


