/*
 * Autoren:     N.Dassler, P.Konopac
 * E-Mail:      dassler@hm.edu, konopac@hm.edu
 * Team:        schnitzel
 * Vorlesung:   Software Architektur
 * Dozent:      A.Boettcher
 */
package edu.hm.cs.schnitzel.daos;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import edu.hm.cs.schnitzel.entities.Book;
import edu.hm.cs.schnitzel.entities.Disc;

/**
 * HibernateDatabaseAccessObject.
 *
 * This implementation of DatabaseAccessObject uses Hibernate
 *
 * @author konopac
 */
public class HibernateDatabaseAccessObject implements DatabaseAccessObject {

    //Fields
    //--------------------------------------------------------------------------
    private final SessionFactory sessionFactory;

    //Methods Private
    //--------------------------------------------------------------------------
    /**
     * Create a new Hibernate SessionFactory.
     *
     * @return the SessionFactory
     */
    private SessionFactory createSessionFactory() {
        return new Configuration().configure().buildSessionFactory();
    }

//    /**
//     * Remove a book.
//     *
//     * @param isbn is the isbn number of the book to remove
//     * @return true
//     */
//    private boolean removeBook(final String isbn) {
//        DATABASE.getBooks().remove(getBook(isbn));
//        return true;
//    }
//
//    /**
//     * Remove a disc.
//     *
//     * @param barcode is the barcode of the disc to remove
//     * @return true
//     */
//    private boolean removeDisc(final String barcode) {
//        DATABASE.getDiscs().remove(getDisc(barcode));
//        return true;
//    }

    /**
     * Insert an object to the database.
     *
     * @param toAdd is the object to be added
     * @return true, if it was successful
     */
    private boolean insert(final Object toAdd) {
        boolean done = true;
        try (final Session entityManager = getSessionFactory().getCurrentSession()) {
            final Transaction transaction = entityManager.beginTransaction();
            entityManager.persist(toAdd);
            transaction.commit();
        } catch (final Exception exception) {
            done = false;
            exception.printStackTrace();
        }
        return done;
    }

    /**
     * Update an object in the database.
     *
     * @param toUpdate is the object to be updated.
     * @return true, if it was successful.
     */
    private boolean update(final Object toUpdate) {
        boolean done = true;
        try (final Session entityManager =
                getSessionFactory().getCurrentSession()) {
            final Transaction transaction = entityManager.beginTransaction();
            //TODO: check behaviour of merge
            entityManager.merge(toUpdate);
            transaction.commit();
        } catch (final Exception exception) {
            done = false;
            exception.printStackTrace();
        }
        return done;
    }

    // Constructors
    //--------------------------------------------------------------------------
    /**
     * Default Constructor. Will create a new SessionFactory
     */
    public HibernateDatabaseAccessObject() {
        this.sessionFactory = createSessionFactory();
    }

    //Methods Public
    //--------------------------------------------------------------------------
    @Override
    public final boolean addBook(final Book toAdd) {
        return insert(toAdd);
    }

    @Override
    public final boolean addDisc(final Disc toAdd) {
        return insert(toAdd);
    }

    @Override
    public final List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        try (final Session entityManager = getSessionFactory().getCurrentSession()) {
            final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<Book> criteriaQuery = builder.createQuery(Book.class);

            criteriaQuery.from(Book.class);
//    		final Root<Book> root = criteriaQuery.from(Book.class);
//    		query.where(builder.equal(root.get("firstName"), "Neville"));
            final Query<Book> query = entityManager.createQuery(criteriaQuery);
            books = query.getResultList();

//    		final String queryString = "FROM Book";
//    		final Query<Book> query = entityManager.createQuery(queryString);
//    		books = query.list();
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
        return books;
    }

    @Override
    public final List<Disc> getDiscs() {
        List<Disc> discs = new ArrayList<>();
        try (final Session entityManager = getSessionFactory().getCurrentSession()) {
            final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<Disc> criteriaQuery = builder.createQuery(Disc.class);

            criteriaQuery.from(Disc.class);
            final Query<Disc> query = entityManager.createQuery(criteriaQuery);
            discs = query.getResultList();
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
        return discs;
    }

    @Override
    public final boolean updateBook(final Book toUpdate) {
        return update(toUpdate);
    }

    @Override
    public final boolean updateDisc(final Disc toUpdate) {
        return update(toUpdate);
    }

    @Override
    public final Book getBook(final String isbn) {
        Book result = null;
        for (final Book book : getBooks()) {
            if (book.getIsbn().equals(isbn)) {
                result = book;
            }
        }
        return result;
    }

    @Override
    public final Disc getDisc(final String barcode) {
        Disc result = null;
        for (final Disc disc : getDiscs()) {
            if (disc.getBarcode().equals(barcode)) {
                result = disc;
            }
        }
        return result;
    }

    //Private Getters
    //--------------------------------------------------------------------------
    /**
     * Getter for sessionFactory.
     *
     * @return the sessionFactory
     */
    private SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
