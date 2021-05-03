package com.microservices.demo.analytics.service.dataaccess.repository;

import java.util.Collection;

// To customize the spring boot JPA repository.
public interface AnalyticsCustomRepository<T, PK> {

    <S extends T> PK persist(S entity);

    <S extends T> void batchPersist(Collection<S> entities);

    <S extends T> S merge(S entity);

    <S extends T> void batchMerge(Collection<S> entities);

    void clear();
}
