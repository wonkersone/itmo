package org.example;




import org.example.entity.PointResult;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Named("pointBean")
@SessionScoped
public class PointBean implements Serializable {

    @PersistenceContext(unitName = "Unit")
    private EntityManager em;

    private double x = 0.0;
    private double y = 0.0;
    private double r = 1.0; // Дефолтное значение
    private List<PointResult> results;

    @PostConstruct
    public void init() {
        loadResults();
    }

    private void loadResults() {
        if (em != null) {

            results = em.createQuery("SELECT p FROM PointResult p ORDER BY p.id DESC", PointResult.class)
                    .getResultList();
        }
    }


    @Transactional
    public void checkPoint() {
        boolean isHit = checkArea(x, y, r);
        PointResult res = new PointResult(x, y, r, isHit);

        if (em != null) {
            em.persist(res);

            results.add(0, res);
        }
    }
    @Transactional
    public void clearResults() {
        if (em != null) {
            em.createQuery("DELETE FROM PointResult").executeUpdate();
            results.clear();
        }
    }


    public void setR(double r) {
        this.r = r;
    }


    private boolean checkArea(double x, double y, double r) {

        if (x >= 0 && y >= 0) {
            return (x * x + y * y) <= (r * r);
        }

        if (x < 0 && y >= 0) {
            return false;
        }

        if (x <= 0 && y <= 0) {
            return (x >= -r / 2.0) && (y >= -r);
        }

        if (x > 0 && y < 0) {
            return y >= (x - r / 2.0);
        }
        return false;
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

    public List<PointResult> getResults() {
        return results;
    }
}