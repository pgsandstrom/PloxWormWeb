require(["jquery", "websocket"],

    function (jquery, websocketConstructor) {
        "use strict";

        var game = function () {

            var APPLE_RADIUS = 20;

            //XXX more awesome colors
            var WORM_COLORS = ['Chartreuse', 'Darkorange', 'blue'];

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
                websocket.prepareGame();
            });

            $("#stop-game").click(function () {
                websocket.webSocketStop();
            });

            game.updateScoreboard = function updateScoreboard(jsonData) {
                var scores = jsonData.scores;

                var scoreList = $("#score-list");
                scoreList.empty();
                $.each(scores, function (index, value) {
                    var listItem = $(document.createElement('li'));
                    listItem.text(value.player_name + ":\t" + value.score);
                    scoreList.append(listItem);
                });
            };

            game.startGame = function startGame(data) {
                saveMatchData(data);
                gameRunning = true;
                startSendingPosition();
                if (inQueue) {
                    playSound("3beeps");
                    inQueue = false;
                }
            };

            function saveMatchData(jsonData) {
                match = jsonData;
                game.render(null);
            }

            game.render = function render(jsonData) {

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
            };

            game.endRound = function endRound(data) {
                window.ploxworm.log("Game ended!");
                gameRunning = false;
                if (match.your_number === data.winner_number) {
                    game.showTitleString("YOU WON!");
                } else {
                    //XXX get winner from match
                    game.showTitleString("GAME OVER MAN!");
                    if (data.winner_message) {
                        game.showMessageString(data.winner_message);
                    }
                }
            };

            game.death = function death(data) {
                window.ploxworm.log("death");
                if (data.player_number === match.your_number) {
                    game.showMessageString("You died!");
                } else {
                    //XXX find his name
                    window.ploxworm.log("Some loser died");
                }
            };

            game.putInQueue = function putInQueue(data) {
                window.ploxworm.log("put in queue");
                inQueue = true;
                game.showMessageString("Waiting for opponents...");
            };

            game.showTitle = function showTitle(data) {
                game.showTitleString(data.message);
            };

            game.showTitleString = function showTitleString(messageString) {
                var title = $("#title");
                title.text(messageString);
                title.show();
            };

            game.hideTitle = function hideTitle() {
                var title = $("#title");
                title.hide();
            };

            game.showMessage = function showMessage(data) {
                game.showMessageString(data.message);
            };

            game.showMessageString = function showMessageString(messageString) {
                var message = $("#message");
                message.text(messageString);
                message.show();
            };

            game.hideMessage = function hideMessage() {
                var message = $("#message");
                message.hide();
            };

            function playSound(soundObjId) {
                var sound = document.getElementById(soundObjId);
                sound.Play();
            }

            $(window).bind('beforeunload', function () {
                websocket.webSocketStop();
            });


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
                        websocket.sendPosition(x, y, scaleX, scaleY);
                    }

                    if (gameRunning) {
                        setTimeout(updateWormDirection, 50);
                    }
                }
            }


            function adjustForResolution() {
                canvas.width = canvasContainer.width();
                canvas.height = canvasContainer.height();
//            window.ploxworm.log("adjustForResolution: " + canvas.width);
                game.render(null);
            }

            window.ploxworm.log("loaded");

            adjustForResolution();

            window.onresize = adjustForResolution;
            console.log("game.js ran");

            return game;
        };

        //this solution really is kind of wtf-worthy...
        game = game();

        var websocket = websocketConstructor(game);

        console.log("game.js outer ran");
        return game;
    }
);
