(function () {
    "use strict";

    $(document).ready(function () {

        var ws;

        var canvas = $("#canvas")[0];
        var context = canvas.getContext('2d');
        var match;

        var headX;
        var headY;
        var mouseX;
        var mouseY;

        var gameRunning = false;

        $("#start-game").click(function () {
            webSocketInit();
        });

        $("#stop-game").click(function () {
            webSocketStop();
        });

        function webSocketInit() {
            if ("WebSocket" in window) {
                window.ploxworm.log("Opening websocket");

                ws = new WebSocket('ws://' + window.location.hostname + ':' + location.port + '/chat');
                ws.onopen = function () {
                    window.ploxworm.log("Connection open!");
                    sendGameRequest();
                };
                ws.onmessage = function (evt) {
                    var received_msg = evt.data;
//                    window.ploxworm.log("Received: " + received_msg);
                    var msgJson = $.parseJSON(received_msg);
//                    console.log("msgJson.type: " + msgJson.type);
                    if (msgJson.type === 'frame') {
                        render(msgJson.data);
                    } else if (msgJson.type === 'match') {
                        saveMatchData(msgJson.data);
                        //XXX currently this is our "game started" signal
                        gameRunning = true;
                        startSendingPosition();
                    } else {
                        window.ploxworm.log('unknown data: ' + msgJson);
                    }
                };
                ws.onclose = function (evt) {
                    window.ploxworm.log("Connection closed!");
                    gameRunning = false;
                };
                ws.onerror = function (evt) {
                    window.ploxworm.log("error!");
                    gameRunning = false;
                };
            } else {
                // The browser doesn't support WebSocket
                alert("WebSocket NOT supported by your Browser! GTFO!");
            }
        }

        function sendGameRequest() {
            var matchRequest = {};
            var data = {};
            matchRequest.type = "match_request";
            matchRequest.data = data;
            var gameType = $("#game-type").val();
            var level = $("#level").val();
            data.game_type = gameType;
            data.level = level;
            ws.send(JSON.stringify(matchRequest));
        }

        function saveMatchData(jsonData) {
            window.ploxworm.log('saveMatchData: ' + jsonData);
            match = jsonData;
            renderBoard();
        }

        function render(jsonData) {
//            console.log("render: " + jsonData);

            //TODO draw level on another canvas or something?
            context.clearRect(0, 0, 800, 800);
            renderFrame(jsonData);
            renderBoard();
        }

        function renderFrame(jsonData) {
            var worms = jsonData.worms;

            $.each(worms, function () {
                context.beginPath();
                var lines = this;
                $.each(this, function (index) {
                    if (index === 0) {
                        context.moveTo(this.x, this.y);
                    } else {
                        context.lineTo(this.x, this.y);
                    }
                    if (index + 1 === lines.length) {
                        headX = this.x;
                        headY = this.y;
                    }
                });
                context.stroke();
            });
        }

        function renderBoard() {
            var obstacles = match.obstacles;
            context.beginPath();
            $.each(obstacles, function () {
                var data = this.data;
                if (this.type === 'rectangle') {
                    context.rect(data.left, data.top, data.right - data.left, data.bottom - data.top);
                } else if (this.type === 'circle') {
                    context.arc(data.x, data.y, data.radius, 0, 2 * Math.PI, false);
                } else {
                    console.log('wtf unknown type: ' + this.type);
                }
            });
            context.fillStyle = 'gray';
            context.fill();
        }

        $(window).bind('beforeunload', function () {
            webSocketStop();
        });

        function webSocketStop() {
            window.ploxworm.log("webSocketStop");
            if (ws) {
                ws.close();
            }
        }

        $("#canvas").mousemove(function (event) {
            //taken from http://stackoverflow.com/questions/1114465/getting-mouse-location-in-canvas
            if (event.offsetX) {
                mouseX = event.offsetX;
                mouseY = event.offsetY;
            } else if (event.layerX) {
                mouseX = event.layerX;
                mouseY = event.layerY;
            }
//            window.ploxworm.log("move: " + mouseX + ", " + mouseY);
        });

        function startSendingPosition() {
            setTimeout(updateWormDirection, 500);  //TODO time it takes for game to start, actually, or something lol

            function updateWormDirection() {
//                console.log('updateWormDirection');
                //TODO should care about the canvas size and stuff...
//                var x = mouseX - 400;
//                var y = mouseY - 400;
                var x = mouseX - headX;
                var y = mouseY - headY;

                if (!isNaN(x) && !isNaN(y)) {
                    var directionMessage = {};
                    directionMessage.type = 'direction';
                    directionMessage.data = {};
                    directionMessage.data.x = x;
                    directionMessage.data.y = y;
                    console.log('sending direction: ' + x + ', ' + y);
                    ws.send(JSON.stringify(directionMessage));
                }

                if (gameRunning) {
                    setTimeout(updateWormDirection, 50);
                }
            }
        }

        window.ploxworm.log("loaded");
    });

})();