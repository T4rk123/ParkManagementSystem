package com.amusementpark.repositories;

import com.amusementpark.config.HibernateConfig;
import com.amusementpark.models.Attraction;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class AttractionRepository {
    public List<Attraction> findAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("FROM Attraction", Attraction.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка получения аттракционов: " + e.getMessage());
        }
    }

    public List<Attraction> searchByName(String name) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {

            Query<Attraction> query = session.createQuery("FROM Attraction WHERE lower(name) LIKE lower(:name)", Attraction.class);
            query.setParameter("name", "%" + name + "%");
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка поиска: " + e.getMessage());
        }
    }

    public List<Attraction> findSorted(String sortBy, boolean ascending) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            String order = ascending ? "ASC" : "DESC";
            String hql = "FROM Attraction ORDER BY " + sortBy + " " + order;
            return session.createQuery(hql, Attraction.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сортировки: " + e.getMessage());
        }
    }

    public void save(Attraction attraction) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(attraction);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сохранения аттракциона: " + e.getMessage());
        }
    }

    public void delete(Long id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            session.beginTransaction();
            Attraction attr = session.get(Attraction.class, id);
            if (attr != null) {
                session.delete(attr);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка удаления: " + e.getMessage());
        }
    }

    public Attraction findById(Long id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.get(Attraction.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка получения аттракциона: " + e.getMessage());
        }
    }

    public Double averageWaitingTime() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Query<Double> query = session.createQuery("SELECT AVG(a.waitingTime) FROM Attraction a", Double.class);
            return query.uniqueResult() != null ? query.uniqueResult() : 0.0;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка расчета среднего времени: " + e.getMessage());
        }
    }
}