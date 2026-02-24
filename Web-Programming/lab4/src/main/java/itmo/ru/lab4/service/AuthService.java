package itmo.ru.lab4.service;

import itmo.ru.lab4.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Stateless
public class AuthService {
    @PersistenceContext
    private EntityManager em;

    private static final String SECRET_KEY = "Lab4_Secret_Salt_Key";

    public boolean register(String login, String password) {
        if (em.find(User.class, login) != null) return false;
        em.persist(new User(login, hashPassword(password)));
        return true;
    }


    public String login(String login, String password) {
        User user = em.find(User.class, login);
        if (user != null && user.getPassword().equals(hashPassword(password))) {
            return generateToken(login);
        }
        return null;
    }


    public String generateToken(String login) {
        String signature = hashPassword(login + SECRET_KEY);
        String tokenRaw = login + "." + signature;
        return Base64.getEncoder().encodeToString(tokenRaw.getBytes(StandardCharsets.UTF_8));
    }


    public String validateToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split("\\.");
            if (parts.length != 2) return null;

            String login = parts[0];
            String signature = parts[1];

            if (signature.equals(hashPassword(login + SECRET_KEY))) {
                return login;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return password;
        }
    }
}