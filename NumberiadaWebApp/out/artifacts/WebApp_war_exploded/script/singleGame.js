
var refreshRate = 1500; //miliseconds
var isPlayerActive;
var isDoneTwoTurns;
var userName;

function ajaxGamesList() {
    $.ajax({
        type: "GET",
        url: "movetogame",
        contentType: "application/json;",
        dataType: 'json',
        data: {"updatePlayresList": "updatePlayresList"},
        success: function(players) {
            updatePlayersList(players);
        },
        error:function (error) {
            console.log("refreshing userList- error");
        }
    });
}

function ajaxMoves() {
    $.ajax({
        type: "GET",
        url: "movetogame",
        contentType: "application/json;",
        dataType: 'json',
        data: {"updateMoves": "updateMoves"},
        success: function(moves) {
            updateMoves(moves);
        },
        error:function (error) {
            console.log("refreshing game Moves - error");
        }
    });
}

function ajaxCurrentPlayer() {
    $.ajax({
        type: "GET",
        url: "movetogame",
        contentType: "application/json;",
        dataType: 'json',
        data: {"updateCurrentPlayer": "updateCurrentPlayer"},
        success: function(currentPlayer) {
            updateCurrentPlayer(currentPlayer);
        },
        error:function (error) {
            console.log("refreshing CurrentPlayer - error");
        }
    });
}

function updateMoves(moves) {
    $('#movesArea').empty();
    $('#movesArea').append(moves.toString());
}

function updateTurn(turn) {
    // console.log("update turn to " + turn.toString());
    /*$('#turnArea').empty();
    $('#turnArea').append(turn.toString());
    if (turn.toString() == "<h4>Turn : 2 / 2</h4>"){
        isDoneTwoTurns = true;
    }*/
}

function updatePlayersList(players) {
    $('#userslist').empty();
    $.each(players || [], function(key, val) {
        $('#userslist').append(val);
    });
}

function updateCurrentPlayer(currentPlayer) {
    $('#currentPlayerArea').empty();
    $('#currentPlayerArea').append(currentPlayer.toString());
}

function ajaxPlayerActive() {
    $.ajax({
        url: "singlegame",
        type:'get',
        contentType: "application/json;",
        dataType: 'json',
        data: {"playerActive": "playerActive"},

        success: function(data) {
            if (data === true){
                isPlayerActive = true;
                playerIsActive();
            }
            else{
                isPlayerActive = false;
                playerIsNotActive();
            }
        },
        error: function(error) {
            console.log("player Active failed")
        }
    });
}

function playerIsActive() {
    $.ajax({
        url: "singlegame",
        type:'get',
        contentType: "application/json;",
        dataType: 'json',
        data: {"isHuman": "isHuman"},
        success: function(data) {
            if (data === true){
                HumanPlayerActive();
            }
            else{
                computerPlayerActive();
            }
        },
        error: function(error) {
            console.log("player Active failed")
        }
    });
}

function HumanPlayerActive() {
    document.getElementById("movesHuman").style.visibility="visible";
    document.getElementById("preformMove").style.visibility="visible";
    document.getElementById("preformMove").disabled=false;
    document.getElementById("logout").disabled=false;

    checkifHumanCanPlay();
}


function checkifHumanCanPlay() {
    $.ajax({
        url: "singlegame",
        type:'get',
        contentType: "application/json;",
        dataType: 'json',
        data: {"humanCanPlay": "humanCanPlay"},
        success: function(data) {
            if (data['error'] === "false"){
                alert(data['value']);
            }
            else{
            }
        },
        error: function(error) {
        }
    });
}

function computerPlayerActive() {
    document.getElementById("movesHuman").style.visibility="visible";
    document.getElementById("preformMove").disabled=true;
    document.getElementById("preformMove").style.visibility="visible";

    document.getElementById("logout").disabled=true;

    //$('#preformComputerMove').click();
    preformComputerMove();
}

function playerIsNotActive() {
    $.ajax({
        url: "singlegame",
        type:'get',
        contentType: "application/json;",
        dataType: 'json',
        data: {"isHuman": "isHuman"},
        success: function(data) {
            if (data === true){
                HumanPlayerNotActive();
            }
            else{
                computerPlayerNotActive();
            }
        },
        error: function(error) {
            console.log("player Active failed")
        }
    });
}

function HumanPlayerNotActive() {
    document.getElementById("movesHuman").style.visibility="visible";
    document.getElementById("preformMove").disabled=true;
    document.getElementById("preformMove").style.visibility="visible";

    $.ajax({
        url: "singlegame",
        type:'get',
        contentType: "application/json;",
        dataType: 'json',
        data: {"gameStarted": "gameStarted"},
        success: function(data) {
            if (data=== true){
                document.getElementById("logout").disabled=true;
            }
            else{
                document.getElementById("logout").disabled=false;
            }
        },
        error: function(error) {
        }
    });

    document.getElementById("logout").disabled=true;
}

