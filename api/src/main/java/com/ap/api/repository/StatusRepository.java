package com.ap.api.repository;

import com.ap.api.entity.Status;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusRepository extends CrudRepository<Status, String> {
    public List<Status> findAllByOrderByKeyDesc();
}
