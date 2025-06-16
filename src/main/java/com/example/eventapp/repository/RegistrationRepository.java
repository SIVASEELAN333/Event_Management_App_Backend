package com.example.eventapp.repository;

import com.example.eventapp.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByUserIdAndEventId(Long userId, String eventId);

    Registration findByUserIdAndEventId(Long userId, String eventId);

    java.util.List<Registration> findByUserId(Long userId);

    java.util.List<Registration> findByEventId(String eventId);

    @Query("SELECT r.user.username, COUNT(r) as count FROM Registration r GROUP BY r.user.username ORDER BY count DESC")
    List<Object[]> findTopParticipants();


    long countByEventId(String eventId);
    List<Registration> findByEventIdAndWaitingList(String eventId, boolean waitingList);

    long countByEventIdAndWaitingList(String eventId, boolean b);
    long countByEventIdAndWaitingListFalse(String eventId);

}
