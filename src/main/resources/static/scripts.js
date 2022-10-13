var stompClient = null;
var notificationCount = 0;
var URI = "http://localhost:9019/";

$(document).ready(function() {
    console.log("Index page is ready");
    connect();

    $("#send").click(function() {
        sendMessage();
    });

    $("#send-private").click(function() {
        sendPrivateMessage();
    });

    $("#notifications").click(function() {
        resetNotificationCount();
    });
});

function connect() {
    var socket = new SockJS('/our-websocket');  // endpoint for the websocket to subscribe some things and stuff  (open connection with the endpoint)
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        updateNotificationDisplay();
        stompClient.subscribe('/topic/messages', function (message) {
            showMessage(JSON.parse(message.body).content);
        });

        stompClient.subscribe('/topic/key-change', function (message) {
            $("#messages-key-change").append("<tr><td>"
                + (message.body) + "</td></tr>");
        });

        stompClient.subscribe('/topic/key-event', function (message) {
            $("#messages-key-event").append("<tr><td>"
                + (message.body) + "</td></tr>");
        });

        stompClient.subscribe('/user/topic/private-messages', function (message) {   // we written /user/  due to @SendToUser()
            showMessage(JSON.parse(message.body).content);
        });

        stompClient.subscribe('/topic/global-notifications', function (message) {
            notificationCount = notificationCount + 1;
            updateNotificationDisplay();
        });

        stompClient.subscribe('/user/topic/private-notifications', function (message) {
            notificationCount = notificationCount + 1;
            updateNotificationDisplay();
        });
    });


}

function showMessage(message) {
    $("#messages").append("<tr><td>" + message + "</td></tr>");
}

function sendMessage() {
    console.log("sending message");
    // stompClient.send("/ws/message", {}, JSON.stringify({'messageContent': $("#message").val()}));

    let messageModel = {"messageContent":$("#message").val()};
    let url = URI + 'send-message';

    postData(url, messageModel )
        .then((data) => {
            console.log("message sent ..");
        });

}

function sendPrivateMessage() {
    console.log("sending private message");
    // stompClient.send("/ws/private-message", {}, JSON.stringify({'messageContent': $("#private-message").val()}));

    let messageModel = {"messageContent":$("#private-message").val()};
    let url = URI + 'send-private-message/' + $("#userId").val();

    postData(url, messageModel )
        .then((data) => {
            showMessage(JSON.parse(data).content);
        });

}

function updateNotificationDisplay() {
    if (notificationCount === 0) {
        $('#notifications').hide();
    } else {
        $('#notifications').show();
        $('#notifications').text(notificationCount);
    }
}

function resetNotificationCount() {
    notificationCount = 0;
    updateNotificationDisplay();
}



async function postData(url = '', data = {}) {
    // Default options are marked with *
    const response = await fetch(url, {
        method: 'POST', // *GET, POST, PUT, DELETE, etc.
        mode: 'cors', // no-cors, *cors, same-origin
        cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
        credentials: 'same-origin', // include, *same-origin, omit
        headers: {
            'Content-Type': 'application/json'
        },
        redirect: 'follow', // manual, *follow, error
        referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: JSON.stringify(data) // body data type must match "Content-Type" header
    });
    // return response.json(); // parses JSON response into native JavaScript objects
    return response;
}
