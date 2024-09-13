package com.proyecto.flotavehicular_webapp.services;

import com.proyecto.flotavehicular_webapp.dto.AssignedOrderDTO;
import com.proyecto.flotavehicular_webapp.utils.PageResponse;
import com.proyecto.flotavehicular_webapp.models.AssignedOrder;

public interface IAssignedOrderService {

    AssignedOrderDTO getAssignedOrderById(Long id);

    AssignedOrder saveAssignedOrder(AssignedOrderDTO assignedOrderDTO);

    void updateAssignedOrder(Long id, AssignedOrderDTO assignedOrderDTO);

    void deleteAssignedOrder(Long id);

    PageResponse<AssignedOrderDTO> getAllAssignedOrders(int pageNumber, int pageSize);
}
