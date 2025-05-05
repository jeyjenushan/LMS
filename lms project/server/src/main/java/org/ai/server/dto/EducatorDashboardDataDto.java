package org.ai.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EducatorDashboardDataDto {
    private double totalEarnings;
    private List<EnrolledStudentsDto> enrolledStudentsDtoList;
    private int totalCourses;
}
