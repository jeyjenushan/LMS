package org.ai.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterDto {
    private Long chapterId;
    private int order;
    private String title;
    private List<LectureDto> lectures;
}
