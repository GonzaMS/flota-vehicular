package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.travel.AssignedOrderDTO;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.models.Travel.AssignedOrder;

public interface IAssignedOrderService {

    AssignedOrderDTO getAssignedOrderById(Long id);

    AssignedOrder saveAssignedOrder(AssignedOrderDTO assignedOrderDTO, String token);

    AssignedOrderDTO updateAssignedOrder(Long id, AssignedOrderDTO assignedOrderDTO,String token);

    void deleteAssignedOrder(Long id);

    PageResponse<AssignedOrderDTO> getAllAssignedOrders(int pageNumber, int pageSize);
}
