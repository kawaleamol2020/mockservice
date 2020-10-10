package org.aask.repository;

import java.sql.Timestamp;

import org.aask.entity.MockRequestData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MockRequestRepository extends JpaRepository<MockRequestData, String> {
	
	@Modifying
	@Query(value="UPDATE MockRequestData m SET m.mockId = :newMockId, m.archivedDate=:archivedDate WHERE m.mockId = :mockId AND m.archivedDate IS NULL ",nativeQuery=false)
	int archiveMockRequestData(@Param("mockId") String mockId,@Param("newMockId") String newMockId, @Param("archivedDate") Timestamp archivedDate);
}
