package dc.clubok.data;

import dc.clubok.models.UserEntity;

import java.util.List;

public interface UserDB {
    void add(UserEntity user);

    void add(List<UserEntity> users);

    void delete(UserEntity user);

    UserEntity findById(String id);

    UserEntity findByCredentials(String email, String password);
}
