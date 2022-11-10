package online.contactraphael.readabook.service.service;

import online.contactraphael.readabook.model.user.UserToken.ActivationToken;
import online.contactraphael.readabook.model.user.UserToken.TokenPurpose;
import online.contactraphael.readabook.model.user.AppUser;

public interface ActivationTokenService {

    public void newToken(String tokenString, AppUser appUser, TokenPurpose tokenPurpose);

    public void activate(String token);

    public void resendLink(String email);

    public ActivationToken getActivationToken(String token);
}
