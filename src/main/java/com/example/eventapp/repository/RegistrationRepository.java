package com.example.eventapp.repository;

import com.example.eventapp.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByUserIdAndEventId(Long userId, String eventId);

    Registration findByUserIdAndEventId(Long userId, String eventId);

    java.util.List<Registration> findByUserId(Long userId);

    java.util.List<Registration> findByEventId(String eventId);
}
