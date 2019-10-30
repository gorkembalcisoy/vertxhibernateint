package com.gbalcisoy.config.hibernate;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.Getter;
import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.dialect.PostgreSQL95Dialect;
import org.postgresql.Driver;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

import static org.hibernate.cfg.AvailableSettings.*;
import static org.hibernate.tool.schema.Action.VALIDATE;

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
                this.entityManagerFactory =
                        Persistence
                                .createEntityManagerFactory("vertx-hibernate-int-tests"
                                        , createJpaConfigMap());
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

    private Map createJpaConfigMap() {
        HashMap<String, Object> jpaConfigMap = new HashMap<>();
        jpaConfigMap.put(DIALECT, PostgreSQL95Dialect.class);
        jpaConfigMap.put(GENERATE_STATISTICS, false);
        jpaConfigMap.put(HBM2DDL_AUTO, VALIDATE);
        jpaConfigMap.put(SHOW_SQL, true);
        jpaConfigMap.put(FORMAT_SQL, false);
        jpaConfigMap.put(USE_SQL_COMMENTS, false);
        // jdbc props
        jpaConfigMap.put(JPA_JDBC_DRIVER, Driver.class);
        jpaConfigMap.put(JPA_JDBC_URL, "jdbc:postgresql://localhost:5432/postgres");
        jpaConfigMap.put(JPA_JDBC_USER, "postgres");
        jpaConfigMap.put(JPA_JDBC_PASSWORD, "postgres321");
        // c3p0 props
        jpaConfigMap.put(CONNECTION_PROVIDER, C3P0ConnectionProvider.class);
        jpaConfigMap.put(C3P0_MAX_SIZE, 20);
        jpaConfigMap.put(C3P0_MIN_SIZE, 1);
        jpaConfigMap.put(C3P0_ACQUIRE_INCREMENT, 1);
        jpaConfigMap.put(C3P0_IDLE_TEST_PERIOD, 300);
        jpaConfigMap.put(C3P0_MAX_STATEMENTS, 0);
        jpaConfigMap.put(C3P0_TIMEOUT, 100);
        return jpaConfigMap;
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
