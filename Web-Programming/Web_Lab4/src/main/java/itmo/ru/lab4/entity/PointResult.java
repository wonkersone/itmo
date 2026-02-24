package itmo.ru.lab4.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "results")
public class PointResult implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double x;

    @Column(nullable = false)
    private Double y;

    @Column(nullable = false)
    private Double r;

    @Column(nullable = false)
    private Boolean hit;

    @Column(name = "check_time", nullable = false)
    private LocalDateTime checkTime;

    @Column(nullable = false)
    private String owner;

    public PointResult() {
        this.checkTime = LocalDateTime.now();
    }

    public PointResult(Double x, Double y, Double r, Boolean hit, String owner) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.owner = owner;
        this.checkTime = LocalDateTime.now(); // Установка текущего времени при создании
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }

    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }

    public Double getR() { return r; }
    public void setR(Double r) { this.r = r; }

    public Boolean getHit() { return hit; }
    public void setHit(Boolean hit) { this.hit = hit; }

    public LocalDateTime getCheckTime() { return checkTime; }
    public void setCheckTime(LocalDateTime checkTime) { this.checkTime = checkTime; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
}