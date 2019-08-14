<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ page import="org.mposolda.WebAuthnBean" %>
<%@ page session="false" %>

<html>
  <head>
    <title>Hello</title>
    <script src="my-script.jsp"></script>
  </head>

  <body>
    <h2>Hello World!</h2>

    <h3>Buttons</h3>
    <button onclick="test()">Test</button>
    <button onclick="webauthnRegister()">WebAuthn Register</button>
    <button onclick="webauthnLogin()">WebAuthn Login</button>

    <h3>output</h3>
    <pre style="background-color: #ddd; border: 1px solid #ccc; padding: 10px; word-wrap: break-word; white-space: pre-wrap;" id="output"></pre>

  </body>

</html>