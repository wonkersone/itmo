import com.fastcgi.FCGIInterface;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static final List<Map<String, Object>> resultsHistory = new ArrayList<>();

    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();
        while (fcgi.FCGIaccept() >= 0) {
            long startTime = System.nanoTime();

            try {
                String queryString = System.getProperty("QUERY_STRING");
                if (queryString == null || queryString.isEmpty()) {
                    sendErrorResponse("Missing parameters");
                    continue;
                }

                Map<String, String> params = parseQueryString(queryString);

                if (!params.containsKey("x") || !params.containsKey("y") || !params.containsKey("r")) {
                    sendErrorResponse("Missing required parameters: x, y, r");
                    continue;
                }
                double x = Double.parseDouble(params.get("x"));
                double y = Double.parseDouble(params.get("y"));
                double r = Double.parseDouble(params.get("r"));

                if (x < -5 || x > 3 || y < -3 || y > 5 || r < 1 || r > 4) {
                    sendErrorResponse("Parameters out of valid range");
                    continue;
                }
                boolean hit = checkPointInArea(x, y, r);

                long endTime = System.nanoTime();
                long execTime = endTime - startTime;

                Map<String, Object> result = new HashMap<>();
                result.put("x", x);
                result.put("y", y);
                result.put("r", r);
                result.put("hit", hit);
                result.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                result.put("execTime", execTime + " ns");
                resultsHistory.add(result);

                String jsonResponse = buildSuccessResponse(result);
                System.out.println("Content-type: application/json");
                System.out.println("Content-Length: " + jsonResponse.length());
                System.out.println();
                System.out.println(jsonResponse);

            } catch (Exception e) {
                long endTime = System.nanoTime();
                long execTime = endTime - startTime;
                sendErrorResponse("Server error: " + e.getMessage() + " (execution time: " + execTime + " ns)");
            }
        }
    }

    private static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return params;
        }

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                try {
                    String key = java.net.URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    String value = java.net.URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    params.put(key, value);
                } catch (Exception ignored) {

                }
            }
        }
        return params;
    }

    private static boolean checkPointInArea(double x, double y, double r) {
        if (x >= 0 && y >= 0 && (x * x + y * y <= r * r)) {
            return true;
        }
        if (x <= 0 && y <= 0 && x >= -r && y >= -r) {
            return true;
        }
        if (x >= 0 && y <= 0 && x <= r/2 && y >= -r && y >= -2 * x - r) {
            return true;
        }
        return false;
    }

    private static String buildSuccessResponse(Map<String, Object> result) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"x\":").append(result.get("x")).append(",");
        json.append("\"y\":").append(result.get("y")).append(",");
        json.append("\"r\":").append(result.get("r")).append(",");
        json.append("\"hit\":").append(result.get("hit")).append(",");
        json.append("\"currentTime\":\"").append(result.get("time")).append("\",");
        json.append("\"workTime\":\"").append(result.get("execTime")).append("\"");
        json.append("}");
        return json.toString();
    }

    private static void sendErrorResponse(String message) {
        String errorJson = "{\"error\":true,\"message\":\"" + message + "\"}";
        System.out.println("Status: 400 Bad Request");
        System.out.println("Content-type: application/json");
        System.out.println("Content-Length: " + errorJson.length());
        System.out.println();
        System.out.println(errorJson);
    }
}