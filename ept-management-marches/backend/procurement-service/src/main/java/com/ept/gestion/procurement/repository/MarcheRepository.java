package com.ept.gestion.procurement.repository;

import com.ept.gestion.common.enums.StatutMarche;
import com.ept.gestion.procurement.entity.Marche;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarcheRepository extends JpaRepository<Marche, Long> {

    Optional<Marche> findByReference(String reference);

    Page<Marche> findByStatut(StatutMarche statut, Pageable pageable);

    List<Marche> findByStatut(StatutMarche statut);

    long countByStatut(StatutMarche statut);

    @Query("SELECT m FROM Marche m WHERE " +
           "(:recherche IS NULL OR LOWER(m.reference) LIKE LOWER(CONCAT('%', :recherche, '%')) OR LOWER(m.objet) LIKE LOWER(CONCAT('%', :recherche, '%'))) AND " +
           "(:statut IS NULL OR m.statut = :statut)")
    Page<Marche> rechercher(@Param("recherche") String recherche,
                            @Param("statut") StatutMarche statut,
                            Pageable pageable);
}
