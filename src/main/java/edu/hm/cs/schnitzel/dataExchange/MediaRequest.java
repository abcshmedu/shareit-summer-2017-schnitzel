/*
 * Autoren:     N.Dassler, P.Konopac
 * E-Mail:      dassler@hm.edu, konopac@hm.edu
 * Team:        schnitzel
 * Vorlesung:   Software Architektur
 * Dozent:      A.Boettcher
 */
package edu.hm.cs.schnitzel.dataExchange;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import edu.hm.cs.schnitzel.entities.Book;
import edu.hm.cs.schnitzel.entities.Disc;
import edu.hm.cs.schnitzel.services.Service;
import java.io.InputStream;

/**
 *
 * @author nicfel
 */
public class MediaRequest implements Request {

    //Constants
    //--------------------------------------------------------------------------
    private static final int INDEX_RESOURCE_TYPE = 3;
    private static final int INDEX_ISBN = 4;
    //Object Variables
    //--------------------------------------------------------------------------
    private String method;
    private String requestURI;
    private String token;
    private InputStream inputStream;

    @Inject
    private Service service;
    @Inject
    private Authorization authorization;
    //Constructors
    //--------------------------------------------------------------------------

    /**
     * Default C-tor.
     */
    public MediaRequest() {
    }

    //Methods Private
    //--------------------------------------------------------------------------
    /**
     * A small help method that chooses which action will performed with a book
     * with the Service.
     *
     * @return Returns the result coming from the performed action.
     * @throws IOException Exception must be handled to give the user a status
     * report on what happened.
     */
    private Result delegateBookAction() throws IOException {
        //the result object which will be returned
        final Result result;
        //the underlying service that will actually execute the desired action
        //the jackson mapper to create book objects
        final ObjectMapper mapper = new ObjectMapper();
        final String[] splittedURI = getRequestURI().split("/");
        switch (getMethod()) {
            case "GET":
                //check if isbn is in url
                if (splittedURI.length == INDEX_ISBN + 1) {
                    //request only for one book
                    result = getService().getBook(splittedURI[INDEX_ISBN]);
                } else {
                    //request for all books
                    result = getService().getBooks();
                }
                break;
            case "PUT":
                if (splittedURI.length < (INDEX_ISBN + 1)) {
                    result = new MediaResult(HttpServletResponse.SC_BAD_REQUEST,
                            "Bad Request. The isbn-number must not be emty!",
                            Collections.emptyList());
                } else {
                    //update a book which will be specified with a book object
                    final Book book = mapper.readValue(getInputStream(),
                            Book.class);
                    book.setIsbn(splittedURI[INDEX_ISBN].replaceAll("-", ""));
                    result = getService().updateBook(book);
                }
                break;
            case "POST":
                //add a book which will be specified with a book object
                result = getService().addBook(mapper.readValue(getInputStream(),
                        Book.class));
                break;
            default:
                //send an error answer
                result = new MediaResult(HttpServletResponse.SC_BAD_REQUEST,
                        "Bad Request. The used http method is not supported for"
                        + "this REST service.", Collections.emptyList());
                break;
        }
        return result;
    }

    /**
     * A small help method that chooses which action will performed with a disc
     * with the Service.
     *
     * @return Returns the result coming from the performed action.
     * @throws IOException Exception must be handled to give the user a status
     * report on what happened.
     */
    private Result delegateDiscAction() throws IOException {
        //the result object which will be returned
        final Result result;
        //the underlying service that will actually execute the desired action
        //the jackson mapper to create book objects
        final ObjectMapper mapper = new ObjectMapper();
        final String[] splittedURI = getRequestURI().split("/");
        switch (getMethod()) {
            case "GET":
                //check for isbn in uri
                if (splittedURI.length == INDEX_ISBN + 1) {
                    //return just the requested book
                    result = getService().getDisc(splittedURI[INDEX_ISBN]);
                } else {
                    //return all discs
                    result = getService().getDiscs();
                }
                break;
            case "PUT":
                if (splittedURI.length < (INDEX_ISBN + 1)) {
                    result = new MediaResult(HttpServletResponse.SC_BAD_REQUEST,
                            "Bad Request. The barcode must not be emty!",
                            Collections.emptyList());
                } else {
                    final Disc disc = mapper.readValue(getInputStream(),
                            Disc.class);
                    disc.setBarcode(splittedURI[INDEX_ISBN]);
                    //update a disc which will be specified with a disc object
                    result = getService().updateDisc(disc);
                }
                break;
            case "POST":
                //add a disc which will be specified with a disc object
                result = getService().addDisc(mapper.readValue(getInputStream(),
                        Disc.class));
                break;
            default:
                //send an error answer
                result = new MediaResult(HttpServletResponse.SC_BAD_REQUEST,
                        "Bad Request. The used http method is not supported for"
                        + "this REST service.", Collections.emptyList());
                break;
        }
        return result;
    }

    //Methods Public
    //--------------------------------------------------------------------------
    @Override
    public final Result processRequest(final String methodInput,
            final String requestURIInput, final String tokenInput,
            final InputStream inputStreamInput) {
        this.method = methodInput;
        this.requestURI = requestURIInput;
        this.token = tokenInput;
        this.inputStream = inputStreamInput;
        //the result which will be returned
        Result result;
        //check if requested person is authorized first
        final Optional<Result> optionalResult = getAuthorization()
                .checkAuthorization(getToken());
        //present means user is not authorized
        if (!optionalResult.isPresent()) {
            //book or disc request
            final String requestedResource = getRequestURI()
                    .split("/")[INDEX_RESOURCE_TYPE];
            try {
                if ("books".equals(requestedResource)) {
                    result = delegateBookAction();
                } else if ("discs".equals(requestedResource)) {
                    result = delegateDiscAction();
                } else {
                    result = new MediaResult(HttpServletResponse.SC_NOT_FOUND,
                            "Not found. The requested resource does not exist."
                            + " Make sure to use the correct URI "
                            + "pattern. The pattern is as follows: "
                            + "/shareit/media/[books or discs]/"
                            + "{isbn or barcode}",
                            Collections.emptyList());
                }
            } catch (IOException exception) {
                result = new MediaResult(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "A server error occured. This is not your fault. "
                        + "You can calm down again. "
                        + "Info fuer den Chefinformatiker:"
                        + "Ein Fehler is beim parsen des Requests aufgetreten.",
                        Collections.emptyList());
            }
        } else {
            result = optionalResult.get();
        }
        return result;
    }
    //Getter + Setter (also Private)
    //--------------------------------------------------------------------------

    /**
     * Basic getter for request method.
     *
     * @return method as String.
     */
    private String getMethod() {
        return method;
    }

    /**
     * Basic getter for request URI.
     *
     * @return requestURI as String.
     */
    private String getRequestURI() {
        return requestURI;
    }

    /**
     * Basic getter for request token.
     *
     * @return token as String.
     */
    private String getToken() {
        return token;
    }

    /**
     * Basic getter for request input stream.
     *
     * @return inputStream.
     */
    private InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Basic getter for service.
     *
     * @return service.
     */
    private Service getService() {
        return service;
    }

    /**
     * Basic getter for authorization.
     *
     * @return authorization.
     */
    private Authorization getAuthorization() {
        return authorization;
    }
    
    
}
