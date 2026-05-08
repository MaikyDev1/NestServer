package net.maikydev.nestserver.ducket.annotations.auth;

import java.util.Optional;

public interface AuthentificationFlow {

    AuthClient getUser();

}
