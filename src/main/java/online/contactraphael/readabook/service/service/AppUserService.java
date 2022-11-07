package online.contactraphael.readabook.service.service;

import online.contactraphael.readabook.model.dtos.UserRegistrationRequest;
import online.contactraphael.readabook.model.user.AppUser;
import online.contactraphael.readabook.model.user.ShortUser;

import java.util.List;

public interface AppUserService {
    AppUser addUser(UserRegistrationRequest userRegistrationRequest);

    AppUser findByEmail(String email);

    List<ShortUser> getAll(Integer page);

    void deleteUser(String email);

    void suspendUser(String email);
}
