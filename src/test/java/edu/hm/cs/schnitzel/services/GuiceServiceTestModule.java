package edu.hm.cs.schnitzel.services;


import com.google.inject.AbstractModule;
import edu.hm.cs.schnitzel.daos.DatabaseAccessObject;
import edu.hm.cs.schnitzel.daos.MockDatabaseAccessObject;

/**
 *
 * @author nicfel
 */
public class GuiceServiceTestModule extends AbstractModule{

    public GuiceServiceTestModule() {
    }

    @Override
    protected void configure() {
        bind(Service.class).to(MediaService.class);
        bind(DatabaseAccessObject.class).to(MockDatabaseAccessObject.class);
    }
    
}
