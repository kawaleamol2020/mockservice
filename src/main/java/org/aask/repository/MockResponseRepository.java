package org.aask.repository;

import java.sql.Timestamp;

import org.aask.entity.MockResponseData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MockResponseRepository extends JpaRepository<MockResponseData, String> {

	@Query(value="SELECT r From MockResponseData r WHERE r.archivedDate IS NULL ")
	Page<MockResponseData> findAllActiveData(PageRequest pageable);

	@Modifying
	@Query(value="DELETE From MockResponseData r WHERE r.archivedDate IS NOT NULL ")
	void cleanArchivedData();

	void deleteAllByArchivedDateNotNullAndArchivedDateLessThanEqual(Timestamp archivedDate);
}
