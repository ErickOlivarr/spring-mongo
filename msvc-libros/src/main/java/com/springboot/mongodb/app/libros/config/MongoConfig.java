package com.springboot.mongodb.app.libros.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

//Lo siguiente se pone para que se pueda hacer transacciones con mongodb, pero OJO que eso es siempre y cuando tenga el replica set activado en mongodb, y para eso necesitamos tener el mongodb en la nube como en el mongo atlas, tambien se puede con docker pero yo lo intenté con docker y no pude activar su replica set ahi ya que es un proceso que se hace manual y no pude, pero con el mongo atlas ya viene incluido el replica set sin tener que hacer nada manual, solo debemos poner esta clase y ya

@Configuration
@EnableMongoRepositories(basePackages = "com.springboot.mongodb.app.libros")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Override
    protected String getDatabaseName() {
        return "spring-mongo";
    }

    @Override
    public MongoClient mongoClient() {
        final ConnectionString connectionString = new ConnectionString("mongodb+srv://user_node_cafe:vBLXoKBajcFvKCRO@calendardb.fklbbtn.mongodb.net/spring-mongo");
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }
}
