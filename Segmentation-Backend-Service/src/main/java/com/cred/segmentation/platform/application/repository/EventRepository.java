package com.cred.segmentation.platform.application.repository;

import com.cred.segmentation.platform.application.entity.EventStream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<EventStream, Long> {
}
