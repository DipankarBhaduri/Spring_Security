package com.HRNavigator.authenticationConfig;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCursor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.util.ClassUtils;
import java.util.HashMap;


@Configuration
public class ContextEventAppConfig extends AbstractMongoClientConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private String database = "hr-navigator";

    private HashMap<String, MongoTemplate> mongoTemplates = new HashMap<>();

    private boolean savequerylog = false;


    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Bean("mongoTemplate")
    public MongoTemplate mongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        return mongoTemplate;
    }

    public MongoTemplate mongoTemplate(String... dbNames) {
        if(dbNames!= null && dbNames.length>0){
            return mongoTemplates().get(dbNames[0]);
        } return mongoTemplate();
    }

    @Bean("mongoTemplateReadPreferred")
    public MongoTemplate mongoTemplateReadPreferred() {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory()/*, customMappingMongoConverter*/);
        mongoTemplate.setReadPreference(ReadPreference.secondaryPreferred());
        return mongoTemplate;
    }

    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient, String databaseName) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, databaseName);
    }

    @Bean("mongoTemplates")
    public HashMap<String, MongoTemplate> mongoTemplates() {
        if(mongoTemplates.size() == 0) {
            MongoCursor<String> databases = mongoClient().listDatabaseNames().iterator();
            while (databases.hasNext()) {
                String dbName = databases.next();
                mongoTemplates.put(dbName, new MongoTemplate(mongoDatabaseFactory(mongoClient(), dbName)/*, customMappingMongoConverter*/));
            }
            return mongoTemplates;
        }
        return mongoTemplates;
    }

    @Bean
    @Override
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory databaseFactory, MongoCustomConversions customConversions, MongoMappingContext mappingContext) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(databaseFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mappingContext){
            @Override
            public Object convertId(Object id, Class<?> targetType) {
                if (id == null) {
                    return new ObjectId().toString();
                } else if (ClassUtils.isAssignable(ObjectId.class, targetType) && id instanceof String && ObjectId.isValid(id.toString())) {
                    return id;
                }
                return super.convertId(id, targetType);
            }
        };
        converter.setCustomConversions(customConversions);
        converter.setCodecRegistryProvider(databaseFactory);
        return converter;
    }
    public boolean isSavequerylog() {
        return savequerylog;
    }

    public void setSavequerylog(boolean savequerylog) {
        this.savequerylog = savequerylog;
    }
}
