package dc.clubok.db.mongomodel;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import dc.clubok.ClubOKService;
import dc.clubok.db.controllers.UserController;
import dc.clubok.db.models.Entity;
import dc.clubok.db.models.Model;
import dc.clubok.db.models.User;
import dc.clubok.utils.Crypt;
import dc.clubok.utils.exceptions.ClubOkException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.currentDate;
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
        MongoCollection<T> collection = getCollection(type);
        validate(entity);

        if (entity instanceof User)
            ((User) entity).setPassword(Crypt.hash(((User) entity).getPassword().toCharArray()));

        try {
            collection.insertOne(entity);
        } catch (MongoException me) {
            throw new ClubOkException(DB_ERROR, me.getMessage(), SC_INTERNAL_SERVER_ERROR);
        }
        logger.info("Entity [" + entity.getClass().getSimpleName() + "] was created");
    }

    @Override
    public <T extends Entity> void saveMany(List<T> entities, Class<T> type) throws ClubOkException {
        MongoCollection<T> collection = getCollection(type);

        for (Entity entity : entities) {
            validate(entity);
            if (entity instanceof User)
                ((User) entity).setPassword(Crypt.hash(((User) entity).getPassword().toCharArray()));
        }

        try {
            collection.insertMany(entities);
        } catch (MongoException me) {
            throw new ClubOkException(DB_ERROR, me.getMessage(), SC_INTERNAL_SERVER_ERROR);
        }

        logger.info("Entities [" + entities.get(0).getClass().getSimpleName() + "] were created");
    }

    /* Count entries */
    @Override
    public <T extends Entity> long count(Class<T> type) throws ClubOkException {
        try {
            return getCollection(type).count();
        } catch (MongoException me) {
            throw new ClubOkException(DB_ERROR, me.getMessage(), SC_INTERNAL_SERVER_ERROR);
        }
    }

    /* Finding in Database */
    @Override
    public <T extends Entity> T findOne(Document document, Class<T> type) throws ClubOkException {
        try {
            return getCollection(type).find(document).first();
        } catch (MongoException me) {
            throw new ClubOkException(DB_ERROR, me.getMessage(), SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public <T extends Entity> List<T> findAll(Class<T> type) throws ClubOkException {
        List<T> list = new ArrayList<>();

        try (MongoCursor<T> cursor = getCollection(type).find().iterator()) {
            while (cursor.hasNext()) {
                list.add(cursor.next());
            }
        } catch (MongoException me) {
            throw new ClubOkException(DB_ERROR, me.getMessage(), SC_INTERNAL_SERVER_ERROR);
        }
        return list;
    }

    @Override
    public <T extends Entity> T findById(String id, Class<T> type) throws ClubOkException {
        try {
            return findById(new ObjectId(id), type);
        } catch (IllegalArgumentException iae) {
            throw new ClubOkException(INCORRECT_USER_ID, "ID format is invalid");
        }
    }

    @Override
    public <T extends Entity> T findById(ObjectId id, Class<T> type) throws ClubOkException {
        try {
            return getCollection(type).find(eq(id)).first();
        } catch (MongoException me) {
            throw new ClubOkException(DB_ERROR, me.getMessage(), SC_INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException iae) {
            throw new ClubOkException(INCORRECT_USER_ID, "ID format is invalid");
        }
    }

    @Override
    public <T extends Entity> T findByField(String fieldName, String value, Class<T> type) throws ClubOkException {
        try {
            return getCollection(type).find(eq(fieldName, value)).first();
        } catch (MongoException me) {
            throw new ClubOkException(DB_ERROR, me.getMessage(), SC_INTERNAL_SERVER_ERROR);
        }
    }

    /* Deleting entries in Database */
    @Override
    public <T extends Entity> void deleteById(String id, Class<T> type) throws ClubOkException {
        try {
            getCollection(type).deleteOne(eq(id));
        } catch (MongoException me) {
            throw new ClubOkException(DB_ERROR, me.getMessage(), SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public <T extends Entity> void deleteById(ObjectId id, Class<T> type) throws ClubOkException {
        try {
            getCollection(type).deleteOne(eq(id));
        } catch (MongoException me) {
            throw new ClubOkException(DB_ERROR, me.getMessage(), SC_INTERNAL_SERVER_ERROR);
        }
    }

    /* Updating entries in Database */
    @Override
    public <T extends Entity> void update(Document query, Document command, Class<T> type) throws ClubOkException {
        try {
            getCollection(type).updateOne(query, command);
        } catch (MongoException me) {
            throw new ClubOkException(DB_ERROR, me.getMessage(), SC_INTERNAL_SERVER_ERROR);
        }
        logger.info("Entity [" + type.getSimpleName() + "] was updated");
    }

    @Override
    public <T extends Entity> void update(T entity, Bson update, Class<T> type) throws ClubOkException {
        try {
            getCollection(type).updateOne(
                    eq("_id", entity.getId()),
                    combine(update, currentDate("lastModified"))
            );
        } catch (MongoException me) {
            throw new ClubOkException(DB_ERROR, me.getMessage(), SC_INTERNAL_SERVER_ERROR);
        }
        logger.info("Entity (" + entity.getId() + ") [" + entity.getClass().getSimpleName() + "] was updated");
    }

    /* Model Validation */
    @Override
    public <T extends Entity> void validate(T entity) throws ClubOkException {
        logger.debug("Validating [" + entity.getClass().getSimpleName() + "]");
        Set<ConstraintViolation<Entity>> violations = validator.validate(entity);

        String message = "";

        for (ConstraintViolation<Entity> violation : violations) {
            message += "Validation Error [" + entity.getClass().getSimpleName() + "] - " +
                    "Property: " + violation.getPropertyPath() +
                    "Value: " + violation.getInvalidValue() +
                    "Message: " + violation.getMessage() + "\n";
            logger.error(message);
        }

        if (violations.size() != 0)
            throw new ClubOkException(VALIDATION_ERROR, message);

        if (entity instanceof User && UserController.findByEmail(((User) entity).getEmail()) != null) {
            message += "Validation Error: User with email " + ((User) entity).getEmail() + " already exists";
            logger.error(message);
            throw new ClubOkException(VALIDATION_ERROR, message);
        }

    }



}
