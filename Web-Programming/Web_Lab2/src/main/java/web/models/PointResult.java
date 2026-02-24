package web.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PointResult {
    private double x;
    private double y;
    private double r;
    private boolean hit;
    private LocalDateTime timestamp;
    private long executionTime;

    public PointResult(double x, double y, double r, boolean hit, long executionTime) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.timestamp = LocalDateTime.now();
        this.executionTime = executionTime;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getR() { return r; }
    public boolean isHit() { return hit; }
    public String getTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    public long getExecutionTime() { return executionTime; }

    public String getFormattedExecutionTime() {
        return executionTime + " ns";
    }
}