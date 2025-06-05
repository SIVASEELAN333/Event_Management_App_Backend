package com.example.eventapp.repository;

import com.example.eventapp.model.EventDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<EventDocument,String>{
        }