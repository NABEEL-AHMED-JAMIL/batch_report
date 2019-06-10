package com.ballistic.batch_report.coredel.dao;

import com.ballistic.batch_report.coredel.query.LocalQuery;
import com.mongodb.DuplicateKeyException;

import java.util.List;

public interface IApplicationDAO {

    public void save(Object transientInstance) throws DuplicateKeyException;
    public void update(Object transientInstance, String keyId);
    public void delete(Class<?> clazz, LocalQuery localMql);
    public Object findById(Class<? extends Object> clazz, String id);
    public List<?> findByMQL(Class<?> clazz, LocalQuery localMql);
    public List<?> findAll(Class<? extends Object> clazz);

}
