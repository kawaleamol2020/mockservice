package org.aask.repository;

import org.aask.entity.MockType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MockTypeRepository extends CrudRepository<MockType, Long> {

}
