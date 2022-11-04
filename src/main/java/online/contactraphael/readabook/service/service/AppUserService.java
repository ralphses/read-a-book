package online.contactraphael.readabook.service.service;

import online.contactraphael.readabook.model.dtos.UserRegistrationRequestRequest;
import online.contactraphael.readabook.model.user.AppUser;

public interface AppUserService {
    AppUser addUser(UserRegistrationRequestRequest userRegistrationRequestRequest);

    AppUser findByEmail(String email);
}
