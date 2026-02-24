package itmo.ru.lab4.resource;

import itmo.ru.lab4.entity.User;
import itmo.ru.lab4.service.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collections;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private AuthService authService;

    @POST
    @Path("/register")
    public Response register(User user) {
        if (user.getLogin() == null || user.getLogin().trim().isEmpty() ||
                user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Логин и пароль не могут быть пустыми").build();
        }

        if (authService.register(user.getLogin(), user.getPassword())) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("Пользователь с таким логином уже существует").build();
    }

    @POST
    @Path("/login")
    public Response login(User user) {
        String token = authService.login(user.getLogin(), user.getPassword());

        if (token != null) {
            return Response.ok(Collections.singletonMap("token", token)).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("Неверный логин или пароль").build();
    }
}