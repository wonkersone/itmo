<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Лабораторная работа #2 | Веб-программирование</title>
    <script src="js/script.js" defer></script>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
<table class="header-table">
    <tr>
        <td colspan="2">
            <header class="main-header">
                <h1>Лабораторная работа №2 | Веб-программирование</h1>
                <div class="student-info">
                    <span>ФИО: Лейковский Никита Вячеславович</span>
                    <span>Группа: P3213</span>
                    <span>Вариант: 466497</span>
                </div>
            </header>
        </td>
    </tr>
</table>

<table class="main-layout">
    <tr>
        <td class="left-column">
            <form id="point-form" action="controller" method="POST">
                <table class="form-section">
                    <tr>
                        <td colspan="2">
                            <h2>Координаты точки</h2>
                        </td>
                    </tr>

                    <tr>
                        <td class="label-cell">
                            <label for="x-radio-group">Координата X:</label>
                        </td>
                        <td class="input-cell">
                            <div class="radio-group" id="x-radio-group">
                                <%
                                    String xParam = request.getParameter("x");
                                    String[] xValues = {"-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3"};
                                    for (String xVal : xValues) {
                                        String checked = xVal.equals(xParam) ? "checked" : "";
                                %>
                                <label class="radio-label">
                                    <input type="radio" name="x" value="<%= xVal %>" <%= checked %>>
                                    <span class="radio-custom"></span> <%= xVal %>
                                </label>
                                <% } %>
                            </div>
                            <input type="hidden" id="x-graph" name="x_graph" value="">
                            <span class="validation-hint" id="x-error"></span>
                        </td>
                    </tr>

                    <tr>
                        <td class="label-cell">
                            <label for="y-coord">Координата Y:</label>
                        </td>
                        <td class="input-cell">
                            <%
                                String yValue = request.getParameter("y");
                                if (yValue == null) yValue = "";
                            %>
                            <input type="text" id="y-coord" name="y" value="<%= yValue %>"
                                   placeholder="От -3 до 5"
                                   class="text-input"
                                   oninput="validateYPrecision(this)">
                            <span class="validation-hint" id="y-error"></span>
                        </td>
                    </tr>

                    <tr>
                        <td class="label-cell">
                            <label for="r-radio-group">Радиус R:</label>
                        </td>
                        <td class="input-cell">
                            <div class="radio-group" id="r-radio-group">
                                <%
                                    String rParam = request.getParameter("r");
                                    if (rParam == null && session.getAttribute("lastR") != null) {
                                        rParam = session.getAttribute("lastR").toString();
                                    }
                                    String[] rValues = {"1", "2", "3", "4", "5"};
                                    for (String rVal : rValues) {
                                        String checked = rVal.equals(rParam) ? "checked" : "";
                                %>
                                <label class="radio-label">
                                    <input type="radio" name="r" value="<%= rVal %>" <%= checked %>>
                                    <span class="radio-custom"></span> <%= rVal %>
                                </label>
                                <% } %>
                            </div>
                            <span class="validation-hint" id="r-error"></span>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="2" class="button-cell">
                            <button type="submit" id="submit-btn" class="submit-button">
                                Проверить точку
                            </button>
                        </td>
                    </tr>
                </table>
            </form>

            <table class="graph-section">
                <tr>
                    <td>
                        <h2>Область попадания</h2>
                        <div class="graph-container">
                            <canvas id="area-graph" width="400" height="400"></canvas>
                        </div>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<script>
    window.pointsHistory = [
        <c:forEach var="point" items="${sessionScope.results}" varStatus="loop">
        {
            x: ${point.x},
            y: ${point.y},
            r: ${point.r},
            hit: ${point.hit}
        }<c:if test="${not loop.last}">,</c:if>
        </c:forEach>
    ];

    function validateYPrecision(input) {
        let value = input.value;
        value = value.replace(',', '.');
        if (value.includes('.')) {
            let parts = value.split('.');
            if (parts[1] && parts[1].length > 3) {
                parts[1] = parts[1].substring(0, 3);
                input.value = parts[0] + '.' + parts[1];
            } else {
                input.value = value;
            }
        }
    }
</script>
</body>
</html>