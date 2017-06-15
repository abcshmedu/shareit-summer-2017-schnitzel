package edu.hm.cs.schnitzel;

import com.google.inject.servlet.ServletModule;

import edu.hm.cs.schnitzel.servlets.MediaServlet;

/**
 * A guice module for testing the MediaRequest class.
 *
 * @author nicfel
 */
public class GuiceServletTestModule extends ServletModule {

	@Override
	protected void configureServlets() {
		bind(MediaServlet.class);
		
		serve("/shareit/*").with(MediaServlet.class);
	}
}
