package web.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.regex.Pattern;

public class ControllerServlet extends HttpServlet {

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
        if (request.getParameter("clear") != null) {
            clearResults(request);
            request.getRequestDispatcher("/result.jsp").forward(request, response);
            return;
        }

        String xParam = request.getParameter("x");
        String xGraphParam = request.getParameter("x_graph");
        String yParam = request.getParameter("y");
        String rParam = request.getParameter("r");

        boolean hasX = (xParam != null && !xParam.isEmpty()) || (xGraphParam != null && !xGraphParam.isEmpty());
        boolean hasY = (yParam != null && !yParam.isEmpty());
        boolean hasR = (rParam != null && !rParam.isEmpty());

        if (!hasX || !hasY || !hasR) {
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        if (!validateParameters(xParam, xGraphParam, yParam, rParam)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверные параметры запроса");
            return;
        }

        request.getRequestDispatcher("/checkArea").forward(request, response);
    }

    private boolean validateParameters(String xParam, String xGraphParam, String yParam, String rParam) {
        try {
            if (rParam != null && !rParam.isEmpty()) {
                if (!R_PATTERN.matcher(rParam).matches()) {
                    return false;
                }
            }

            if (yParam != null && !yParam.isEmpty()) {
                String normalizedY = yParam.replace(',', '.');
                if (!NUMBER_PATTERN.matcher(normalizedY).matches()) {
                    return false;
                }

                if (normalizedY.contains(".") && normalizedY.split("\\.")[1].length() > 3) {
                    return false;
                }

                double y = Double.parseDouble(normalizedY);
                if (y < -3 || y > 5) {
                    return false;
                }
            }

            if (xParam != null && !xParam.isEmpty()) {
                if (!INTEGER_PATTERN.matcher(xParam).matches()) {
                    return false;
                }

                int x = Integer.parseInt(xParam);
                if (x < -5 || x > 3) {
                    return false;
                }
            }

            if (xGraphParam != null && !xGraphParam.isEmpty()) {
                String normalizedX = xGraphParam.replace(',', '.');
                if (!NUMBER_PATTERN.matcher(normalizedX).matches()) {
                    return false;
                }

                if (normalizedX.contains(".") && normalizedX.split("\\.")[1].length() > 3) {
                    return false;
                }

                double x = Double.parseDouble(normalizedX);
                if (x < -5 || x > 3) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void clearResults(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("results");
    }
}