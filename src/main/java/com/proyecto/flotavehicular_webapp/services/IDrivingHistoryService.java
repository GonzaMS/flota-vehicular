package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.driver.DrivingHistoryDTO;
import com.proyecto.flotavehicular_webapp.models.Driver.DrivingHistory;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;

public interface IDrivingHistoryService {

    DrivingHistoryDTO getDrivingHistoryById(Long id);

    DrivingHistory saveDrivingHistory(DrivingHistoryDTO drivingHistoryDTO, String token);

    DrivingHistoryDTO updateDrivingHistory(Long id, DrivingHistoryDTO drivingHistoryDTO);

    void deleteDrivingHistory(Long id);

    PageResponse<DrivingHistoryDTO> getAllDrivingHistories(int pageNumber, int pageSize);

    PageResponse<DrivingHistoryDTO> getDrivingHistoryByDriverId(Long driverId, int pageNumber, int pageSize);

    PageResponse<DrivingHistoryDTO> getDrivingHistoryByCarId(Long carId, int pageNumber, int pageSize);
}
