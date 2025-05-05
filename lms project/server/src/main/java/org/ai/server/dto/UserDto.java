package org.ai.server.dto;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ai.server.enumpackage.ROLE;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String name;

    private String image;
    private String email;
    private ROLE role;
    private List<CourseDto> enrolledCourses=new ArrayList<CourseDto>();
}
