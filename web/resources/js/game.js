(function () {
    "use strict";

    $(document).ready(function () {

        var APPLE_RADIUS = 20;

        //XXX more awesome colors
        var WORM_COLORS = ['Chartreuse', 'Darkorange', 'blue'];

        var ws;

        var canvas = $("#canvas")[0];
        var canvasContainer = $("#main-bar");
        canvasContainer.resizable();
        var context = canvas.getContext('2d');
        var match;
        var lastFrame;
        var inQueue = false;

        var headX;
        var headY;
        var mouseX;
        var mouseY;
        var scaleX = 1;
        var scaleY = 1;

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
            render(null);
        }

        function render(jsonData) {
//            console.log("render: " + jsonData);

            if (match) {
                // we use scaleX not only to scale where the mouse is relative to the worm head, but also to scale the graphics.
                // Why not use context.scale()? Because it makes lines look like crap.
                scaleX = canvas.width / match.size_x;
                scaleY = canvas.height / match.size_y;

                //XXX draw level on another canvas or something?
                context.clearRect(0, 0, canvas.width, canvas.height);
                if (jsonData) {
                    renderFrame(jsonData);
                    lastFrame = jsonData;
                } else if (lastFrame) {
                    renderFrame(lastFrame);
                }

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
                                    context.moveTo(this.x * scaleX, this.y * scaleY);
                                } else {
                                    context.lineTo(this.x * scaleX, this.y * scaleY);
                                }

                                if (index + 1 === lines.length && wormIndex === match.your_number) {
                                    headX = this.x * scaleX;
                                    headY = this.y * scaleY;
                                }
                                lastX = this.x;
                                lastY = this.y;
                            });
                            if (wormIndex === match.your_number) {
                                context.strokeStyle = 'black';
                            } else {
                                context.strokeStyle = WORM_COLORS[wormIndex];
                            }
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
                            var color;
                            if (this.type === "red") {
                                color = 'red';
                            } else {
                                color = 'yellow';
                            }
                            ellipse(this.x, this.y, APPLE_RADIUS, color);

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
                        var color = 'gray';
                        context.beginPath();
                        $.each(obstacles, function () {
                            if (this.type === 'rectangle') {
                                context.rect(this.left * scaleX, this.top * scaleY, (this.right - this.left) * scaleX, (this.bottom - this.top) * scaleY);
                            } else if (this.type === 'circle') {
                                ellipse(this.x, this.y, this.radius, color);
                            } else {
                                console.log('wtf unknown type: ' + this.type);
                            }
                        });
                        context.fillStyle = color;
                        context.fill();
                    }
                } catch (e) {
                    window.ploxworm.log("error in renderBord: " + e.stack);
                }
            }

            function ellipse(x, y, radius, color) {
                context.fillStyle = color;
                context.strokeStyle = color;
                context.save();
                context.scale(scaleX, scaleY);
                context.beginPath();
                context.arc(x, y, radius, 0, Math.PI * 2, false);
                context.stroke();
                context.closePath();
                context.fill();
                context.restore();
            }
        }

        function endRound(data) {
            window.ploxworm.log("Game ended!");
            gameRunning = false;
            if (match.your_number === data.winner_number) {
                showTitleString("YOU WON!");
            } else {
                //XXX get winner from match
                showTitleString("GAME OVER MAN!");
                if (data.winner_message) {
                    showMessageString(data.winner_message);
                }
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
            window.ploxworm.log("put in queue");
            inQueue = true;
            showMessageString("Waiting for opponents...");
        }

        function showTitle(data) {
            showTitleString(data.message);
        }

        function showTitleString(messageString) {
            var title = $("#title");
            title.text(messageString);
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
                    directionMessage.data.x = x / scaleX;
                    directionMessage.data.y = y / scaleY;
                    ws.send(JSON.stringify(directionMessage));
                } else {
//                    console.log('failed to get direction: ' + mouseX + ', ' + headX);
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

        function adjustForResolution() {
            canvas.width = canvasContainer.width();
            canvas.height = canvasContainer.height();
//            window.ploxworm.log("adjustForResolution: " + canvas.width);
            render(null);
        }

        window.ploxworm.log("loaded");

        adjustForResolution();

        window.onresize = adjustForResolution;

    });

})();