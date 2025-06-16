package com.example.eventapp.repository;

import com.example.eventapp.model.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    List<Interest> findByUserId(String userId);
    boolean existsByUserIdAndEventId(String userId, String eventId);
    void deleteByUserIdAndEventId(String userId, String eventId);
    long countByEventId(String eventId);

}
