package com.ept.gestion.procurement.repository;

import com.ept.gestion.procurement.entity.HistoriqueStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueStatutRepository extends JpaRepository<HistoriqueStatut, Long> {
    List<HistoriqueStatut> findByEntiteTypeAndEntiteIdOrderByDateChangementDesc(String entiteType, Long entiteId);
}
