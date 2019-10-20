package com.gbalcisoy.application;

import com.gbalcisoy.config.hibernate.EntityManagerFactorySingleton;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class Application {

    public static void main(String[] args) throws InterruptedException {

        final Vertx vertx = Vertx.vertx();
        // HibernateConfigManager.class process begin ---> EntityManagerFactoryConfigManager olsun adi
        Future<Void> future = Future.future();
        future.setHandler(result -> {
            if (result.cause() != null) {
                result.cause().printStackTrace();
            }
            result.succeeded();
        });
        EntityManagerFactorySingleton.init(vertx, future);
        // HibernateConfigManager.class process end

        vertx.deployVerticle(new Server());
        System.out.println("Deployment done");
    }

}