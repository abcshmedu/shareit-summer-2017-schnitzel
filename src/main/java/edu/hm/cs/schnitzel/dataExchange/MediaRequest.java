/*
 * Autoren:     N.Dassler, P.Konopac
 * E-Mail:      dassler@hm.edu, konopac@hm.edu
 * Team:        schnitzel
 * Vorlesung:   Software Architektur
 * Dozent:      A.Boettcher
 */
package edu.hm.cs.schnitzel.dataExchange;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.hm.cs.schnitzel.entities.Book;
import edu.hm.cs.schnitzel.entities.Disc;
import edu.hm.cs.schnitzel.services.MediaService;

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
    private final HttpServletRequest request;
    //Constructors
    //--------------------------------------------------------------------------

    /**
     * Standard Constructor which initializes object with request and response.
     *
     * @param requestInput The servlet request object.
     */
    public MediaRequest(final HttpServletRequest requestInput) {
        this.request = requestInput;
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
        final MediaService mediaService = new MediaService();
        //the jackson mapper to create book objects
        final ObjectMapper mapper = new ObjectMapper();
        final String[] splittedURI = getRequest()
                .getRequestURI().split("/");
        switch (getRequest().getMethod()) {
            case "GET":
                //check if isbn is in url
                if (splittedURI.length == INDEX_ISBN + 1) {
                    //request only for one book
                    result = mediaService.getBook(splittedURI[INDEX_ISBN]);
                } else {
                    //request for all books
                    result = mediaService.getBooks();
                }
                break;
            case "PUT":
                if (splittedURI.length < (INDEX_ISBN + 1)) {
                    result = new MediaResult(HttpServletResponse.SC_BAD_REQUEST,
                            "Bad Request. The isbn-number must not be emty!",
                            Collections.emptyList());
                } else {
                    //update a book which will be specified with a book object
                    final Book book = mapper.readValue(getRequest()
                            .getInputStream(), Book.class);
                    book.setIsbn(splittedURI[INDEX_ISBN].replaceAll("-", ""));
                    result = mediaService.updateBook(book);
                }
                break;
            case "POST":
                //add a book which will be specified with a book object
                result = mediaService.addBook(mapper.readValue(getRequest()
                        .getInputStream(), Book.class));
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
        final MediaService mediaService = new MediaService();
        //the jackson mapper to create book objects
        final ObjectMapper mapper = new ObjectMapper();
        final String[] splittedURI = getRequest()
                .getRequestURI().split("/");
        switch (getRequest().getMethod()) {
            case "GET":
                //check for isbn in uri
                if (splittedURI.length == INDEX_ISBN + 1) {
                    //return just the requested book
                    result = mediaService.getDisc(splittedURI[INDEX_ISBN]);
                } else {
                    //return all discs
                    result = mediaService.getDiscs();
                }
                break;
            case "PUT":
                if (splittedURI.length < (INDEX_ISBN + 1)) {
                    result = new MediaResult(HttpServletResponse.SC_BAD_REQUEST,
                            "Bad Request. The barcode must not be emty!",
                            Collections.emptyList());
                } else {
                    final Disc disc = mapper.readValue(getRequest()
                            .getInputStream(), Disc.class);
                    disc.setBarcode(splittedURI[INDEX_ISBN]);
                    //update a disc which will be specified with a disc object
                    result = mediaService.updateDisc(disc);
                }
                break;
            case "POST":
                //add a disc which will be specified with a disc object
                result = mediaService.addDisc(mapper.readValue(getRequest()
                        .getInputStream(), Disc.class));
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
     * This checks if the user is authorized. There are three ways how the
     * authorization can fail: 1. There is no token. 2. There is a token but its
     * not valid. 3. The authorization service is offline
     *
     * @return If the user is not authorized this will return an appropriate
     * result.
     */
    private Optional<Result> checkAuthorization() {
        final Optional<Result> result;
        final String token = getRequest().getParameter("token");
        //first check if token is there
        if (token != null && !"".equals(token)) {
            //json string from token
            final String json = "{\"token\": \"" + token + "\"}";
            //send request to auth service
            //Optional will be empty if everything is alright
            //2. There is a token but its not valid.
            //3. The authorization server is offline.
            result = sendRequestToAuthService(json);

        } else {
            //1. There is no token:
            result = Optional.of(new MediaResult(HttpServletResponse.SC_BAD_REQUEST, "U didnt "
                    + "provide a token with your request. Please provide the "
                    + "token which you requested from the authorization server"
                    + " http://auth-schnitzel.herokuapp.com/ like this: "
                    + ".../shareit/...?token=YOUR TOKEN HERE.",
                    Collections.EMPTY_LIST));
        }
        return result;
    }

    /**
     * This method will send a request to auth service to check the token.
     *
     * @param json The json which will be sent with the request.
     * @return Empty optional if the token is valid. Else the result wrapped in
     * an Optional.
     */
    private Optional<Result> sendRequestToAuthService(final String json) {
        Optional<Result> result = Optional.empty();
        try {
        	
        	
        	// send request and receive answer
        	final String answer = sendAndReceive("POST", "token", json);
        	
        	
        	
//            //open connection to auth service
//            final URL url = new URL("https", "auth-schnitzel.herokuapp.com", "/shareit/auth/token");
//            final HttpsURLConnection httpURLConnection
//                    = (HttpsURLConnection) url.openConnection();
//            //make it a output connection
//            httpURLConnection.setDoOutput(true);
//            //set method to post
//            httpURLConnection.setRequestMethod("POST");
//            //set own properties (content type = json + content length =
//            //json string length)
//            httpURLConnection.setRequestProperty("Content-Type",
//                    "application/json");
//            httpURLConnection.setRequestProperty("Accept",
//            		"application/json");
//            httpURLConnection.setRequestProperty("Content-Length",
//                    String.valueOf(json.length()));
//            //the answer from the auth server
//            final String answer;
//            //write data (use try to automatically close resources)
//            try (final OutputStream outputStream
//                    = httpURLConnection.getOutputStream();
//                    final BufferedReader bufferedReader
//                    = new BufferedReader(new InputStreamReader(
//                            httpURLConnection.getInputStream()))) {
//                //write data
//                outputStream.write(json.getBytes());
//                answer = bufferedReader.lines().collect(Collectors.joining());
//            }
            
            
            
            
            //search for valid: true,
            final Pattern pattern = Pattern.compile("\"valid\":true");
            //if this  is true we have a valid token
            if (!pattern.matcher(answer).find()) {
                result = Optional.of(new MediaResult(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Your token was not accepted.",
                        Collections.EMPTY_LIST));
            }
        } catch (MalformedURLException ex) {
            result = Optional.of(new MediaResult(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Dont panic."
                    + "Its not your fault.", Collections.EMPTY_LIST));
            System.out.println("The URL was malformed.");
            System.out.println(ex.toString());

        } catch (IOException ex) {
            result = Optional.of(new MediaResult(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Dont panic."
                    + "Its not your fault.", Collections.EMPTY_LIST));
            System.out.println("An error occured while sending data to the auth"
                    + "service.");
            System.out.println(ex.toString());
        }
        //result will be empty if everything went alright
        return result;
    }
    

    // Private Methods
    
    /**
     * Send Request and receive answer.
     * 
     * @param method is the HTTP method
     * @param token is the "/token" extension
     * @param content is the content to be sent
     * 
     * @return the content of the answer
     * @throws IOException
     */
    private final String sendAndReceive(final String method, final String token, final String content) throws IOException {
        String result = "";
        try (final Socket socket = new Socket("auth-schnitzel.herokuapp.com", 80);
                final PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                final BufferedReader buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            // send header
            sendHttpHeader(printWriter, method, token, content.length());
            // send content
            sendContent(printWriter, content);

            // read answer
            readUntilBody(buffReader);
            // read content
            result = buffReader.lines().collect(Collectors.joining());
        }
        return result;
    }

    /**
     * Send the HTTP header.
     * 
     * @param writer is the writer
     * @param token is the "/token" extension
     * @param method is the HTTP method
     * @param contentLength is the length of the content
     */
    private final void sendHttpHeader(final PrintWriter writer, final String method, final String token, final int contentLength) {
        writer.print(method + " /shareit/auth/" + token + " HTTP/1.0\r\n");
        writer.print("Host: auth-schnitzel.herokuapp.com\r\n");
        writer.print("Content-Type: application/json\r\n");
        writer.print("Accept: application/json\r\n");
        writer.print("Content-Length: " + contentLength + "\r\n");
        writer.print("\r\n");
        writer.flush();
    }

    /**
     * Read body.
     * 
     * @param buffReader is the reader
     * @throws IOException
     */
    private final void readUntilBody(final BufferedReader buffReader) throws IOException {
        String line = buffReader.readLine();
        while (line.length() > 0) {
            line = buffReader.readLine();
        }
    }

    /**
     * Send content.
     * 
     * @param printWriter is the sender
     * @param content is the conent
     */
    private final void sendContent(final PrintWriter printWriter, final String content) {
        printWriter.write(content);
        printWriter.flush();
    }

    //Methods Public
    //--------------------------------------------------------------------------
    @Override
    public final Result processRequest() {
        //the result which will be returned
        Result result;
        //check if requested person is authorized first
        final Optional<Result> optionalResult = checkAuthorization();
        //present means user is authorized
        if (!optionalResult.isPresent()) {

            //book or disc request
            final String requestedResource = getRequest()
                    .getRequestURI().split("/")[INDEX_RESOURCE_TYPE];
            try {
                if ("books".equals(requestedResource)) {
                    result = delegateBookAction();
                } else if ("discs".equals(requestedResource)) {
                    result = delegateDiscAction();
                } else {
                    result = new MediaResult(HttpServletResponse.SC_NOT_FOUND,
                            "Not found. The requested resource does not exist."
                            + "Make sure to use the correct URI pattern."
                            + "The pattern is as follows:"
                            + "/shareit/media/books or discs/{isbn or barcode]",
                            Collections.emptyList());
                }
            } catch (IOException exception) {
                result = new MediaResult(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "A server error occured. This is not your fault. "
                        + "You can calm down again. Info fuer den Chefinformatiker:"
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
     * Simple getter for Request Object Variable.
     *
     * @return Request object.
     */
    private HttpServletRequest getRequest() {
        return request;
    }

}
