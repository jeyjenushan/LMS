package org.ai.server.service;

import org.ai.server.dto.EducatorDashboardDataDto;
import org.ai.server.dto.EnrolledStudentsWithPurchaseData;
import org.ai.server.dto.Response;

import java.util.List;

public interface EducatorService {
    Response getEducatorCourse( String email);
    Response getDashboardData(String email);
Response getEnrolledStudentsWithPurchaseData(String email);


}
