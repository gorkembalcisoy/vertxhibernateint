package com.gbalcisoy.config.hibernate;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.Getter;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerFactorySingleton {

    private static EntityManagerFactorySingleton instance;
    @Getter
    private EntityManagerFactory entityManagerFactory;
    @Getter
    private Vertx vertx;

    private EntityManagerFactorySingleton(Vertx vertx, Future future) {
        System.out.println("Hibernate service is starting...");
        this.vertx = vertx;
        this.vertx.executeBlocking(blockingProcessFuture -> {
            try {
                this.entityManagerFactory = Persistence.createEntityManagerFactory("vertx-hibernate-int-tests");
                blockingProcessFuture.complete();
            } catch (Exception e) {
                blockingProcessFuture.fail(e);
            }
        }, asyncResult -> {
            if (asyncResult.succeeded()) {
                System.out.println("Hibernate service is started successfully!");
                future.complete();
            } else {
                System.out.println("Hibernate service is failed !!!");
                future.fail(asyncResult.cause());
            }
        });
    }

    public static EntityManagerFactorySingleton getInstance() {
        if (instance == null) {
            throw new RuntimeException("init() method should be called first!");
        }
        return instance;
    }

    public static synchronized EntityManagerFactorySingleton init(Vertx vertx, Future future) {
        if (instance != null) {
            throw new RuntimeException("instance is already initialized!");
        }
        instance = new EntityManagerFactorySingleton(vertx, future);
        return instance;
    }
}
