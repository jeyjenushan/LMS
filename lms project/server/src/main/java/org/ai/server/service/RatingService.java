package org.ai.server.service;

import org.ai.server.dto.Response;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface RatingService {
     Response addRating(String email, Long courseId, Integer rating);

}
