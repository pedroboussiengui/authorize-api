package org.example.repository;

import java.util.List;
import java.util.Optional;

import org.example.entity.Unidade;

public interface UnidadeRepository {
    void save(Unidade unidade);
    List<Unidade> findAll();
    Optional<Unidade> findById(Long id);
}
