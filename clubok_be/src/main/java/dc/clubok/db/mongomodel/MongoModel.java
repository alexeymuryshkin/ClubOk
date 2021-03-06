package dc.clubok.db.mongomodel;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import dc.clubok.ClubOKService;
import dc.clubok.db.models.Entity;
import dc.clubok.db.models.Model;
import dc.clubok.utils.ClubOkException;
import dc.clubok.utils.SearchParams;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;
import static dc.clubok.utils.Constants.*;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;

public class MongoModel implements Model {
    private static Logger logger = LoggerFactory.getLogger(ClubOKService.class.getCanonicalName());

    private <T extends Entity> MongoCollection<T> getCollection(Class<T> type) {
        return mongo.getDb().getCollection(
                type.getSimpleName().toLowerCase() + "s",
                type
        );
    }

    /* Save to Database */
    @Override
    public <T extends Entity> void saveOne(T entity, Class<T> type) throws ClubOkException {
        try {
            getCollection(type).insertOne(entity);
        } catch (MongoException me) {
            Document details;
            if (me.getCode() == 121) {
                details = new Document("details", "Validation failed");
                throw new ClubOkException(ERROR_VALIDATION, details);
            } else if (me.getCode() == 11000) {
                details = new Document("details", "Such entry already exist");
                throw new ClubOkException(ERROR_VALIDATION, details);
            } else {
                details = new Document("details", me.getMessage());
                throw new ClubOkException(ERROR_DB, details, SC_INTERNAL_SERVER_ERROR);
            }
        }
        logger.info("Entity [" + entity.getClass().getSimpleName() + "] was created");
    }

    @Override
    public <T extends Entity> void saveMany(List<T> entities, Class<T> type) throws ClubOkException {
        try {
            getCollection(type).insertMany(entities);
        } catch (MongoException me) {
            Document details = new Document("details", me.getMessage());
            throw new ClubOkException(ERROR_DB, details, SC_INTERNAL_SERVER_ERROR);
        }
        logger.info("Entities [" + entities.get(0).getClass().getSimpleName() + "] were created");
    }

    /* Count entries */
    @Override
    public <T extends Entity> long count(Class<T> type) throws ClubOkException {
        try {
            return getCollection(type).count();
        } catch (MongoException me) {
            Document details = new Document("details", me.getMessage());
            throw new ClubOkException(ERROR_DB, details, SC_INTERNAL_SERVER_ERROR);
        }
    }

