package com.example.eventapp.service;

import com.example.eventapp.model.Interest;
import com.example.eventapp.repository.InterestRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterestService {

    @Autowired
    private InterestRepository interestRepository;

    @Transactional
    public void toggleInterest(String userId, String eventId) {
        if (interestRepository.existsByUserIdAndEventId(userId, eventId)) {
            interestRepository.deleteByUserIdAndEventId(userId, eventId);
        } else {
            Interest interest = new Interest();
            interest.setUserId(userId);
            interest.setEventId(eventId);
            interestRepository.save(interest);
        }
    }

    public List<Interest> getInterestsByUser(String userId) {
        return interestRepository.findByUserId(userId);
    }
}
