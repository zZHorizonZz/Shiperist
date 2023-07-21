package dev.shiperist.resource;

import dev.shiperist.model.User;
import dev.shiperist.service.AccountService;
import dev.shiperist.service.SessionService;
import dev.shiperist.service.UserService;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.Optional;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthService {


    @Inject
    UserService userService;

    @Inject
    AccountService accountService;

    @Inject
    SessionService sessionService;

    @GET
    @PermitAll
    @Path("/settings")
    public Uni<Response> getSettings() {
        // Implement logic to return public settings of the instance.
        return Uni.createFrom().item(Response.ok().build());
    }

    @POST
    @PermitAll
    @Path("/signup")
    public Uni<User> signUp(User user) {
        return userService.createUser(user.getName(), user.getEmail(), user.getImage(), user.getPassword());
    }

    @POST
    @PermitAll
    @Path("/verify")
    public Uni<Response> verifyUser(User user) {
        // Implement the logic for user verification here.
        return Uni.createFrom().item(Response.ok().build());
    }

    @POST
    @PermitAll
    @Path("/recover")
    public Uni<Response> recoverPassword(User user) {
        // Implement the logic for password recovery here.
        return Uni.createFrom().item(Response.ok().build());
    }

    @POST
    @PermitAll
    @Path("/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Uni<Response> getToken(@FormParam("grant_type") String grantType,
                                  @FormParam("email") String email,
                                  @FormParam("password") String password,
                                  @FormParam("refresh_token") String refreshToken) {
        return switch (grantType) {
            case "password" -> userService.getUserByEmail(email)
                    .flatMap(user -> {
                        if (user.isEmpty()) {
                            return Uni.createFrom().item(Response.status(Response.Status.UNAUTHORIZED)
                                    .entity("Invalid email or password.").build());
                        }

                        System.out.println("password: " + password);
                        User validUser = user.get();

                        if (userService.checkPassword(password, validUser.getPassword())) {
                            System.out.println("validUser: " + validUser);
                            return sessionService.createSession(validUser)
                                    .map(session -> Response.ok(session).build());
                        } else {
                            return Uni.createFrom().item(Response.status(Response.Status.UNAUTHORIZED)
                                    .entity("Invalid email or password.").build());
                        }
                    });
            case "refresh_token" ->
                // Assuming the refresh_token is the sessionId.
                    sessionService.getSession(Long.parseLong(refreshToken))
                            .flatMap(session -> {
                                if (session.isEmpty() || session.get().getExpires().isBefore(LocalDateTime.now())) {
                                    return Uni.createFrom().item(Response.status(Response.Status.UNAUTHORIZED)
                                            .entity("Invalid or expired refresh token.").build());
                                }

                                return Uni.createFrom().item(Response.ok(session.get()).build());
                            });
            default -> Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid grant_type parameter.").build());
        };
    }

    @GET
    @Path("/user/{id}")
    public Uni<Optional<User>> getUser(@PathParam("id") Long id) {
        return userService.getUser(id);
    }

    @PUT
    @Path("/user")
    public Uni<User> updateUser(User user) {
        return userService.updateUser(user);
    }

    @POST
    @Path("/logout")
    public Uni<Response> logoutUser(User user) {
        // Implement the logic to logout the user (invalidate all sessions).
        return Uni.createFrom().item(Response.ok().build());
    }
}
