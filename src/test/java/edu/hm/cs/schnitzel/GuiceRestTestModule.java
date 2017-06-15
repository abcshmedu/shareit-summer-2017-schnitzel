package edu.hm.cs.schnitzel;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import edu.hm.cs.schnitzel.daos.DatabaseAccessObject;
import edu.hm.cs.schnitzel.daos.HibernateDatabaseAccessObject;
import edu.hm.cs.schnitzel.dataExchange.Authorization;
import edu.hm.cs.schnitzel.dataExchange.MediaRequest;
import edu.hm.cs.schnitzel.dataExchange.Request;
import edu.hm.cs.schnitzel.services.MediaService;
import edu.hm.cs.schnitzel.services.Service;

/**
 * A guice module for testing the MediaRequest class.
 *
 * @author nicfel
 */
public class GuiceRestTestModule extends AbstractModule {

    @Override
	protected void configure() {
		bind(Request.class).to(MediaRequest.class);
		// authorization will be mocked
		bind(Service.class).to(MediaService.class);
		bind(DatabaseAccessObject.class).to(HibernateDatabaseAccessObject.class).in(Singleton.class);;
	}

    /**
     * This provides injections for "Authorization" with a mocked Authorization object.
     *
     * @return The mocked authorization object.
     */
    @Provides
    final Authorization provideAuthorization() {
    	final Authorization authorization = mock(Authorization.class);
    	//behavior of mocked Authorization here:
    	when(authorization.checkAuthorization(any()))
    	.thenReturn(Optional.empty());
    	//example
    	return authorization;
    }
}
