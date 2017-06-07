package edu.hm;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

import edu.hm.cs.schnitzel.daos.DatabaseAccessObject;
import edu.hm.cs.schnitzel.daos.HibernateDatabaseAccessObject;
import edu.hm.cs.schnitzel.dataExchange.MediaRequest;
import edu.hm.cs.schnitzel.dataExchange.Request;
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
			bind(Service.class).to(MediaService.class);
			bind(DatabaseAccessObject.class).to(HibernateDatabaseAccessObject.class);
		}
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