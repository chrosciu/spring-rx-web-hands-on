window.onload = function() {
    var button = document.getElementById('fetch-users')
    var buttonClicked = false;

    button.addEventListener('click', function() {
        if (buttonClicked) {
            return;
        }
        buttonClicked = true;
        var usersSocket = new WebSocket('ws://localhost:8080/ws/users');
        usersSocket.onmessage = function (event) {
            document.getElementById('result').innerHTML += '<div>' + event.data + '</div>';
        };
    });

    var buttonUser = document.getElementById('fetch-user')
    var buttonUserClicked = false;

    buttonUser.addEventListener('click', function() {
        if (buttonUserClicked) {
            return;
        }
        buttonUserClicked = true;
        var login = document.getElementById('login').value;
        var userSocket = new WebSocket('ws://localhost:8080/ws/user');
        userSocket.onopen = function(evt) {
            userSocket.send(login);
        };
        userSocket.onmessage = function (event) {
            document.getElementById('result-user').innerHTML += '<div>' + event.data + '</div>';
        };
    });
}


