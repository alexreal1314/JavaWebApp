var chatVersion = 0;
var refreshRate = 2000; //mili seconds
var USER_LIST_URL = buildUrlWithContextPath("userslist");
var CHAT_LIST_URL = buildUrlWithContextPath("chat");

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



window.onload = function(){
    //prevent IE from caching ajax calls
    $.ajaxSetup({cache: false});

    //setInterval(ajaxChatContent, refreshRate);

//    triggerAjaxPageContent();
    //$(".boardCell").on("click", chooseUnchooseCell);

}