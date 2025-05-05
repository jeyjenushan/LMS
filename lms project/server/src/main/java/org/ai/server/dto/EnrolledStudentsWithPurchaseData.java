package org.ai.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ai.server.model.UserEntity;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrolledStudentsWithPurchaseData {
    private UserEntity student;
    private String courseTitle;
    private Date purchaseDate;
}
