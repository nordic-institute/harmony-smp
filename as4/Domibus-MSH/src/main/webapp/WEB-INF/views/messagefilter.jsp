<%--
  Created by IntelliJ IDEA.
  User: walcz01
  Date: 14.07.2015
  Time: 14:50
  To change this template use File | Settings | File Templates.
--%>
<%@page session="true" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="header.jsp"/>
    <script>
        // Formularfelder dynamisch hinzufügen

        function addBackendConnector(backendConnector) {
            var fieldset = document.createElement("FIELDSET");
            fieldset.setAttribute("id", backendConnector.replace(/ /g, ''));
            document.getElementById("backendConnectorList").appendChild(fieldset);

            var hiddenName = document.createElement("INPUT");
            hiddenName.setAttribute("type", "hidden");
            hiddenName.setAttribute("name", "backends");
            hiddenName.setAttribute("value", backendConnector);
            fieldset.appendChild(hiddenName);

            var legend = document.createElement("LEGEND");

            var moveUpButton = document.createElement("INPUT");
            moveUpButton.setAttribute("type", "button");
            moveUpButton.setAttribute("value", "↑");
            moveUpButton.setAttribute("onclick", "moveUp('" + backendConnector.replace(/ /g, '') + "');");
            legend.appendChild(moveUpButton);

            var moveDownButton = document.createElement("INPUT");
            moveDownButton.setAttribute("type", "button");
            moveDownButton.setAttribute("value", "↓");
            moveDownButton.setAttribute("onclick", "moveDown('" + backendConnector.replace(/ /g, '') + "');");
            legend.appendChild(moveDownButton);

            var legendText = document.createTextNode('Name: ' + backendConnector);
            legend.appendChild(legendText);
            fieldset.appendChild(legend);


            var addFilterButton = document.createElement("INPUT");
            addFilterButton.setAttribute("type", "button");
            addFilterButton.setAttribute("value", "+");
            addFilterButton.setAttribute("onclick", "addFilter('" + backendConnector.replace(/ /g, '') + "');");
            fieldset.appendChild(addFilterButton);


        }

        function addFilter(backendConnector) {
            var p = document.createElement("P");
            document.getElementById(backendConnector).appendChild(p);

            var choicebox = document.createElement("LABEL");
            choicebox.setAttribute("for", backendConnector + "selection");
            p.appendChild(choicebox);

            var sel = document.createElement("SELECT");
            sel.setAttribute("name", backendConnector + "filter");

            var alreadySelected = false;
            <c:forEach var="rc" items="${routingcriterias}">
            var option = document.createElement("OPTION");

            var text = document.createTextNode("${rc}");
            option.appendChild(text);

            var preSelected = false;
            var selections = document.getElementsByName(backendConnector + "filter");
            for (var i = 0; i < selections.length; i++) {
                var selection = selections.item(i);
                if (!selection.isEqualNode(option) && selection.options[selection.selectedIndex].text == "${rc}") {
                    preSelected = true;
                    break;
                }
            }
            option.selected = !preSelected;

            sel.appendChild(option);
            </c:forEach>

            choicebox.appendChild(sel);
            var input = document.createElement("INPUT");
            input.setAttribute("type", "text");
            input.setAttribute("pattern", "\\w+([:]\\w+)*");
            input.setAttribute("title", "Type in the filtering rule like: [Criteria]:[CriteriaType]. Combine with regular expression.");
            input.setAttribute("name", backendConnector + "selection");
            p.appendChild(input);

            var removeFilterButton = document.createElement("INPUT");
            removeFilterButton.setAttribute("type", "button");
            removeFilterButton.setAttribute("name", "remove")
            removeFilterButton.setAttribute("value", "-");
            removeFilterButton.onclick = function () {
                document.getElementById(backendConnector).removeChild(removeFilterButton.parentNode);
            };
            p.appendChild(removeFilterButton);
        }
        ;


        function moveUp(backendConnector) {
            document.getElementById("backendConnectorList").insertBefore(document.getElementById(backendConnector), document.getElementById(backendConnector).previousElementSibling);
        }

        function moveDown(backendConnector) {
            document.getElementById("backendConnectorList").insertBefore(document.getElementById(backendConnector).nextElementSibling, document.getElementById(backendConnector));
        }

    </script>
</head>
<body>
<h1>${title}</h1>
<script>window.onload = function () {
    <c:forEach var="bc" items="${backendConnectors}">
    addBackendConnector("${bc.backendName}");
    </c:forEach>
}</script>
<form method="post" action="messagefilter">
    <div id="backendConnectorList"></div>
    <input type="submit" value="save">
</form>
</body>
</html>
