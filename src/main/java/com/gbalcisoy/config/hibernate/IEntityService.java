package com.gbalcisoy.config.hibernate;

import com.gbalcisoy.model.PersistableEntity;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.function.Function;

public interface IEntityService<T extends PersistableEntity> {

    default void saveOrUpdate(T entity, Handler<AsyncResult<T>> resultHandler) {
        execute(entityManager -> entityManager.merge(entity), resultHandler);
    }

    default void delete(T entity, Handler<AsyncResult<T>> resultHandler) {
        execute(entityManager -> {
            entityManager.remove(entity);
            return null; // TODO fix
        }, resultHandler);
    }

    default void deleteById(Class<T> classForQuery, String id, Handler<AsyncResult<T>> resultHandler) {
        execute(entityManager -> {
            T entity = entityManager.find(classForQuery, id);
            entityManager.remove(entity);
            return null; // TODO fix
        }, resultHandler);
    }

    default void findById(Class<T> classForQuery, String id, Handler<AsyncResult<T>> resultHandler) {
        execute(entityManager -> entityManager.find(classForQuery, id), resultHandler);
    }

    default void executeInTransaction(Function<EntityManager, T> blockingHandler, Handler<AsyncResult<T>> resultHandler) {
        execute(entityManager -> {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            T result = blockingHandler.apply(entityManager);
            transaction.commit();
            return result;
        }, resultHandler);
    }

    default void execute(Function<EntityManager, T> blockingHandler, Handler<AsyncResult<T>> resultHandler) {
        EntityManagerFactorySingleton.getInstance().getVertx().executeBlocking(future -> {
            EntityManager entityManager = null;
            try {
                entityManager = EntityManagerFactorySingleton.getInstance().getEntityManagerFactory().createEntityManager();
                EntityTransaction transaction = entityManager.getTransaction();
                transaction.begin();
                T result = blockingHandler.apply(entityManager);
                transaction.commit();
                future.complete(result);
            } catch (Exception e) {
                future.fail(e);
            } finally {
                closeSilently(entityManager);
            }
        }, asyncResult -> {
            if (asyncResult.cause() != null) {
                resultHandler.handle(Future.failedFuture(asyncResult.cause()));
            } else {
                resultHandler.handle(Future.succeededFuture());
            }
        });
    }

    default void closeSilently(EntityManager entityManager) {
        try {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
