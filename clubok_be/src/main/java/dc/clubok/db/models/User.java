package dc.clubok.db.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static dc.clubok.utils.Constants.PL_REGULAR;

@EqualsAndHashCode(callSuper = true)
@Data
public class User extends Entity {
    @NotNull(message = "Email is not specified")
    @Email(message = "Email format is incorrect")
    private String email;
    @NotNull(message = "Password is not specified")
    @Size(min = 6, max = 16, message = "Password length should be between 6 and 16")
    private @NotNull String password;

    private String imageSrc;
    private int permissionLevel;
    private String fname;
    private String lname;

    private Set<String> subscriptions;
    private Set<Membership> membership;
    private List<Token> tokens;

    public User() {
        setId(new ObjectId());
        tokens = new ArrayList<>();
        subscriptions = new HashSet<>();
        membership = new HashSet<>();
        permissionLevel = PL_REGULAR;
    }

    public User(String email, String password) {
        this();
        setEmail(email);
        setPassword(password);
    }

    public User(String email, String password, int permissionLevel) {
        this(email, password);
        setPermissionLevel(permissionLevel);
    }
}
