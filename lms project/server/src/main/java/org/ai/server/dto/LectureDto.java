package org.ai.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LectureDto {
    private Long lectureId;
    private String title;
    private int duration;
    private String videoUrl;
    private boolean isPreview;
    private int order;
}

