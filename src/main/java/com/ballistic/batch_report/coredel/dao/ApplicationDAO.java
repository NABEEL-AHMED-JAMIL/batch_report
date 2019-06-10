package com.ballistic.batch_report.coredel.dao;

import com.ballistic.batch_report.coredel.MongoDBContext;
import com.ballistic.batch_report.coredel.query.LocalQuery;
import com.mongodb.DuplicateKeyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * A data access object (DAO) providing persistence and search support for POJO
 * entities. Transaction control of the save(), saveOrUpdate(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 *
 * @see com.ballistic.batch_report.coredel.dao
 * @author Nabeel Ahmed Jamil
 */
@Repository
@SuppressWarnings("unchecked")
public class ApplicationDAO implements IApplicationDAO {

    public static final Logger logger = LogManager.getLogger(ApplicationDAO.class);

    @Resource(name = "getMongoDBContext")
    private MongoDBContext mongoDBContext;

    public ApplicationDAO() { }

    public MongoDBContext getMongoDBContext() { return this.mongoDBContext; }

    @Override
    public void save(Object transientInstance) throws DuplicateKeyException {
        long startTime = System.currentTimeMillis();
        this.getMongoDBContext().getDatastore().save(transientInstance);
        logger.debug("Save Method query time =============== " + (System.currentTimeMillis() - startTime));
    }

	@Override
    public void update(Object transientInstance, String keyId) {
        long startTime = System.currentTimeMillis();
        Datastore dataStore = this.getMongoDBContext().getDatastore();
        Query query = dataStore.createQuery(transientInstance.getClass()).field(Mapper.ID_KEY).equal(new ObjectId(keyId)).disableValidation();
        UpdateOperations updateOps = dataStore.createUpdateOperations(transientInstance.getClass());
        dataStore.update(query,updateOps,true);
        logger.debug("Save or Update Method query time =============== " + (System.currentTimeMillis() - startTime));
    }

    @Override
    public void delete(Class<?> clazz, LocalQuery localMql) {
        long startTime = System.currentTimeMillis();
        Query<?> query = this.getMongoDBContext().getDatastore().find(clazz);
        localMql.createQuery(query); // local query process
        this.getMongoDBContext().getDatastore().delete(query); // delete data
        logger.debug("Delete Method query time =============== " + (System.currentTimeMillis() - startTime));
    }

    @Override
    public Object findById(Class<?> clazz, String id) {
        Object result = null;
        long startTime = System.currentTimeMillis();
        result = this.getMongoDBContext().getDatastore().get(clazz, id);
        logger.debug("Find by ID Method query time =============== " + (System.currentTimeMillis() - startTime));
        return result;
    }

    @Override
    public List findByMQL(Class<?> clazz, LocalQuery localMql) {
        List<?> result = null;
        long startTime = System.currentTimeMillis();
        Query<?> fetchQuery = this.getMongoDBContext().getDatastore().find(clazz);
        localMql.createQuery(fetchQuery); // local query process
        result = fetchQuery.asList();
        logger.debug("Find by MQL Method query time =============== " + (System.currentTimeMillis() - startTime));
        return result;
    }

    @Override
    public List<?> findAll(Class<?> clazz) {
        List<?> result = null;
        long startTime = System.currentTimeMillis();
        result = this.getMongoDBContext().getDatastore().find(clazz).asList();
        logger.debug("Find all Method query time =============== " + (System.currentTimeMillis() - startTime));
        return result;
    }

    public Object findByFiled(Class<?> clazz, String filedName, Object filedValue) {
        Object result = null;
        long startTime = System.currentTimeMillis();
        result = this.getMongoDBContext().getDatastore().createQuery(clazz).field(filedName).equal(filedValue).get();
        logger.debug("Find by User by Filed Method query time =============== " + (System.currentTimeMillis() - startTime));
        return result;
    }
}
