

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@page import="Utils.*, Constants.*" %>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel='stylesheet' type='text/css' href='style/index.css' />
  <title>Numberiada</title>
  <!--        Link the Bootstrap (from twitter) CSS framework in order to use its classes-->
 <!-- <link rel="stylesheet" href="css/bootstrap.css"></link> todo -->
  <!--        Link jQuery JavaScript library in order to use the $ (jQuery) method-->
  <script src="script/jquery-2.0.3.min.js"></script>
  <script src="script/gamesRoom.js"></script>
</head>
<body>
<<div class="LoginPage" id ='LoginPage'>
  <div class="titlesArea" id="titlesArea">
    <div class="welcomeArea" id="welcomeArea">
      <h1>Welcome to Numberiada</h1>
    </div>
    <div class="loginTitleArea" id="loginTitleArea">
      <h2>Login Page</h2>
    </div>
  </div>
  <div class="login">
    <form method="GET" action="login">
      <div class="userNameArea" id="userNameArea">
        <p>user:   <input type="text" name="username" class="" id="userName" /></p>
      </div>
      <div class='playerTypeArea' id='playerTypeArea'>
        <p>player type:  <br/> <br/>
          <input type="radio" name="usertype" value="Human" checked> Human
          <input type="radio" name="usertype" value="Computer"> Computer
        </p>
      </div>
      <% Object errorMessage = request.getAttribute(Constants.USER_NAME_ERROR);%>
      <% if (errorMessage != null) {%>
      <span class="label important"><%=errorMessage%></span>
      <% } %>
      <!-- <button id="logIn" value="Login" onclick="saveUserName()">Login</button>
      <!-- <input type="submit" id="logIn" value="Login" onClick="saveUserName()"/>  -->
      <div class="loginArea" id="loginArea">
        <button id="logIn" value="Login" onclick="saveUserName()">Login</button>
      </div>
    </form>

  </div>

</div>
</body>
</html>