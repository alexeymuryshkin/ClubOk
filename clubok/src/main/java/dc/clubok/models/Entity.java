package dc.clubok.models;

import lombok.Data;
import org.bson.types.ObjectId;

import javax.validation.Validator;
import javax.validation.constraints.NotNull;

@Data
public abstract class Entity {
    private @NotNull ObjectId id;

    public static boolean validate(Object object, Validator validator) {
        return validator.validate(object).size() == 0;
    }
}
