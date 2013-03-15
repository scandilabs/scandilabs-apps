package org.catamarancode.connect.entity.support;

import org.catamarancode.entity.support.RepositoryBase;
import org.hibernate.SessionFactory;

public class Repository extends RepositoryBase {
    
    public Repository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }


}
