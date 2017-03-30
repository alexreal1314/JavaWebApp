

function updateGameBoard() {
    $.ajax({
        type: "GET",
        url: "movetogame",
        contentType: "application/json;",
        dataType: 'json',
        data: {"getGameBoard": "getGameBoard"},
        success: function(gameBoard) {
            console.log("success to find game board");
            $('#boardCells').empty();
            $('#boardCells').append(gameBoard.toString());
        },
        error:function (error) {
            console.log("error in find game Board");
        }
    });
}

function backToGamesRoom() {
    $.ajax({
        type: "POST",
        url: "movetogame",
        data: {"removeUserFromShowBoard": "removeUserFromShowBoard"},
        success: function() {
            document.location.href = "gamesRoom.html";
            console.log("move to lobby room after success");
        },
        error:function () {
            document.location.href = "gamesRoom.html";
            console.log("move to lobby room after failed");
        }
    });
}



window.onload = function(){
    updateGameBoard();

}
