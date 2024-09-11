package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.DrivingHistoryDTO;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;

public interface IDrivingHistoryService {

    DrivingHistoryDTO getDrivingHistoryById(Long id);

    void saveDrivingHistory(DrivingHistoryDTO drivingHistoryDTO);

    void updateDrivingHistory(Long id, DrivingHistoryDTO drivingHistoryDTO);

    void deleteDrivingHistory(Long id);

    PageResponse<DrivingHistoryDTO> getAllDrivingHistories(int pageNumber, int pageSize);

    PageResponse<DrivingHistoryDTO> getDrivingHistoryByDriverId(Long driverId, int pageNumber, int pageSize);

    PageResponse<DrivingHistoryDTO> getDrivingHistoryByCarId(Long carId, int pageNumber, int pageSize);
}
