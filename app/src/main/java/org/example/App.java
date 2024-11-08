package org.example;

import java.util.List;
import java.util.Optional;

import org.example.auth.AuthorizationManager;
import org.example.config.AuthorizationConfig;
import org.example.config.DatabaseConfig;
import org.example.config.GsonJsonMapper;
import org.example.dto.GenericResDTO;
import org.example.dto.TokenResponseDTO;
import org.example.dto.UnidadeRegisterReqDTO;
import org.example.dto.UserRegisterReqDTO;
import org.example.entity.Unidade;
import org.example.entity.User;
import org.example.repository.UnidadeRepository;
import org.example.repository.UnidadeRepositoryJdbc;
import org.example.repository.UserRepository;
import org.example.repository.UserRepositoryJdbc;
import org.example.usecases.ListUsersUseCase;
import org.example.usecases.UserRegisterUseCase;
import org.example.util.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import io.javalin.Javalin;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        GsonJsonMapper gsonMapper = new GsonJsonMapper();

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(gsonMapper);
            config.routing.contextPath = "/api/v1";
        }).start(8080);

        // securty setup
        AuthorizationConfig authConfig = new AuthorizationConfig();
        AuthorizationManager authManager = authConfig.getAuthManager();

        // db setup
        DatabaseConfig db = new DatabaseConfig();
        UserRepository userRepositoryJdbc = new UserRepositoryJdbc(db);
        UnidadeRepository unidadeRepositoryJdbc = new UnidadeRepositoryJdbc(db);

        // use cases instaces
        ListUsersUseCase listUsersUseCase = new ListUsersUseCase();
        UserRegisterUseCase userRegisterUseCase = new UserRegisterUseCase();

        app.post("/user-registration", ctx -> {
            var jwtToken = ctx.header("Authorization");
            if (jwtToken == null) {
                throw new UnauthorizedResponse("Authorization header is missing!");
            }
            DecodedJWT decodedJwt = null;
            try {
                decodedJwt = JWTUtil.verifyToken(jwtToken);
            } catch (JWTVerificationException e) {
                throw new UnauthorizedResponse(e.getMessage());
            }
            var currentUserEmail = decodedJwt.getClaim("sub").asString();
            var currentUser = userRepositoryJdbc.findByEmail(currentUserEmail);
            if (currentUser.isEmpty()) {
                throw new ForbiddenResponse("Some error in JWT occurred!");
            }
            if (authManager.authorize("register user", currentUser.get(), null)) {
                String body = ctx.body();
                UserRegisterReqDTO req = gsonMapper.fromJsonString(body, UserRegisterReqDTO.class);
                GenericResDTO res = userRegisterUseCase.execute(req);
                ctx.json(res).status(res.getHttpStatus());
            } else {
                throw new ForbiddenResponse("Denied access!");
            }
        });

        app.get("/users", ctx -> {
            List<User> response = listUsersUseCase.execute();
            ctx.json(response);
        });

        app.get("/users/findByEmail", ctx -> {
            String email = ctx.queryParam("email");
            Optional<User> user = userRepositoryJdbc.findByEmail(email);
            if (user.isEmpty()) {
                throw new NotFoundResponse("User with e-mail " + email + " was not found");
            }
            ctx.json(user.get());
        });

        app.post("/login", ctx -> {
            String email = ctx.queryParam("email");
            Optional<User> user = userRepositoryJdbc.findByEmail(email);
            if (user.isEmpty()) {
                throw new UnauthorizedResponse("User not exists!");
            }
            String jwtToken = JWTUtil.generateToken(user.get());
            ctx.json(new TokenResponseDTO(jwtToken));
        });

        app.get("/unidades", ctx -> {
            List<Unidade> allUnidades = unidadeRepositoryJdbc.findAll();
            ctx.json(allUnidades);
        });

        // register unidade for auth user
        app.post("/unidades", ctx -> {
            // ger current user
            var jwtToken = ctx.header("Authorization");
            if (jwtToken == null) {
                throw new UnauthorizedResponse("Authorization header is missing!");
            }
            var decodedJwt = JWTUtil.verifyToken(jwtToken);
            var currentUserEmail = decodedJwt.getClaim("sub").asString();
            logger.info(currentUserEmail);
            var currentUser = userRepositoryJdbc.findByEmail(currentUserEmail);
            logger.info(currentUser.toString());
            if (currentUser.isEmpty()) {
                throw new ForbiddenResponse("Some error in JWT occurred!");
            }
            // check authorization with auth manager
            if (authManager.authorize("register unidade", currentUser.get(), null)) {
                // use-case orquestration
                UnidadeRegisterReqDTO req = ctx.bodyAsClass(UnidadeRegisterReqDTO.class);
                Unidade unidade = new Unidade(null, req.getTitle());
                unidade.setOwner(currentUser.get());
                unidadeRepositoryJdbc.save(unidade);
                ctx.result("Unidade create for auth user!");
            } else {
                throw new ForbiddenResponse("Denied access!");
            }
        });

        // register product for a unidade
        app.post("/unidades/{unidadeId}/products", ctx -> {
            // ger current user
            var jwtToken = ctx.header("Authorization");
            if (jwtToken == null) {
                throw new UnauthorizedResponse("Authorization header is missing!");
            }
            var decodedJwt = JWTUtil.verifyToken(jwtToken);
            var currentUserEmail = decodedJwt.getClaim("sub").asString();
            var currentUser = userRepositoryJdbc.findByEmail(currentUserEmail);
            if (currentUser.isEmpty()) {
                throw new ForbiddenResponse("Some error in JWT occurred!");
            }
            // get unidade from path varible
            var unidadeId = Long.valueOf(ctx.pathParam("unidadeId"));
            var unidadeParam = unidadeRepositoryJdbc.findById(unidadeId);
            if (unidadeParam.isEmpty()) {
                throw new NotFoundResponse("Unidade not exists!");
            }
            // check authorization with auth manager
            if (authManager.authorize("registrar produto", currentUser.get(), unidadeParam.get())) {
                ctx.result("You are allowed!");
            } else {
                throw new ForbiddenResponse("Denied access!");
            }
        });

        // get products for unidade
        app.get("/unidades/{unidadeId}/products", ctx -> {
            ctx.result("Hello: " + ctx.pathParam("unidadeId"));
        });
    }

    // private static Long LID() {
    //     long leftLimit = 1L;
    //     long rightLimit = 100L;
    //     long generatedLong = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
    //     return Long.valueOf(generatedLong);
    // }

    // var u1 = new Unidade(1L, "Unidade I");
    // var u2 = new Unidade(2L, "Unidade II");
    // var u3 = new Unidade(3L, "Unidade III");
    // List<Unidade> unidades = new ArrayList<>();
    // unidades.add(u1);
    // unidades.add(u2);
    // unidades.add(u3);

    // var s1 = new User(1L, "john@email.com", "John Doe", List.of("gerente", "operador"));
    // s1.addUnidade(u1);
    // s1.addUnidade(u2);
    // var s2 = new User(2L, "maria@email.com", "Maria Doe", List.of("admin", "gerente"));
    // s1.addUnidade(u3);
    // List<User> users = new ArrayList<>();
    // users.add(s1);
    // users.add(s2);
}
