package org.store.structure.repository;

import java.util.List;
import org.store.structure.model.Book;

public interface BookRepository {
    Book save(Book book);

    List findAll();
}
