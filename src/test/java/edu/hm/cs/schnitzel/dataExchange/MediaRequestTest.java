/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.hm.cs.schnitzel.dataExchange;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 *
 * @author nicfel
 */
public class MediaRequestTest {

    /**
     * The injector which contains bindings specified in GuiceRequestTestModule.
     */
    private static final Injector INJECTOR
            = Guice.createInjector(new GuiceRequestTestModule());
    /**
     * The mediaRequest object all test methods will work with.
     */
    private Request mediaRequest;

    /**
     * Create a new instance of mediaRequest b4 every test. This will also
     * trigger the injection in mediaRequest class (e.g. Service).
     */
    @Before
    public final void setUp() {
        mediaRequest = INJECTOR.getInstance(Request.class);
    }

    @Test
    public void uriDoesntContainBooksOrDiscs() {
        final String expected = new MediaResult(404,
                "Not found. The requested resource does not exist. Make sure "
                + "to use the correct URI pattern. The pattern is as "
                + "follows: /shareit/media/[books or discs]"
                + "/{isbn or barcode}",
                Collections.emptyList()).getJsonString();
        final String have = getMediaRequest()
                .processRequest("GET", "/shareit/media/ape/1234", null, null)
                .getJsonString();
        assertEquals(expected, have);
    }

    @Test
    public void correctGetRequestForOneBook() {
        final String expected = new MediaResult(200,
                "OK.",
                Collections.emptyList()).getJsonString();
        final String have = getMediaRequest()
                .processRequest("GET", "/shareit/media/books/1234", null, null)
                .getJsonString();
        System.out.println(have);
        assertEquals(expected, have);
    }

    @Test
    public void correctGetRequestForAllBooks() {
        final String expected = new MediaResult(201,
                "OK.",
                Collections.emptyList()).getJsonString();
        final String have = getMediaRequest()
                .processRequest("GET", "/shareit/media/books", null, null)
                .getJsonString();
        System.out.println(have);
        assertEquals(expected, have);
    }

    private Request getMediaRequest() {
        return mediaRequest;
    }

}
