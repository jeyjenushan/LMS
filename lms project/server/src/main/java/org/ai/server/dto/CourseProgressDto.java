package org.ai.server.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ai.server.model.CourseEntity;
import org.ai.server.model.LectureEntity;
import org.ai.server.model.UserEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseProgressDto {

    private Long id;
    private UserDto user;
    private CourseDto course;
    private Boolean completed = false;
    private int completedCount ;
    private List<LectureDto> completedLectures = new ArrayList<>();
}
