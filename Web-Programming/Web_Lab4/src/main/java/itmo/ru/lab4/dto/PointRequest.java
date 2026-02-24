package itmo.ru.lab4.dto;

public class PointRequest {
    private Double x;
    private Double y;
    private Double r;
    private String owner;
    public PointRequest() {}

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }
    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }
    public Double getR() { return r; }
    public void setR(Double r) { this.r = r; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
}