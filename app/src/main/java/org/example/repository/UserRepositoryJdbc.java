package org.example.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.config.DatabaseConfig;
import org.example.entity.Role;
import org.example.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRepositoryJdbc implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepositoryJdbc.class);

    private final DatabaseConfig db;

    public UserRepositoryJdbc(DatabaseConfig db) {
        this.db = db;
    }

    // this should be a transaction becasue inserts in 3 diferent tables
    @Override
    public void save(User user) {
        String userInsertSql = "INSERT INTO users (name, email) VALUES (?, ?)";
        String userRoleInsertSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";

        Connection connection = null;
        try {
            connection = db.getConnection();
            connection.setAutoCommit(false); // inicia a transaction
            
            // user insert
            Long userId = null;
            try (PreparedStatement statement = connection.prepareStatement(userInsertSql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, user.getName());
                statement.setString(2, user.getEmail());
                statement.executeUpdate();

                // Obtém o ID gerado
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    userId = generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Erro ao inserir usuário, ID não foi gerado.");
                }
            }
            // faz a inserção pra cada role do usuário
            try (PreparedStatement statement = connection.prepareStatement(userRoleInsertSql)) {
                statement.setLong(1, userId); // Define userId apenas uma vez
                for (String role : user.getRoles()) {
                    Long roleId = Role.fromString(role).id;
                    statement.setLong(2, roleId);
                    statement.addBatch();
                }
                statement.executeBatch();
            }
            
            // Confirma a transação se tudo foi bem-sucedido
            connection.commit();
        } catch (SQLException | IllegalArgumentException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    log.error("Transação revertida devido a erro: {}", e.getMessage());
                } catch (SQLException rollbackEx) {
                    log.error("Erro ao fazer rollback: {}", rollbackEx.getMessage());
                }
            }
        } finally {
            // Restaura o auto-commit e fecha a conexão
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException closeEx) {
                    log.error("Erro ao fechar a conexão: {}", closeEx.getMessage());
                }
            }
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = """
            SELECT u.id, u.name, u.email, r.name AS role_name
            FROM users u
            LEFT JOIN user_roles ur ON u.id = ur.user_id
            LEFT JOIN roles r ON ur.role_id = r.id
            WHERE u.email = ?
        """;
        try (Connection connection = db.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            User user = null;
            while (rs.next()) {
                if (user == null) {
                    user = new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        new ArrayList<>()
                    );
                }
                String roleName = rs.getString("role_name");
                if (roleName != null && !user.getRoles().contains(roleName)) {
                    user.getRoles().add(roleName);
                }
            }
            if (user != null) {
                return Optional.of(user);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }


    @Override
    public List<User> findAll() {
        String sql = """
            SELECT u.id, u.name, u.email, r.name AS role_name
            FROM users u
            LEFT JOIN user_roles ur ON u.id = ur.user_id
            LEFT JOIN roles r ON ur.role_id = r.id
        """;
        Map<Long, User> userMap = new HashMap<>();
        try (Connection connection = db.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Long userId = rs.getLong("id");
                User user = userMap.computeIfAbsent(userId, id -> createUserFromResultSet(rs));
                String roleName = rs.getString("role_name");
                if (roleName != null && !user.getRoles().contains(roleName)) {
                    user.getRoles().add(roleName);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return new ArrayList<>(userMap.values());
    }

    private User createUserFromResultSet(ResultSet rs) {
        try {
            return new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                new ArrayList<>()
            );
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error creating User from ResultSet", e);

        }
    }
}
