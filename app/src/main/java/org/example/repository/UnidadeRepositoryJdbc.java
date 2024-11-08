package org.example.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.config.DatabaseConfig;
import org.example.entity.Unidade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnidadeRepositoryJdbc implements UnidadeRepository {

    private static final Logger log = LoggerFactory.getLogger(UnidadeRepositoryJdbc.class);

    private final DatabaseConfig db;

    public UnidadeRepositoryJdbc(DatabaseConfig db) {
        this.db = db;
    }

    @Override
    public void save(Unidade unidade) {
        String sql = "INSERT INTO unidade (title, user_id) VALUES (?, ?)";
        try (Connection connection = db.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, unidade.getTitle());
            statement.setLong(2, unidade.getOwner().getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public List<Unidade> findAll() {
        String sql = "SELECT id, title FROM unidade";
        List<Unidade> unidades = new ArrayList<>();
        try (Connection connection = db.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                var unidade = new Unidade(
                    rs.getLong("id"),
                    rs.getString("title")
                );
                unidades.add(unidade);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return unidades;
    }

    @Override
    public Optional<Unidade> findById(Long id) {
        String sql = "SELECT id, title FROM unidade WHERE id = ?";
        try (Connection connection = db.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong('1', id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                var unidade = new Unidade(
                    rs.getLong("id"),
                    rs.getString("title")
                );
                return Optional.of(unidade);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }
}
