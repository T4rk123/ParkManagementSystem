package com.amusementpark.repositories;

import com.amusementpark.config.HibernateConfig;
import com.amusementpark.models.User;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import java.sql.SQLException;

public class UserRepository {
    public User findByUsername(String username) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        } catch (Exception e) {

            throw new RuntimeException("Ошибка поиска пользователя: " + e.getMessage());
        }
    }

    public void save(User user) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(user);
            session.getTransaction().commit();
        } catch (ConstraintViolationException e) {

            throw new RuntimeException("Логин уже существует!");
        } catch (Exception e) {
            if (e.getCause() instanceof SQLException && ((SQLException) e.getCause()).getSQLState().equals("23505")) {

                throw new RuntimeException("Логин уже существует!");
            }
            throw new RuntimeException("Ошибка сохранения пользователя: " + e.getMessage());
        }
    }

    public Long countAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM User", Long.class);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка подсчета пользователей: " + e.getMessage());
        }
    }
}