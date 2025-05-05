package org.ai.server.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ai.server.enumpackage.Status;
import org.ai.server.model.CourseEntity;
import org.ai.server.model.UserEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDto {

    private Long id;

    private CourseEntity course;

    private UserEntity user;

    private Double amount;

    private Status status = Status.PENDING;
}
