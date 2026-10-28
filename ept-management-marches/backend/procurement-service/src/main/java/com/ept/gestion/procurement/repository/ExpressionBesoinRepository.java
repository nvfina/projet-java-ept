package com.ept.gestion.procurement.repository;

import com.ept.gestion.common.enums.StatutBesoin;
import com.ept.gestion.procurement.entity.ExpressionBesoin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpressionBesoinRepository extends JpaRepository<ExpressionBesoin, Long> {

    Optional<ExpressionBesoin> findByReference(String reference);

    Page<ExpressionBesoin> findByDemandeurId(Long demandeurId, Pageable pageable);

    Page<ExpressionBesoin> findByStatut(StatutBesoin statut, Pageable pageable);

    List<ExpressionBesoin> findByStatut(StatutBesoin statut);

    Page<ExpressionBesoin> findByServiceDemandeur(String serviceDemandeur, Pageable pageable);

    long countByStatut(StatutBesoin statut);

    @Query("SELECT b FROM ExpressionBesoin b WHERE " +
           "(:service IS NULL OR LOWER(b.serviceDemandeur) LIKE LOWER(CONCAT('%', :service, '%'))) AND " +
           "(:statut IS NULL OR b.statut = :statut) AND " +
           "(:demandeurId IS NULL OR b.demandeurId = :demandeurId)")
    Page<ExpressionBesoin> rechercher(@Param("service") String service,
                                      @Param("statut") StatutBesoin statut,
                                      @Param("demandeurId") Long demandeurId,
                                      Pageable pageable);
}
