package com.microservices.demo.analytics.service.dataaccess.repository.impl;

import com.microservices.demo.analytics.service.dataaccess.entity.BaseEntity;
import com.microservices.demo.analytics.service.dataaccess.repository.AnalyticsCustomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Collection;

@Repository
public class AnalyticsRepositoryImpl<T extends BaseEntity<PK>, PK> implements AnalyticsCustomRepository<T, PK> {

    private static final Logger LOG = LoggerFactory.getLogger(AnalyticsRepositoryImpl.class);

    // An EM annotated with @PersistenceContext is a container management bean
    // which will take care of transactional management as we use @Transactional annotation
    @PersistenceContext
    protected EntityManager em; // This is a thread local reference to the Entity Manager object.

    // For configuring our custom batching logic.
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:50}")
    protected int batchSize;

    @Override
    @Transactional
    // At a high level, Spring creates proxies for all the classes annotated with @Transactional,
    // either on the class or on any of the methods.
    // The proxy allows the framework to inject transactional logic before and after the running method,
    // mainly for starting and committing the transaction.
    // Warning, since we are implementing an interface here,
    // by default the proxy will be a Java Dynamic Proxy.
    // This means that only external method calls that come in through the proxy will be intercepted.
    // Any self-invocation calls will not start any transaction,
    // even if the method has the @Transactional annotation.
    public <S extends T> PK persist(S entity) {
        this.em.persist(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public <S extends T> void batchPersist(Collection<S> entities) {
        if (entities.isEmpty()) {
            LOG.info("No entity found to insert!");
            return;
        }
        int batchCnt = 0;
        for (S entity: entities) {
            LOG.trace("Persisting entity with id {}", entity.getId());
            this.em.persist(entity);
            batchCnt++;
            if (batchCnt % batchSize == 0) {
                this.em.flush();
                this.em.clear();
            }
        }
        if (batchCnt % batchSize != 0) {
            this.em.flush();
            this.em.clear();
        }
    }

    @Override
    @Transactional
    public <S extends T> S merge(S entity) {
        return this.em.merge(entity);
    }

    @Override
    @Transactional
    public <S extends T> void batchMerge(Collection<S> entities) {
        if (entities.isEmpty()) {
            LOG.info("No entity found to insert!");
            return;
        }
        int batchCnt = 0;
        for (S entity : entities) {
            LOG.trace("Merging entity with id {}", entity.getId());
            this.em.merge(entity);
            batchCnt++;
            if (batchCnt % batchSize == 0) {
                this.em.flush();
                this.em.clear();
            }
        }
        if (batchCnt % batchSize != 0) {
            this.em.flush();
            this.em.clear();
        }
    }

    @Override
    public void clear() {

    }
}
