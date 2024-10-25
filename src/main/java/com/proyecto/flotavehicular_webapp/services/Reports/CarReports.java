package com.proyecto.flotavehicular_webapp.services.Reports;

import com.proyecto.flotavehicular_webapp.dto.CarReportWrapper;
import com.proyecto.flotavehicular_webapp.models.Car.Car;
import com.proyecto.flotavehicular_webapp.models.Car.Kilometers;
import com.proyecto.flotavehicular_webapp.models.Car.MaintenanceHistory;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.repositories.IKilometersRepository;
import com.proyecto.flotavehicular_webapp.repositories.IMaintenanceRepository;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CarReports {

    private final ICarRepository carRepository;
    private final IMaintenanceRepository maintenanceHistoryRepository;
    private final IKilometersRepository kilometersRepository;

    public CarReports(ICarRepository carRepository,
                      IMaintenanceRepository maintenanceHistoryRepository,
                      IKilometersRepository kilometersRepository) {
        this.carRepository = carRepository;
        this.maintenanceHistoryRepository = maintenanceHistoryRepository;
        this.kilometersRepository = kilometersRepository;
    }

    public void exportReport(HttpServletResponse response, int pageNumber, int pageSize) throws JRException, IOException {
        // Obtén todos los vehículos (o utiliza paginación si es necesario)
        List<Car> vehicles = carRepository.findAll();

        // Define la paginación
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // Mapea cada vehículo a su envoltura con mantenimientos y kilómetros
        List<CarReportWrapper> carReportWrappers = vehicles.stream().map(vehicle -> {
            List<MaintenanceHistory> maintenanceHistories = maintenanceHistoryRepository.findByCarId(vehicle.getId(), pageable).getContent();
            List<Kilometers> kilometers = kilometersRepository.findByCarId(vehicle.getId(), pageable).getContent();

            CarReportWrapper carReportWrapper = new CarReportWrapper();
            carReportWrapper.setCar(vehicle);
            carReportWrapper.setMaintenanceHistories(maintenanceHistories);
            carReportWrapper.setKilometers(kilometers);

            return carReportWrapper;
        }).collect(Collectors.toList());

        // Verifica que los datos se hayan obtenido correctamente
        if (carReportWrappers.isEmpty()) {
            throw new JRException("No se encontraron datos para generar el reporte.");
        }

        // Carga la plantilla .jrxml
        try (InputStream templateStream = getClass().getResourceAsStream("/reports/car_reports.jrxml")) {
            if (templateStream == null) {
                throw new JRException("No se encontró el archivo de plantilla de reporte.");
            }

            // Compila el archivo .jrxml
            JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);

            // Prepara el datasource con los carReportWrappers
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(carReportWrappers);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("createdBy", "Sistema de Gestión de Vehículos");

            // Llena el reporte con los datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Configura la respuesta HTTP para enviar el PDF al cliente
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_vehiculos.pdf");

            // Exporta el reporte como un flujo PDF a la respuesta HTTP
            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
        } catch (JRException | IOException e) {
            e.printStackTrace();
            // En caso de error, puedes devolver un mensaje de error al cliente
            response.setContentType("text/html");
            response.getWriter().write("<h1>Error al generar el reporte</h1>");
        }
    }
}
