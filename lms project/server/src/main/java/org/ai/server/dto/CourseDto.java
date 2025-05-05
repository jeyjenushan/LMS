package org.ai.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ai.server.model.UserEntity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
    private Long id;
    private String courseTitle;
    private String courseDescription;
    private String thumbnailUrl;
    private double coursePrice;
    private boolean isPublished;
    private double discount;
    private UserDto educator;
    private List<ChapterDto> chapters;
    private List<CourseRatingDto> courseRatings;
    private List<UserDto> enrolledStudents;
    private Date updatedAt;
    private Date createdAt;





}
