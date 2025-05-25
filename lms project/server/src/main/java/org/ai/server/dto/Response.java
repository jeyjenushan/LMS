package org.ai.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ai.server.enumpackage.ROLE;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private String message;
    private String stripeUrl;
    private int statusCode;
    private String token;
    private UserDto userDto;
    private List<UserDto> userDtoList;
    private CourseDto courseDto;
    private List<CourseDto> courseDtoList;
    private boolean success;
    private LectureDto lectureDto;
    private List<LectureDto> lectureDtoList;
    private ChapterDto chapterDto;
    private List<ChapterDto> chapterDtoList;
    private String expirationTime;
    private EducatorDashboardDataDto educatorDashboardDataDto;
    private List<EducatorDashboardDataDto> educatorDashboardDataDtoList;
    private EnrolledStudentsDto enrolledStudentsDto;
    private List<EnrolledStudentsDto> enrolledStudentsDtoList;
    public Object data;
    private PurchaseDto purchaseDto;
    private CourseProgressDto courseProgressDto;
    private CourseRatingDto courseRatingDto;


}
