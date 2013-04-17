(function () {
    "use strict";

    $(document).ready(function () {

        var APPLE_RADIUS = 20;

        var ws;

        var canvas = $("#canvas")[0];
        var context = canvas.getContext('2d');
        var match;
        var inQueue = false;

        var headX;
        var headY;
        var mouseX;
        var mouseY;

        var gameRunning = false;

        $("#start-game").click(function () {
            prepareGame();
        });

        $("#stop-game").click(function () {
            webSocketStop();
        });

        function prepareGame() {
            if (!ws || ws.readyState !== 1) {
                if (ws) {
                    window.ploxworm.log("websocket readystate: " + ws.readyState);
                } else {
                    window.ploxworm.log("websocket: " + ws);
                }
                webSocketInit();
            } else {
                sendGameRequest();
            }
        }

        function webSocketInit() {
            if ("WebSocket" in window) {
                window.ploxworm.log("Opening websocket");


                var webSocketUrl = getWsUrl();
                window.ploxworm.log("webSocketUrl: " + webSocketUrl);
                ws = new WebSocket(webSocketUrl);
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
                    } else if (msgJson.type === 'scoreboard') {
                        updateScoreboard(msgJson.data);
                    } else if (msgJson.type === 'match') {
                        //currently this is our "game started" signal
                        startGame(msgJson.data);
                    } else if (msgJson.type === 'end_round') {
                        endRound(msgJson.data);
                    } else if (msgJson.type === 'death') {
                        death(msgJson.data);
                    } else if (msgJson.type === 'put_in_queue') {
                        putInQueue(msgJson.data);
                    } else if (msgJson.type === 'show_title') {
                        showTitle(msgJson.data);
                    } else if (msgJson.type === 'hide_title') {
                        hideTitle();
                    } else if (msgJson.type === 'show_message') {
                        showMessage(msgJson.data);
                    } else if (msgJson.type === 'hide_message') {
                        hideMessage();
                    } else {
                        window.ploxworm.log('unknown data: ' + msgJson);
                        window.ploxworm.log('unknown data 2: ' + msgJson.type);
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
            window.ploxworm.log("Sendings game request!");

            var matchRequest = {};
            var data = {};
            matchRequest.type = "match_request";
            matchRequest.data = data;
            var gameType = $("#game-type").val();
            var level = $("#level").val();
            data.game_type = gameType;
            data.level = level;
            data.player_name = $("#player_name").val();
            data.winning_message = $("#winning_message").val();
            ws.send(JSON.stringify(matchRequest));
        }

        function updateScoreboard(jsonData) {
            var scores = jsonData.scores;

            var scoreList = $("#score-list");
            scoreList.empty();
            $.each(scores, function (index, value) {
                var listItem = $(document.createElement('li'));
                listItem.text(value.player_name + ":\t" + value.score);
                scoreList.append(listItem);
            });
        }

        function startGame(data) {
            saveMatchData(data);
            gameRunning = true;
            startSendingPosition();
            if (inQueue) {
                playSound("3beeps");
                inQueue = false;
            }
        }

        function saveMatchData(jsonData) {
//            window.ploxworm.log('saveMatchData your_number: ' + jsonData.your_number);
            match = jsonData;
            renderBoard();
        }

        function render(jsonData) {
//            console.log("render: " + jsonData);

            //XXX draw level on another canvas or something?
            context.clearRect(0, 0, 800, 800);
            renderFrame(jsonData);
            renderBoard();
        }

        function renderFrame(jsonData) {
            renderWorms(jsonData);
            renderApples(jsonData);
        }

        function renderWorms(jsonData) {
            try {
                var worms = jsonData.worms;
                if (worms) {
                    $.each(worms, function (wormIndex, value) {
                        context.beginPath();
                        var lines = this;
                        var lastX;
                        var lastY;
                        $.each(this, function (index) {
                            // move context if this is the first step or if we just crossed a border
                            if (index === 0 || Math.abs(lastX - this.x) > 100 || Math.abs(lastY - this.y) > 100) {
                                context.moveTo(this.x, this.y);
                            } else {
                                context.lineTo(this.x, this.y);
                            }

                            if (index + 1 === lines.length && wormIndex === match.your_number) {
                                headX = this.x;
                                headY = this.y;
                            }
                            lastX = this.x;
                            lastY = this.y;
                        });
                        context.stroke();
                    });
                } else {
                    console.log("no worms");
                }
            } catch (e) {
                window.ploxworm.log("error in renderWorms: " + e.stack);
            }
        }

        function renderApples(jsonData) {
            try {
                var apples = jsonData.apples;
                if (apples) {
                    $.each(apples, function (appleIndex, value) {
                        context.beginPath();
                        context.arc(this.x, this.y, APPLE_RADIUS, 0, 2 * Math.PI, false);
                        if (this.type === "red") {
                            context.fillStyle = 'red';
                        } else {
                            context.fillStyle = 'yellow';
                        }
                        context.fill();
                    });
                } else {
                    console.log("no apples");
                }

            } catch (e) {
                window.ploxworm.log("error in renderApples: " + e.stack);
            }
        }

        function renderBoard() {
            try {
                var obstacles = match.obstacles;
                if (obstacles) {
                    context.beginPath();
                    $.each(obstacles, function () {
                        if (this.type === 'rectangle') {
                            context.rect(this.left, this.top, this.right - this.left, this.bottom - this.top);
                        } else if (this.type === 'circle') {
                            context.arc(this.x, this.y, this.radius, 0, 2 * Math.PI, false);
                        } else {
                            console.log('wtf unknown type: ' + this.type);
                        }
                    });
                    context.fillStyle = 'gray';
                    context.fill();
                }
            } catch (e) {
                window.ploxworm.log("error in renderBord: " + e.stack);
            }
        }

        function endRound(data) {
            window.ploxworm.log("Game ended!");
            gameRunning = false;
            if (match.your_number === data.winner) {
                showTitle("YOU WON!");
            } else {
                //XXX get winner from match
                showTitle("GAME OVER MAN!");
            }
        }

        function death(data) {
            window.ploxworm.log("death");
            if (data.player_number === match.your_number) {
                showMessageString("You died!");
            } else {
                //XXX find his name
                window.ploxworm.log("Some loser died");
            }
        }

        function putInQueue(data) {
            window.ploxworm.log("putInQueue");
            inQueue = true;
            showMessageString("Waiting for opponents...");
        }

        function showTitle(data) {
            var title = $("#title");
            title.text(data.message);
            title.show();
        }

        function hideTitle() {
            var title = $("#title");
            title.hide();
        }

        function showMessage(data) {
            showMessageString(data.message);
        }

        function showMessageString(messageString) {
            var message = $("#message");
            window.ploxworm.log("show message: " + messageString);
            message.text(messageString);
            message.show();
        }

        function hideMessage() {
            var message = $("#message");
            message.hide();
        }

        function playSound(soundObjId) {
            var sound = document.getElementById(soundObjId);
            sound.Play();
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
        });

        function startSendingPosition() {
            setTimeout(updateWormDirection, 500);  //XXX time it takes for game to start, actually, or something lol

            function updateWormDirection() {
//                console.log('updateWormDirection');
                var x = mouseX - headX;
                var y = mouseY - headY;

                if (!isNaN(x) && !isNaN(y)) {
                    var directionMessage = {};
                    directionMessage.type = 'direction';
                    directionMessage.data = {};
                    directionMessage.data.x = x;
                    directionMessage.data.y = y;
//                    console.log('sending direction: ' + x + ', ' + y);
                    ws.send(JSON.stringify(directionMessage));
                } else {
                    console.log('failed to get direction: ' + mouseX + ', ' + headX);
                }

                if (gameRunning) {
                    setTimeout(updateWormDirection, 50);
                }
            }
        }

        function getPort() {
            if (typeof location.port !== "undefined" && location.port !== "") {
                window.ploxworm.log("found port lol: \"" + location.port + "\"");
                return location.port;
            } else if (location.protocol === 'http') {
                return 80;
            } else if (location.protocol === 'https') {
                return 443;
            } else if (typeof location.port === "undefined") {
                window.ploxworm.log("Port was undefined! Going with 8080!");
                return 8080;
            } else if (location.port === "") {
                window.ploxworm.log("Port was \"\"! Going with 8080!");
                return 8080;
            } else {
                window.ploxworm.log("FUCK! No port found! Going with 8080!");
                return 8080;
            }
        }

        function getWsUrl() {
            // ugly hax to get around that I failed to forward websockets through apache2
            if (window.location.hostname === "ploxworm.com") {
                return 'ws://' + window.location.hostname + ':' + getPort() + '/PloxWormWeb/chat';
            } else {
                return 'ws://' + window.location.hostname + ':' + getPort() + '/chat';
            }
        }

        window.ploxworm.log("loaded");
    });

})();