    /* Finding in Database */
    @Override
    public <T extends Entity> T findOne(Document document, Class<T> type) throws ClubOkException {
        try {
            return getCollection(type).find(document).first();
        } catch (MongoException me) {
            Document details = new Document("details", me.getMessage());
            throw new ClubOkException(ERROR_DB, details, SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public <T extends Entity> List<T> findByParams(SearchParams params, Class<T> type) throws ClubOkException {
        List<T> result = new ArrayList<>();

        FindIterable<T> iterable = getCollection(type)
                .find(params.getFields())
                .limit(params.getSize())
                .skip((params.getPage() - 1) * params.getSize());

        if (params.getProjection() != null) iterable.projection(params.getProjection());
        if (params.getSort() != null) iterable.sort(params.getSort());


        try (MongoCursor<T> cursor = iterable.iterator()) {
            while (cursor.hasNext()) {
                result.add(cursor.next());
            }
        } catch (MongoException me) {
            Document details = new Document("details", me.getMessage());
            throw new ClubOkException(ERROR_DB, details, SC_INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    @Override
    public <T extends Entity> List<T> findAll(Class<T> type) throws ClubOkException {
        List<T> list = new ArrayList<>();

        try (MongoCursor<T> cursor = getCollection(type).find().iterator()) {
            while (cursor.hasNext()) {
                list.add(cursor.next());
            }
        } catch (MongoException me) {
            Document details = new Document("details", me.getMessage());
            throw new ClubOkException(ERROR_DB, details, SC_INTERNAL_SERVER_ERROR);
        }
        return list;
    }

    @Override
    public <T extends Entity> T findById(String id, Class<T> type) throws ClubOkException {
        try {
            return findById(new ObjectId(id), type);
        } catch (IllegalArgumentException iae) {
            throw new ClubOkException(ERROR_ILLEGAL_ID);
        }
    }

    @Override
    public <T extends Entity> T findById(ObjectId id, Class<T> type) throws ClubOkException {
        try {
            return getCollection(type).find(eq(id)).first();
        } catch (MongoException me) {
            Document details = new Document("details", me.getMessage());
            throw new ClubOkException(ERROR_DB, details, SC_INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException iae) {
            throw new ClubOkException(ERROR_ILLEGAL_ID);
        }
    }

    @Override
    public <T extends Entity> T findByField(String fieldName, String value, Class<T> type) throws ClubOkException {
        try {
            return getCollection(type).find(eq(fieldName, value)).first();
        } catch (MongoException me) {
            Document details = new Document("details", me.getMessage());
            throw new ClubOkException(ERROR_DB, details, SC_INTERNAL_SERVER_ERROR);
        }
    }

    /* Deleting entries in Database */
    @Override
    public <T extends Entity> void deleteById(String id, Class<T> type) throws ClubOkException {
        try {
            deleteById(new ObjectId(id), type);
        } catch (IllegalArgumentException iae) {
            throw new ClubOkException(ERROR_ILLEGAL_ID);
        }
    }

    @Override
    public <T extends Entity> void deleteById(ObjectId id, Class<T> type) throws ClubOkException {
        try {
            getCollection(type).deleteOne(eq(id));
        } catch (MongoException me) {
            Document details = new Document("details", me.getMessage());
            throw new ClubOkException(ERROR_DB, details, SC_INTERNAL_SERVER_ERROR);
        }
    }

    /* Updating entries in Database */
    @Override
    public <T extends Entity> void updateRaw(Bson query, Bson update, Class<T> type) throws ClubOkException {
        try {
            getCollection(type).updateOne(query, update);
        } catch (MongoException me) {
            Document details;
            if (me.getCode() == 121) {
                details = new Document("details", "Validation failed");
                throw new ClubOkException(ERROR_VALIDATION, details);
            } else {
                details = new Document("details", me.getMessage());
                throw new ClubOkException(ERROR_DB, details, SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Override
    public <T extends Entity> void update(T entity, Bson update, Class<T> type) throws ClubOkException {
        updateRaw(eq("_id", entity.getId()), update, type);
    }

    @Override
    public <T extends Entity> void modify(T entity, Document update, Class<T> type) throws ClubOkException {
        List<Bson> updates = new ArrayList<>();
        update.forEach((k, v) -> updates.add(set(k, v)));
        update(entity, combine(updates), type);
    }

    @Override
    public <T extends Entity, S> void addOneToArray(T entity, String fieldName, S value, Class<T> type) throws ClubOkException {
        update(entity, push(fieldName, value), type);
    }

    @Override
    public <T extends Entity, S> void addManyToArray(T entity, String fieldName, List<S> values, Class<T> type) throws ClubOkException {
        update(entity, pushEach(fieldName, values), type);
    }

    @Override
    public <T extends Entity, S> void addOneToSet(T entity, String fieldName, S value, Class<T> type) throws ClubOkException {
        update(entity, addToSet(fieldName, value), type);
    }

    @Override
    public <T extends Entity, S> void addManyToSet(T entity, String fieldName, List<S> values, Class<T> type) throws ClubOkException {
        update(entity, addEachToSet(fieldName, values), type);
    }

    @Override
    public <T extends Entity, S extends Entity> void modifyOneFromArray(T entity, String fieldName, S value, Document update, Class<T> type) throws ClubOkException {
        Document query = new Document("_id", entity.getId()).append(fieldName + "._id", value.getId());
        List<Bson> updates = new ArrayList<>();
        update.forEach((k, v) -> updates.add(set(fieldName + ".$." + k, v)));
        updates.add(currentTimestamp("lastModified"));

        updateRaw(query, combine(updates), type);
    }

    @Override
    public <T extends Entity, S> void removeOneFromArray(T entity, String fieldName, S value, Class<T> type) throws ClubOkException {
        update(entity, pull(fieldName, value), type);
    }

    @Override
    public <T extends Entity, S> void removeManyFromArray(T entity, String fieldName, List<S> values, Class<T> type) throws ClubOkException {
        update(entity, pullAll(fieldName, values), type);
    }
}
