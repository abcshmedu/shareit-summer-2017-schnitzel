package edu.hm.cs.schnitzel.services;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import edu.hm.cs.schnitzel.dataExchange.Result;
import edu.hm.cs.schnitzel.entities.Book;
import edu.hm.cs.schnitzel.entities.Disc;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nicfel
 */
public class MediaServiceTest {

    /**
     * The injector which contains bindings specified in GuiceServiceTestModule.
     */
    private static final Injector INJECTOR
            = Guice.createInjector(new GuiceServiceTestModule());
    @Inject
    private Service service;

    public MediaServiceTest() {
        INJECTOR.injectMembers(this);
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private Service getService() {
        return service;
    }

    /**
     * Test of addBook method, added book is null.
     */
    @Test
    public void testAddedBookIsNull() {
        final String expected = "{\"Message\":\"Policy Not Fulfilled."
                + " The book and its isbn-number/author/title"
                + " must not be null or empty!\""
                + ",\"Resources\":{\"Discs\":[],\"Books\":[]},"
                + "\"Code\":420}";
        final String have = getService().addBook(null).getJsonString();
        assertEquals(expected, have);
    }

    /**
     * Test of addBook method, added book has empty ISBN.
     */
    @Test
    public void testAddedBookHasEmptyISBN() {
        final String expected = "{\"Message\":\"Policy Not Fulfilled."
                + " The book and its isbn-number/author/title"
                + " must not be null or empty!\""
                + ",\"Resources\":{\"Discs\":[],\"Books\":[]},"
                + "\"Code\":420}";
        final String have = getService()
                .addBook(new Book("test", "", 0, "test")).getJsonString();
        assertEquals(expected, have);
    }

    /**
     * Test of addBook method, added book has null author.
     */
    @Test
    public void testAddedBookHasNullAuthor() {
        final String expected = "{\"Message\":\"Policy Not Fulfilled."
                + " The book and its isbn-number/author/title"
                + " must not be null or empty!\""
                + ",\"Resources\":{\"Discs\":[],\"Books\":[]},"
                + "\"Code\":420}";
        final String have = getService()
                .addBook(new Book(null, "test", 0, "test")).getJsonString();
        assertEquals(expected, have);
    }

    /**
     * Test of addBook method, added book has empty title.
     */
    @Test
    public void testAddedBookHasEmptyTitle() {
        final String expected = "{\"Message\":\"Policy Not Fulfilled."
                + " The book and its isbn-number/author/title"
                + " must not be null or empty!\""
                + ",\"Resources\":{\"Discs\":[],\"Books\":[]},"
                + "\"Code\":420}";
        final String have = getService()
                .addBook(new Book("test", "test", 0, "")).getJsonString();
        assertEquals(expected, have);
    }



    /**
     * Test of addDisc method, of class MediaService.
     */
    @Test
    public void testAddDisc() {
        System.out.println("addDisc");
        Disc toAdd = null;
        MediaService instance = new MediaService();
        Result expResult = null;
        Result result = instance.addDisc(toAdd);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBooks method, of class MediaService.
     */
    @Test
    public void testGetBooks() {
        System.out.println("getBooks");
        MediaService instance = new MediaService();
        Result expResult = null;
        Result result = instance.getBooks();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDiscs method, of class MediaService.
     */
    @Test
    public void testGetDiscs() {
        System.out.println("getDiscs");
        MediaService instance = new MediaService();
        Result expResult = null;
        Result result = instance.getDiscs();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateBook method, of class MediaService.
     */
    @Test
    public void testUpdateBook() {
        System.out.println("updateBook");
        Book toUpdate = null;
        MediaService instance = new MediaService();
        Result expResult = null;
        Result result = instance.updateBook(toUpdate);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateDisc method, of class MediaService.
     */
    @Test
    public void testUpdateDisc() {
        System.out.println("updateDisc");
        Disc toUpdate = null;
        MediaService instance = new MediaService();
        Result expResult = null;
        Result result = instance.updateDisc(toUpdate);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBook method, of class MediaService.
     */
    @Test
    public void testGetBook() {
        System.out.println("getBook");
        String isbn = "";
        MediaService instance = new MediaService();
        Result expResult = null;
        Result result = instance.getBook(isbn);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDisc method, of class MediaService.
     */
    @Test
    public void testGetDisc() {
        System.out.println("getDisc");
        String barcode = "";
        MediaService instance = new MediaService();
        Result expResult = null;
        Result result = instance.getDisc(barcode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
