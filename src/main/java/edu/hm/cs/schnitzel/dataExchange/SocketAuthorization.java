/*
 * Autoren:     N.Dassler, P.Konopac
 * E-Mail:      dassler@hm.edu, konopac@hm.edu
 * Team:        schnitzel
 * Vorlesung:   Software Architektur
 * Dozent:      A.Boettcher
 */
package edu.hm.cs.schnitzel.dataExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

/**
 * This class will authorize a given user by sending a request to the
 * authorization server.
 *
 * @author nicfel
 */
public class SocketAuthorization implements Authorization {

    private String token;

    @Override
    public Optional<Result> checkAuthorization(String token) {
        this.token = token;
        final Optional<Result> result;
        //first check if token is there
        if (getToken() != null && !"".equals(getToken())) {
            //json string from token
            final String json = "{\"token\": \"" + getToken() + "\"}";
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

    /**
     * Send Request and receive answer.
     *
     * @param methodInput is the HTTP methodInput
     * @param tokenInput is the "/token" extension
     * @param content is the content to be sent
     *
     * @return the content of the answer
     * @throws IOException
     */
    private String sendAndReceive(final String methodInput,
            final String tokenInput, final String content) throws IOException {
        String result;
        try (final Socket socket
                = new Socket("auth-schnitzel.herokuapp.com", 80);
                final PrintWriter printWriter
                = new PrintWriter(socket.getOutputStream());
                final BufferedReader buffReader
                = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()))) {
            // send header
            sendHttpHeader(printWriter, methodInput, tokenInput,
                    content.length());
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
     * @param tokenInput is the "/token" extension
     * @param methodInput is the HTTP method
     * @param contentLength is the length of the content
     */
    private void sendHttpHeader(final PrintWriter writer,
            final String methodInput, final String tokenInput,
            final int contentLength) {
        writer.print(methodInput + " /shareit/auth/" + tokenInput
                + " HTTP/1.0\r\n");
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
     * @param buffReader is the reader.
     * @throws IOException if error occurs during reading.
     */
    private void readUntilBody(final BufferedReader buffReader)
            throws IOException {
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
    private void sendContent(final PrintWriter printWriter, final String content) {
        printWriter.write(content);
        printWriter.flush();
    }

    private String getToken() {
        return token;
    }

}
