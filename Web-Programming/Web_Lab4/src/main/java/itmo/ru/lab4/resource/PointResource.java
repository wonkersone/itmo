package itmo.ru.lab4.resource;

import itmo.ru.lab4.dto.PointRequest;
import itmo.ru.lab4.entity.PointResult;
import itmo.ru.lab4.service.AuthService;
import itmo.ru.lab4.service.PointService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@Path("/points")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PointResource {

    @Inject
    private PointService pointService;

    @Inject
    private AuthService authService;

    private String getOwnerFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return authService.validateToken(token);
    }

    @GET
    public Response getPoints(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        String owner = getOwnerFromToken(authHeader);
        if (owner == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Необходима авторизация").build();
        }
        List<PointResult> points = pointService.getPointsByOwner(owner);
        return Response.ok(points).build();
    }

    @POST
    public Response addPoint(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader, PointRequest request) {
        String owner = getOwnerFromToken(authHeader);
        if (owner == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Необходима авторизация").build();
        }

        if (request.getX() == null || request.getY() == null || request.getR() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Все координаты и R должны быть заданы").build();
        }

        boolean hit = checkHit(request.getX(), request.getY(), request.getR());

        PointResult result = new PointResult(
                request.getX(),
                request.getY(),
                request.getR(),
                hit,
                owner
        );
        result.setCheckTime(LocalDateTime.now());

        pointService.savePoint(result);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    private boolean checkHit(Double x, Double y, Double r) {
        if (r == null || r <= 0) return false;

        if (x >= 0 && y >= 0) {
            return x <= r && y <= r;
        }
        if (x <= 0 && y >= 0) {
            return y <= (0.5 * x + r / 2);
        }
        if (x >= 0 && y <= 0) {
            return (x * x + y * y) <= (Math.pow(r / 2, 2));
        }
        return false;
    }

    @DELETE
    public Response clearPoints(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        String owner = getOwnerFromToken(authHeader);
        if (owner == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Необходима авторизация").build();
        }
        pointService.deletePointsByOwner(owner);
        return Response.ok().build();
    }
}