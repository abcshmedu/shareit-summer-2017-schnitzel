/*
 * Autoren:     N.Dassler, P.Konopac
 * E-Mail:      dassler@hm.edu, konopac@hm.edu
 * Team:        schnitzel
 * Vorlesung:   Software Architektur
 * Dozent:      A.Boettcher
 */
package edu.hm.cs.schnitzel.dataExchange;

import java.util.Optional;

/**
 *
 * @author nicfel
 */
public interface Authorization {

    /**
     * This checks if the user is authorized. There are three ways how the
     * authorization can fail: 1. There is no token. 2. There is a token but its
     * not valid. 3. The authorization service is offline
     *
     * @param token The token to be sent to auth service.
     * @return If the user is not authorized this will return an appropriate
     * result.
     */
    Optional<Result> checkAuthorization(String token);
}
