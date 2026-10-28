package com.ept.gestion.procurement.repository;

import com.ept.gestion.procurement.entity.LigneOffre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneOffreRepository extends JpaRepository<LigneOffre, Long> {
    List<LigneOffre> findByOffreId(Long offreId);
}
