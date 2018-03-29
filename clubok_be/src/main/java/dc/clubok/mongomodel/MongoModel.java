package dc.clubok.mongomodel;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import dc.clubok.ClubOKService;
import dc.clubok.Crypt;
import dc.clubok.controllers.UserController;
import dc.clubok.models.*;
import dc.clubok.models.Model;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;
import static dc.clubok.utils.Constants.*;

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
    public <T extends Entity> void saveOne(T entity, Class<T> type) throws Exception {
        MongoCollection<T> collection = getCollection(type);
        validate(entity);

        if (entity instanceof User)
            ((User) entity).setPassword(Crypt.hash(((User) entity).getPassword().toCharArray()));

        collection.insertOne(entity);
        logger.info("Entity [" + entity.getClass().getSimpleName() + "] was created");
    }

    @Override
    public <T extends Entity> void saveMany(List<T> entities, Class<T> type) throws Exception {
        MongoCollection<T> collection = getCollection(type);

        for (Entity entity : entities) {
            validate(entity);
            if (entity instanceof User)
                ((User) entity).setPassword(Crypt.hash(((User) entity).getPassword().toCharArray()));
        }

        collection.insertMany(entities);
        logger.info("Entities [" + entities.get(0).getClass().getSimpleName() + "] were created");
    }

    /* Count entries */
    @Override
    public <T extends Entity> long count(Class<T> type) {
        return getCollection(type).count();
    }

    /* Finding in Database */
    @Override
    public <T extends Entity> T findOne(Document document, Class<T> type) {
        return getCollection(type).find(document).first();
    }

    @Override
    public <T extends Entity> List<T> findAll(Class<T> type) {
        List<T> list = new ArrayList<>();

        try (MongoCursor<T> cursor = getCollection(type).find().iterator()) {
            while (cursor.hasNext()) {
                list.add(cursor.next());
            }
        }
        return list;
    }

    @Override
    public <T extends Entity> T findById(ObjectId id, Class<T> type) {
        return getCollection(type).find(eq(id)).first();
    }

    @Override
    public <T extends Entity> T findByField(String fieldName, String value, Class<T> type) {
        return getCollection(type).find(eq(fieldName, value)).first();
    }

    @Override
    public <T extends Entity> void deleteOne(Document document, Class<T> type) {
        getCollection(type).deleteOne(document);
    }

    /* Updating entries in Database */
    @Override
    public <T extends Entity> void update(T entity, Document update, Class<T> type) {
        getCollection(type).updateOne(eq("_id", entity.getId()), new Document("$set", update));
        logger.info("Entity [" + entity.getClass().getSimpleName() + "] was updated");
    }

    /* Model Validation */
    @Override
    public <T extends Entity> void validate(T entity) throws Exception {
        logger.debug("Validating [" + entity.getClass().getSimpleName() + "]");
        Set<ConstraintViolation<Entity>> violations = validator.validate(entity);

        String message = "";

        for (ConstraintViolation<Entity> violation: violations) {
            message += "Validation Error [" + entity.getClass().getSimpleName() + "] - " +
                    "Property: " + violation.getPropertyPath() +
                    "Value: " + violation.getInvalidValue() +
                    "Message: " + violation.getMessage() + "\n";
            logger.error(message);
        }

        if (violations.size() != 0)
            throw new Exception(message);

        if (entity instanceof User && UserController.findByEmail(((User) entity).getEmail()) != null) {
            message += "Validation Error: User with email " + ((User) entity).getEmail() + " already exists";
            logger.error(message);
            throw new Exception(message);
        }

    }



}
