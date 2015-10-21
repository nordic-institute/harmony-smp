<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
        <title>CIPA BDMSL</title>
    </head>
    <style>
        pre {
            margin-top: 0px;
            margin-bottom: 0px;
        }
    </style>
    <body>
        <h1>ListDNS</h1>
        <c:choose>
            <c:when test="${dnsEnabled}">
                <ul>
                    <li>DNS server : ${dnsServer}</li>
                </ul>
                <ul>
                    <li>Number of records : ${numberOfRecords}</li>
                </ul>
                <c:forEach items="${recordMap.keySet()}" var="domain">
                    <h2>Domain: ${domain}</h2>
                    <c:forEach items="${recordMap.get(domain)}" var="record">
                        <pre>${record}</pre>
                    </c:forEach>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <ul>
                    <li>The DNS client is disabled.</li>
                </ul>
            </c:otherwise>
        </c:choose>
    </body>
</html>
