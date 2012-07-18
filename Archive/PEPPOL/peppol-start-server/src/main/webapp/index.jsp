<%-- 
    Document   : index
    Created on : Sep 15, 2011, 6:55:39 PM
    Author     : Jose Gorvenia Narvaez(jose@alfa1lab.com)
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN""http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="css/web.css"/>
        <title>PEPPOL-START-SERVER</title>
    </head>
    <body>
        <div id="container">
            <div id="body">
                <div id="title">
                    <p>PEPPOL START SERVER - 2.1.0</p>
                </div>
                <hr></hr>
                <div id="description">
                    <p>Welcome to the START Access Point.</p>
                    <p>If you see this page means that the service is running and you can start to send and receive messages from other access point...</p>
                    <p>Server configuration file: <b>configServer.properties</b></p>
                    <p>Log configuration file: <b>log4j.xml</b></p>
                    <br></br>
                </div>
                <div id="ws">
                    <p><b>Web Service Interface</b></p>
                    <%
                        String url = request.getScheme() +"://"+ request.getServerName() +":"+
				request.getServerPort() + request.getContextPath() +"/";
                    %>
                    <a href="<%= url+"accesspointService"%>"><%= url+"accesspointService"%></a>
                </div>
            </div>
            <hr></hr>
            <div id="footer">
                <a href="">Documentation</a> | <a href="http://www.peppol.eu/about_peppol">About PEPPOL</a>
            </div>
        </div>
    </body>
</html>