package web.servlets;

import web.models.PointResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AreaCheckServlet extends HttpServlet {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d{1,3})?");
    private static final Pattern INTEGER_PATTERN = Pattern.compile("-?\\d+");
    private static final Pattern R_PATTERN = Pattern.compile("[1-5]");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long startTime = System.nanoTime();

        try {
            String xStr = request.getParameter("x_graph");
            boolean fromGraph = false;

            if (xStr == null || xStr.isEmpty()) {
                xStr = request.getParameter("x");
                fromGraph = false;
            } else {
                fromGraph = true;
            }

            String yStr = request.getParameter("y");
            String rStr = request.getParameter("r");

            if (xStr == null || yStr == null || rStr == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Отсутствуют обязательные параметры");
                return;
            }

            if (xStr.isEmpty() || yStr.isEmpty() || rStr.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Параметры не могут быть пустыми");
                return;
            }

            if (!isValidNumber(xStr, fromGraph)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Неверный формат координаты X. " +
                                (fromGraph ? "Допустимы числа от -5 до 3 с точностью до 3 знаков" : "Допустимы целые числа от -5 до 3"));
                return;
            }

            if (!isValidNumber(yStr, true)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Неверный формат координаты Y. Допустимы числа от -3 до 5 с точностью до 3 знаков");
                return;
            }

            if (!isValidR(rStr)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Неверный формат радиуса R. Допустимы целые числа: 1, 2, 3, 4, 5");
                return;
            }

            double x = Double.parseDouble(xStr.replace(',', '.'));
            double y = Double.parseDouble(yStr.replace(',', '.'));
            double r = Double.parseDouble(rStr.replace(',', '.'));

            if (x < -5 || x > 3) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Координата X должна быть в диапазоне от -5 до 3");
                return;
            }

            if (y < -3 || y > 5) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Координата Y должна быть в диапазоне от -3 до 5");
                return;
            }

            if (r < 1 || r > 5) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Радиус R должен быть в диапазоне от 1 до 5");
                return;
            }

            if (r != Math.floor(r)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Радиус R должен быть целым числом");
                return;
            }

            if (fromGraph && !hasValidPrecision(xStr, 3)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Координата X не должна содержать более 3 знаков после запятой");
                return;
            }

            if (!hasValidPrecision(yStr, 3)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Координата Y не должна содержать более 3 знаков после запятой");
                return;
            }

            boolean hit = checkPointInArea(x, y, r);
            long executionTime = System.nanoTime() - startTime;

            PointResult result = new PointResult(x, y, r, hit, executionTime);

            saveResultToSession(request, result);

            HttpSession session = request.getSession();
            session.setAttribute("lastR", String.valueOf((int)r));

            request.getRequestDispatcher("/result.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверный числовой формат параметров");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    private boolean isValidNumber(String value, boolean allowDecimal) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        String normalizedValue = value.replace(',', '.');

        if (!NUMBER_PATTERN.matcher(normalizedValue).matches()) {
            return false;
        }

        if (!allowDecimal && !INTEGER_PATTERN.matcher(normalizedValue).matches()) {
            return false;
        }

        return true;
    }

    private boolean isValidR(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        return R_PATTERN.matcher(value).matches();
    }

    private boolean hasValidPrecision(String value, int maxDecimalPlaces) {
        if (value == null) {
            return false;
        }

        String normalizedValue = value.replace(',', '.');

        if (!normalizedValue.contains(".")) {
            return true;
        }

        String[] parts = normalizedValue.split("\\.");
        if (parts.length != 2) {
            return false;
        }

        return parts[1].length() <= maxDecimalPlaces;
    }

    private boolean checkPointInArea(double x, double y, double r) {
        boolean inQuarter1 = (x >= 0 && y >= 0) && (x <= r/2) && (y <= r/2) && (x + y <= r/2);
        boolean inQuarter2 = (x <= 0 && y >= 0) && (x * x + y * y <= (r/2) * (r/2));
        boolean inQuarter3 = false;
        boolean inQuarter4 = (x >= 0 && y <= 0) && (x <= r/2) && (y >= -r);

        return inQuarter1 || inQuarter2 || inQuarter3 || inQuarter4;
    }

    private void saveResultToSession(HttpServletRequest request, PointResult result) {
        HttpSession session = request.getSession();

        @SuppressWarnings("unchecked")
        List<PointResult> results = (List<PointResult>) session.getAttribute("results");

        if (results == null) {
            results = new ArrayList<>();
            session.setAttribute("results", results);
        }

        results.add(0, result);

        if (results.size() > 50) {
            results = new ArrayList<>(results.subList(0, 50));
            session.setAttribute("results", results);
        }
    }
}