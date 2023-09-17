package org.store.structure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.store.structure.exception.EntityNotFoundException;
import org.store.structure.model.Book;

@RequiredArgsConstructor
@Repository
public class BookRepositoryImpl implements BookRepository {
    private final EntityManagerFactory managerFactory;

    @Override
    public Book save(Book book) {
        EntityTransaction transaction = null;
        try (EntityManager manager = managerFactory.createEntityManager()) {
            transaction = manager.getTransaction();
            transaction.begin();
            manager.persist(book);
            transaction.commit();
            return book;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't insert book: "
                    + book.toString() + " into DB", e);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        try (EntityManager manager = managerFactory.createEntityManager()) {
            Book book = manager.find(Book.class, id);
            return Optional.ofNullable(book);
        } catch (Exception e) {
            throw new EntityNotFoundException("Can't get book by id: " + id, e);
        }
    }

    @Override
    public List findAll() {
        try (EntityManager manager = managerFactory.createEntityManager()) {
            return manager.createQuery("SELECT b FROM Book b", Book.class).getResultList();
        } catch (Exception e) {
            throw new EntityNotFoundException("Can't find all entities", e);
        }
    }
}
