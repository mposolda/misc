<%@ page language="java" contentType="text/javascript; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ page import="org.mposolda.WebAuthnBean" %>
<%@ page session="false" %>

    function test() {
        var testOutput = "<%= WebAuthnBean.test() %>";
        output(testOutput);
    }

    function webauthnRegister() {
        output('webauthnRegister called');
    }

    function webauthnLogin() {
        output('webauthnLogin called');
    }

    function output(event) {
        var e = document.getElementById('output').innerHTML;
        document.getElementById('output').innerHTML = new Date().toLocaleString() + "\t" + event + "\n" + e;
    }

