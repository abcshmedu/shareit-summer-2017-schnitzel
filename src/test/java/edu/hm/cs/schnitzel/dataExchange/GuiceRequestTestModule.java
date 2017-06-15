package edu.hm.cs.schnitzel.dataExchange;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import edu.hm.cs.schnitzel.entities.Book;
import edu.hm.cs.schnitzel.services.Service;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A guice module for testing the MediaRequest class.
 *
 * @author nicfel
 */
public class GuiceRequestTestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Request.class).to(MediaRequest.class);
        /*our mock objects will be created using mockito + @Provides methods */
    }

    /**
     * This provides injections for "Service" with a mocked Service object.
     *
     * @return The mocked service object.
     */
    @Provides
    final Service provideService() {
        final Service service = mock(Service.class);
        //behavior of mocked Service here:
        when(service.getBook(any()))
                .thenReturn(new MediaResult(200, "OK.", Collections.emptyList()));
        when(service.getBooks()).thenReturn(new MediaResult(201, "OK.", Collections.emptyList()));
        //example
        //Mockito.when(service.addBook(any(Book.class))).thenReturn(new MediaResult(0, "test", Collections.emptyList()));
        return service;
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
