package com.ept.gestion.procurement.repository;

import com.ept.gestion.procurement.entity.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Long> {
    List<Livraison> findByLotId(Long lotId);
}
