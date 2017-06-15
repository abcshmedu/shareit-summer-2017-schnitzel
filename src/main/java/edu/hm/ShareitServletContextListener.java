package edu.hm;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

import edu.hm.cs.schnitzel.daos.DatabaseAccessObject;
import edu.hm.cs.schnitzel.daos.HibernateDatabaseAccessObject;
import edu.hm.cs.schnitzel.dataExchange.Authorization;
import edu.hm.cs.schnitzel.dataExchange.MediaRequest;
import edu.hm.cs.schnitzel.dataExchange.Request;
import edu.hm.cs.schnitzel.dataExchange.SocketAuthorization;
import edu.hm.cs.schnitzel.services.MediaService;
import edu.hm.cs.schnitzel.services.Service;
import edu.hm.cs.schnitzel.servlets.MediaServlet;

/**
 * Context Listener to enable usage of google guice together with jersey.
 */
public class ShareitServletContextListener extends GuiceServletContextListener {
	private static final Injector injector = Guice.createInjector(new AbstractModule() {
		@Override
		protected void configure() {
			bind(Request.class).to(MediaRequest.class);
			// To disable authentification, remove the next line and activete the mocking code below
			bind(Authorization.class).to(SocketAuthorization.class);
			bind(Service.class).to(MediaService.class);
			bind(DatabaseAccessObject.class).to(HibernateDatabaseAccessObject.class).in(Singleton.class);;
		}
		
	    /**
	     * This provides injections for "Authorization" with a mocked Authorization object.
	     *
	     * @return The mocked authorization object.
	     */
//	    @Provides
//	    final Authorization provideAuthorization() {
//	    	final Authorization authorization = mock(Authorization.class);
//	    	//behavior of mocked Authorization here:
//	    	when(authorization.checkAuthorization(any()))
//	    	.thenReturn(Optional.empty());
//	    	//example
//	    	return authorization;
//	    }
	}, new ServletModule() {
		@Override
		protected void configureServlets() {
			bind(MediaServlet.class);
			
			serve("/shareit/*").with(MediaServlet.class);
		}
	});

	@Override
	protected Injector getInjector() {
		return injector;
	}

	/**
	 * This method is only required for the HK2-Guice-Bridge in the Application
	 * class.
	 * 
	 * @return Injector instance.
	 */
	static Injector getInjectorInstance() {
		return injector;
	}
}