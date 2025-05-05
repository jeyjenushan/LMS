package org.ai.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ai.server.model.UserEntity;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrolledStudentsDto {

    private String courseTitle;
    private UserEntity student;
    private Date purchaseDate;

}
