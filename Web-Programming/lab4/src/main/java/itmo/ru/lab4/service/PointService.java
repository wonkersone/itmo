package itmo.ru.lab4.service;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import itmo.ru.lab4.entity.PointResult;
import java.util.List;

@Stateless
public class PointService {
    @PersistenceContext
    private EntityManager em;

    public void savePoint(PointResult point) {
        em.persist(point);
        em.flush();
    }

    public List<PointResult> getPointsByOwner(String owner) {
        return em.createQuery("SELECT p FROM PointResult p WHERE p.owner = :owner", PointResult.class)
                .setParameter("owner", owner)
                .getResultList();
    }

    @jakarta.ejb.TransactionAttribute(jakarta.ejb.TransactionAttributeType.REQUIRED)
    public void deletePointsByOwner(String owner) {
        em.createQuery("DELETE FROM PointResult p WHERE p.owner = :owner")
                .setParameter("owner", owner)
                .executeUpdate();
        em.flush();
    }
}