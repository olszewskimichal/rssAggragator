package pl.michal.olszewski.rssaggregator.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class CascadeSaveMongoEventListener extends AbstractMongoEventListener<Object> {
 
    @Autowired
    private MongoOperations mongoOperations;
 
    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object source = event.getSource(); 
        ReflectionUtils.doWithFields(source.getClass(),
          new CascadeCallback(source, mongoOperations));
    }
}