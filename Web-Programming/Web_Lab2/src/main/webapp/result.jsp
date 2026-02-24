<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Результаты проверки | Веб-программирование</title>
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
            <table class="results-section">
                <tr>
                    <td>
                        <div class="results-header">
                            <h2>Результаты проверки</h2>
                        </div>

                        <div class="current-result">
                            <h3>Последняя проверка:</h3>
                            <table class="results-table">
                                <thead>
                                <tr>
                                    <th>Параметр</th>
                                    <th>Значение</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:if test="${not empty sessionScope.results}">
                                    <c:set var="lastResult" value="${sessionScope.results[0]}" />
                                    <tr>
                                        <td>Координата X</td>
                                        <td>${lastResult.x}</td>
                                    </tr>
                                    <tr>
                                        <td>Координата Y</td>
                                        <td>${lastResult.y}</td>
                                    </tr>
                                    <tr>
                                        <td>Радиус R</td>
                                        <td>${lastResult.r}</td>
                                    </tr>
                                    <tr>
                                        <td>Результат</td>
                                        <td class="${lastResult.hit ? 'hit' : 'miss'}">
                                                ${lastResult.hit ? 'Попадание' : 'Промах'}
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Время проверки</td>
                                        <td>${lastResult.timestamp}</td>
                                    </tr>
                                    <tr>
                                        <td>Время работы (нс)</td>
                                        <td>${lastResult.executionTime}</td>
                                    </tr>
                                </c:if>
                                </tbody>
                            </table>
                        </div>

                        <div class="results-container">
                            <h3>История проверок:</h3>
                            <div class="table-wrapper">
                                <table class="results-table history-table">
                                    <thead>
                                    <tr class="table-header-row">
                                        <th>X</th>
                                        <th>Y</th>
                                        <th>R</th>
                                        <th>Результат</th>
                                        <th>Время</th>
                                        <th>Время работы (нс)</th>
                                    </tr>
                                    </thead>
                                    <tbody id="results-body">
                                    <c:choose>
                                        <c:when test="${empty sessionScope.results}">
                                            <tr>
                                                <td colspan="6" style="text-align: center; padding: 20px;">
                                                    Нет данных о проверках
                                                </td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="point" items="${sessionScope.results}">
                                                <tr>
                                                    <td>${point.x}</td>
                                                    <td>${point.y}</td>
                                                    <td>${point.r}</td>
                                                    <td class="${point.hit ? 'hit' : 'miss'}">
                                                            ${point.hit ? 'Попадание' : 'Промах'}
                                                    </td>
                                                    <td>${point.timestamp}</td>
                                                    <td>${point.executionTime}</td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <div class="button-cell">
                            <a href="index.jsp" class="submit-button" style="text-decoration: none; display: inline-block;">
                                Вернуться на главную
                            </a>
                            <form action="controller" method="GET" style="display: inline; margin-left: 20px;">
                                <input type="hidden" name="clear" value="true">
                                <button type="submit" class="clear-button">
                                    Очистить историю
                                </button>
                            </form>
                        </div>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>