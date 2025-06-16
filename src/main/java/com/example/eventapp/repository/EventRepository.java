package com.example.eventapp.repository;

import com.example.eventapp.model.EventDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventRepository extends MongoRepository<EventDocument,String>{



}