package com.tabletrack.table_track_API.repository;

import com.tabletrack.table_track_API.model.entity.product_import.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ImportRepository extends JpaRepository<Import,String> {
}
