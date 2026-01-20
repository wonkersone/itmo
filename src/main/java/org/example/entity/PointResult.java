package org.example.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "results")
public class PointResult implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double x;
    private double y;
    private double r;
    private boolean hit;
    private String checkDate;

    public PointResult() {}

    public PointResult(double x, double y, double r, boolean hit) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.checkDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }

    public double getR() {
        return r;
    }
    public void setR(double r) {
        this.r = r;
    }

    public boolean isHit() {
        return hit;
    }
    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public String getCheckDate() {
        return checkDate;
    }
    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }
}