function computerPlayerNotActive() {
    document.getElementById("movesHuman").style.visibility="visible";
    document.getElementById("preformMove").disabled=true;
    document.getElementById("preformMove").style.visibility="visible";

    $.ajax({
        url: "singlegame",
        type:'get',
        contentType: "application/json;",
        dataType: 'json',
        data: {"gameStarted": "gameStarted"},
        success: function(data) {
            if (data=== true){
                document.getElementById("logout").disabled=true;
            }
            else{
                document.getElementById("logout").disabled=false;
            }
        },
        error: function(error) {
        }
    })
}

function triggerAjaxPageContent() {
    setTimeout(ajaxPageContent, refreshRate);
}

function leaveGame() {
    console.log("Leave gameeeee:  ")
    $.ajax({
        url: "movetogame",
        type: "POST",
        data: {"leaveGame": "leaveGame"},
        success: function() {

        },
        error:function () {
            console.log("leave game error");
        }
    });
}

function ajaxCheckTechWin() {
    $.ajax({
        url: "singlegame",
        type: "GET",
        contentType: "application/json;",
        dataType: 'json',
        data: {"checkForTechWin": "checkForTechWin"},
        success: function(data) {
            if(data) {
                var answer = confirm("Congratulations!!! \nYou won the game :)");
                if(answer) {
                    document.location.href = "gamesRoom.html";

                }
                else{
                    document.location.href = "gamesRoom.html";

                }
            }
        },
        error:function () {
        }
    });
}


function userPerformMove() {
    console.log("userPerformMove noww");
   //$('#boardArea').empty();
    //var status = $('input[name=preformMoveRadio]:checked', '#FormOfRadioButton').val();
    $.ajax({
        url: "singlegame",
        type: "GET",
        //contentType: "application/json;",
        contentType: "text/html;",
        dataType: 'json',
        data: {"performMove": "performMove", "status": status},
        success: function(data) {
            console.log(data);
            if (data['error'] === "error"){
                alert(data['value']);
            }
            else {
                refreshBoardCells();
            }

        },
        error:function (e) {
            console.log("perform move error");
        }
    });
}

function preformComputerMove() {
    $.ajax({
        url: "singlegame",
        type: "GET",
        contentType: "text/html;",
        dataType: 'json',
        data: {"performComputerMove": "performComputerMove"},
        success: function() {
            console.log("success find games servlet in performComputerMove ");
            if (data['error'] === "false"){
                alert(data['value']);
            }
            refreshBoardCells();
            nextPlayer();
        },
        error:function () {
            console.log("perform performComputerMove error");
        }
    });
}

function refreshBoardCells() {
    $.ajax({
        url: "movetogame",
        type: "GET",
        contentType: "application/json;",
        dataType: 'json',
        data: {"refreshBoard": "refreshBoard"},
        success: function(newBoard) {

            $('#boardCells').empty();
            $('#boardCells').append(newBoard.toString());
            //$(".boardCell").on("click", myclick());
            setCellClickEvents();
            console.log("refresh board good!");
        },
        error:function () {
            console.log("refresh board failed error");
        }
    });
}

function nextPlayer() {
    console.log("NEXT noww");
    $(".selected").removeClass("selected");
    $.ajax({
        url: "singlegame",
        type: "GET",
        contentType: "application/json;",
        dataType: 'json',
        data: {"nextPlayer": "nextPlayer"},
        success: function() {
            console.log("success find games servlet in next player ");
},
        error:function () {
            console.log("next player error :   " + isDoneTwoTurns);
        }
    });
}

function ajaxWinnerExist() {
    $.ajax({
        url: "singlegame",
        type: "GET",
        contentType: "application/json;",
        dataType: 'json',
        data: {"winnerExist": "winnerExist"},
        success: function(data) {
            if (data['isFinished'] === "true"){
                console.log("game over");
                cleanGame();
                moveToGamesRoom(data['value'])
            }
            else {
                refreshBoardCells();
            }
        },
        error:function () {
            console.log("game over error");
        }
    });
}

function moveToGamesRoom(message) {
    var answer = confirm(message);
    if(answer) {
        document.location.href = "gamesRoom.html";
    }
    else{
        document.location.href = "gamesRoom.html";
    }
}

function cleanGame() {
    $.ajax({
        url: "singlegame",
        type: "GET",
        contentType: "application/json;",
        dataType: 'json',
        data: {"cleanGame": "cleanGame"},
        error:function () {
            console.log("perform cleanGame error");
        }
    });
}

function updateUserName() {
    $.ajax({
        type: "GET",
        url: "movetogame",
        contentType: "application/json;",
        dataType: 'json',
        data: {"updateUserName": "updateUserName"},
        success: function(userFromServlet) {
            userName = userFromServlet;
            console.log("set user name to " + userFromServlet.toString());
        },
        error:function (error) {
            console.log("error in setting userName");
        }
    });
}


function addPlayerToReplay() {
    $.ajax({
        type: "POST",
        url: "replaygame",
        data: {"addPlayerToReplay": "addPlayerToReplay"},
        success: function() {
            console.log("success to add player to replay");
        },
        error:function () {
            console.log("error in add player to replay");
        }
    });
}

