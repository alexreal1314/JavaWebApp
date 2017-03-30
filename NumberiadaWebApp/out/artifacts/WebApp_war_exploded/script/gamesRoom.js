/**
 * Created by chenn on 04/10/2016.
 */

var refreshRate = 1500; //miliseconds
var userName;
function refreshUsersList(users) {
    //clear all current users
    $("#playersDetails").empty();
    var nameOfCurrentPlayer = localStorage.getItem(userName);

    $.each(users || [], function(name, type) {
        if(name === nameOfCurrentPlayer){
            $('#playersDetails').append('<tr bgcolor="greenyellow" ><td>' + name + '</td><td>' + type + '</td></tr>');

        }
        else {
            $('#playersDetails').append('<tr><td>' + name + '</td><td>' + type + '</td></tr>');
        }
    });
}

function refreshGamesList(games) {
    $("#gameDetails").empty();
    var index = 0;
    $.each(games || [], function() {
        $('#gameDetails').append('<tr class="gameRow">');
        var count = 0;
        var gameTitle;
        $.each(games[index] || [], function(key, val) {

            $('#gameDetails').append('<td class="gameDetails" id="' + val + '">' + val + '</td>');
            if (count === 0) {
                gameTitle = val;
            }
            count++;

        });

        $('#gameDetails').append('<td><input type="button" id="joinGame" class="joinGame" value="Join"></br></td>');
        $('#gameDetails').append('<td><input type="button" id="showBoard" class="showBoard" value="Show board"></br></td>');
        $('#gameDetails').append('<td><input type="button" id="removeGame" class="removeGame" value="Remove Game"></br></td>');
        $('#gameDetails').append('<td><input type="button" id="resetGame" class="resetGame" value="Reset Game"></br></td>');
        $('#gameDetails').append('</tr>');

        $('.joinGame').bind('click', {gameTitle: gameTitle},addUserToTheGame);
        $('.joinGame').removeClass('joinGame').addClass('unfollow');

        $('.showBoard').bind('click', {gameTitle: gameTitle},showBoard);
        $('.showBoard').removeClass('showBoard').addClass('unfollow');

        $('.removeGame').bind('click', {gameTitle: gameTitle},removeGame);
        $('.removeGame').removeClass('removeGame').addClass('unfollow');

        $('.resetGame').bind('click', {gameTitle: gameTitle},resetGame);
        $('.resetGame').removeClass('resetGame').addClass('unfollow');

        index++;

    });
}

function resetGame(event) {
    var gameTitle = event.data.gameTitle;
    $.ajax({
        type:'GET',
        url: "gameslist",
        contentType: "application/json;",
        dataType: 'json',
        data:{"resetGame":"resetGame", "gameTitle": gameTitle},
        success: function (data) {
            if (data['error'] === "notReset"){
                alert(data['value']);
            }
            else{
                alert(data['value']);
            }
        },
        error: function () {
            console.log("failed find showBoard servlet ");
        }
    });



}

function removeGame(event) {
    var gameTitle = event.data.gameTitle;
    $.ajax({
        type:'GET',
        url: "gameslist",
        contentType: "application/json;",
        dataType: 'json',
        data:{"removeGame":"removeGame", "gameTitle": gameTitle},
        success: function (data) {
            if (data['error'] === "notRemove"){
                alert(data['value']);
            }
            else{
            }
        },
        error: function () {
            console.log("failed find showBoard servlet ");
        }
    });
}

function showBoard(event) {
    var gameTitle = event.data.gameTitle;
    $.ajax({
        type:'POST',
        url: "gameslist",
        data:{"showBoard":"showBoard", "gameTitle": gameTitle},
        success: function () {
            console.log("find showBoard servlet");
            document.location.href = "showBoard.html";
        },
        error: function () {
            console.log("failed find showBoard servlet ");
        }
    });
}
function Logout() {
    var name = localStorage.getItem(userName);
    $.ajax({
        type:'GET',
        url: "backtologin",
        data:{"Logout":"Logout", "username": name},
        success: function () {
            console.log("find games servlet");

            $('#backLogin').click();
        },
        error: function () {
            console.log("failed find games servlet ");
        }
    });
}
function saveUserName(){

    var val = document.getElementById("userName").value;
    localStorage.setItem(userName, val);

}

function saveUserNameJSP() {
    var val = document.getElementById("userNameJSP").value;
    localStorage.setItem(userName, val);
}


function ajaxUsersList() {
    $.ajax({
        url: "userslist",
        success: function(users) {

            refreshUsersList(users);
        },
        error:function (error) {
            console.log("refreshing userList- error");
        }
    });
}

function ajaxGamesList() {
    $.ajax({
        url: "gameslist",
        success: function(games) {
            refreshGamesList(games);
        },

    });
}


function getUserName(){
    return localStorage.getItem(userName);
}



function addOrganizerToLastGame(){
    $.ajax({
        type:'post',
        url: "gameslist",
        data:{"organizer":getUserName()},
        success: function () {
            console.log("send " + getUserName());
            console.log("find games servlet");
        },
        error: function () {
            console.log("failed find games servlet ");
        }
    });
}


function addUserToTheGame(event) {
    var name = localStorage.getItem(userName);
    var gameTitle = event.data.gameTitle;
    console.log("in function:  " + gameTitle);

    $.ajax({
        type: "GET",
        url: "gameslist",
        contentType: "application/json;",
        dataType: 'json',
        data: {"addUserToTheGame": "addUserToTheGame", "userName": name, "gameTitle": gameTitle},
        success: function(data){
            console.log("success find games servlet in adding user ");
            if (data) {
                console.log(data);
                alert(data)
            }
            else {
                //moving form -> submit to next page
                $('#getGameTitle').value = gameTitle;
                localStorage.setItem("gameTitle",gameTitle);
                $('#moveToGame').click();
            }
        },
        error: function () {
            console.log("failed find games servlet in adding user ");
            //checkIfGameIsFullOfPlayers(gameTitle);
        }
    });

    $('.joinGame').one('click', addUserToTheGame);
    $('.showBoard').one('click', showBoard);
}



function addGame(){
    $('#formSubmit').click();

}



function ajaxPageContent() {
    $.ajax({
        url: "gameslist",
        type:'get',
        dataType: 'json',
        success: function(data) {
            // if (data.version !== gameVersion) {
            //     gameVersion = data.version;
            //     appendToChatArea(data.entries);
            // }
            triggerAjaxPageContent();
        },
        error: function(error) {
            triggerAjaxPageContent();
        }
    });
}


function triggerAjaxPageContent() {
    setTimeout(ajaxPageContent, refreshRate);
}

$(function() {
    //prevent IE from caching ajax calls
    $.ajaxSetup({cache: false});
    $("#formSubmit").click(function () {
        $('#formData').click();
    });
    //The users list is refreshed automatically every second
    document.getElementById("formData").style.visibility = "hidden";
    document.getElementById("moveToGame").style.visibility = "hidden";
    document.getElementById("backLogin").style.visibility = "hidden";
    document.getElementById("formSubmit").style.visibility = "hidden";
    setInterval(ajaxUsersList, refreshRate);
    setInterval (ajaxGamesList, refreshRate);
    //The chat content is refreshed only once (using a timeout) but
    //on each call it triggers another execution of itself later (1 second later)
    triggerAjaxPageContent();
});

window.onload = function() {
    ajaxUsersList();
    ajaxGamesList();
}
