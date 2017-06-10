/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.hm.cs.schnitzel.dataExchange;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import edu.hm.cs.schnitzel.services.Service;
import javax.servlet.http.HttpServletRequest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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

    private Request getMediaRequest() {
        return mediaRequest;
    }
    
    
}