function userSendMsg() {
    var msg = document.getElementById("userstring").value;

    $.ajax({
        url: "sendChat",
        type: "GET",
        contentType: "application/json;",
        dataType: 'json',
        data: {"sendMsg": "sendMsg", "userstring": msg},
        success:function() {
            //document.getElementById("userstring").val("");
            //$('userstring').val("");

        },
        error:function () {
            //$('userstring').val("");

            console.log("perform cleanGame error");
        }
    });
    $('#userstring').val("");
    //$('#userstring').attr("value", "");
}



window.onload = function(){
    //prevent IE from caching ajax calls
    $.ajaxSetup({cache: false});
    ajaxPlayerActive();
    setInterval (ajaxGamesList, refreshRate);//updates player list in current game
    setInterval (ajaxMoves, refreshRate);//updates number of moves
    setInterval (ajaxCurrentPlayer, refreshRate);//prints current player and that it
    setInterval (ajaxPlayerActive, refreshRate);//checks if player is active - enable/disable perform move button
    setInterval (ajaxCheckTechWin,refreshRate);
    setInterval(ajaxWinnerExist,refreshRate);
    setInterval(refreshBoardCells, refreshRate);

    setInterval(ajaxChatContent, refreshRate);

//    triggerAjaxPageContent();
    //$(".boardCell").on("click", chooseUnchooseCell);

    updateUserName();
    setCellClickEvents();
}

function setCellClickEvents(){

    var cells = document.getElementsByClassName('boardCell');
    for (var i = 0 ; i < cells.length ; i++){
        var cell = cells[i];
        cell.onmouseup = myclick;
    }
}

function myclick(event){

    var row = event.currentTarget.attributes['row'].value;
    var col = event.currentTarget.attributes['col'].value;
    var value = event.currentTarget.attributes['cellValue'].value;
    /*document.getElementById('form_row').value = row;
    document.getElementById('form_col').value = col;
    document.getElementById('form_rowcol_value').value = value;
    document.getElementById('button').value = event.button;
    document.forms['clickBoardCell'].submit();*/


    chooseUnchooseCell(event);
    var isSelected = $(event.target).attr('data-selected');

    $.ajax({
        type: "GET",
        url: "singlegame",
        contentType: "application/json;",
        dataType: "json",
        data: {"getCellData":"getCellData", "cellRow":row, "cellCol":col, "cellValue":value, "selected":isSelected},
        success: function() {

            console.log("success get cell value");
        },
        error:function () {
            console.log("error in get cell value");
        }
    });




}

function chooseUnchooseCell(event) {
    var isSelected = $(event.target).attr('data-selected');
    if (isPlayerActive) {
        if (isSelected == 'true') {
            $(event.target).attr('data-selected', 'false');
            $('.boardCell').attr('data-selected' , 'false');
        }
        else{
            $('.boardCell').attr('data-selected' , 'false');
            $(event.target).attr('data-selected', 'true');
        }
    }



    /*if (isPlayerActive) {
        if ($(event.target).hasClass('selected')) {
            $(event.target).removeClass('selected');

            $('.cellBoard').children.hasClass('selected').removeClass('selected');


        }
        else {
            $('.cellBoard').children.hasClass('selected').removeClass('selected');
            $(event.target).addClass('selected');
        }
    }*/
}


//entries = the added chat strings represented as a single string
function appendToChatArea(entries) {
//    $("#chatarea").children(".success").removeClass("success");

    // add the relevant entries
    $.each(entries || [], appendChatEntry);

    // handle the scroller to auto scroll to the end of the chat area
    var scroller = $("#chatarea");
    var height = scroller[0].scrollHeight - $(scroller).height();
    $(scroller).stop().animate({ scrollTop: height }, "slow");
}

function appendChatEntry(index, entry){
    var entryElement = createChatEntry(entry);
    $("#chatarea").append(entryElement).append("<br>");
}

function createChatEntry (entry){
    entry.chatString = entry.chatString.replace (":)", "<span class='smiley'></span>");
    return $("<span class=\"success\">").append(entry.username + "> " + entry.chatString);
}

//call the server and get the chat version
//we also send it the current chat version so in case there was a change
//in the chat content, we will get the new string as well
function ajaxChatContent() {
    $.ajax({
        url: "chat",
        type: "GET",
        contentType: "application/json;",
        data: "chatversion=" + chatVersion,
        dataType: 'json',
        success: function(data) {
            /*
             data is of the next form:
             {
             "entries": [
             {
             "chatString":"Hi",
             "username":"bbb",
             "time":1485548397514
             },
             {
             "chatString":"Hello",
             "username":"bbb",
             "time":1485548397514
             }
             ],
             "version":1
             }
             */
            console.log("Server chat version: " + data.version + ", Current chat version: " + chatVersion);
            if (data.version !== chatVersion) {
                chatVersion = data.version;
                appendToChatArea(data.entries);
            }
            triggerAjaxChatContent();
        },
        error: function(error) {
            triggerAjaxChatContent();
        }
    });
}



