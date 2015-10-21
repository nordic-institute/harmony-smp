package eu.europa.ec.cipa.bdmsl.util;

/**
 * Created by feriaad on 15/06/2015.
 */

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class MapperFactoryFactory implements FactoryBean<MapperFactory> {

    @Override
    public MapperFactory getObject() throws Exception {
        return new DefaultMapperFactory.Builder().build();
    }

    @Override
    public Class<?> getObjectType() {
        return MapperFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}