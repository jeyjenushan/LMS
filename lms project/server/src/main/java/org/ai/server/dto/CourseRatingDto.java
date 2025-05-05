package org.ai.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRatingDto {
    private Long id;
    private Long userId;
    private int rating;

}
