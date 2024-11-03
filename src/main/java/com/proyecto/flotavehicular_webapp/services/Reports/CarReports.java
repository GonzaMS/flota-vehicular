package com.proyecto.flotavehicular_webapp.services.Reports;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.proyecto.flotavehicular_webapp.models.Car.Car;
import com.proyecto.flotavehicular_webapp.models.Car.Kilometers;
import com.proyecto.flotavehicular_webapp.models.Car.MaintenanceHistory;
import com.proyecto.flotavehicular_webapp.repositories.ICarRepository;
import com.proyecto.flotavehicular_webapp.repositories.IKilometersRepository;
import com.proyecto.flotavehicular_webapp.repositories.IMaintenanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class CarReports {

    private final ICarRepository carRepository;
    private final IKilometersRepository kilometersRepository;
    private final IMaintenanceRepository maintenanceHistoryRepository;

    public CarReports(ICarRepository carRepository, IKilometersRepository kilometersRepository, IMaintenanceRepository maintenanceHistoryRepository) {
        this.carRepository = carRepository;
        this.kilometersRepository = kilometersRepository;
        this.maintenanceHistoryRepository = maintenanceHistoryRepository;
    }

    public byte[] generateCarReport() throws IOException {
        log.info("Iniciando la generación del reporte de autos.");

        List<Car> carList = carRepository.findAll(); // Obtener todos los autos
        log.info("Total de autos encontrados: {}", carList.size());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Título del reporte
        document.add(new Paragraph("Reporte de Autos").setBold().setFontSize(20));
        document.add(new Paragraph(" ")); // Espacio

        // Crear una tabla para mostrar la información de los autos
        Table table = new Table(4); // 4 columnas: Matrícula, Marca, Modelo, Kilometraje
        table.addHeaderCell("Matrícula");
        table.addHeaderCell("Marca");
        table.addHeaderCell("Modelo");
        table.addHeaderCell("Kilometraje Actual");

        // Llenar la tabla con información de los autos
        for (Car car : carList) {
            log.info("Procesando auto: {}", car.getLicensePlate());

            table.addCell(car.getLicensePlate());
            table.addCell(car.getBrand());
            table.addCell(car.getModel());

            // Obtener el kilometraje actual
            List<Kilometers> currentKilometers = kilometersRepository.findByCarId(car.getId());
            int actualKm = (currentKilometers != null && !currentKilometers.isEmpty()) ? currentKilometers.get(0).getActualKm() : 0; // Valor por defecto
            table.addCell(String.valueOf(actualKm)); // Asegúrate de que el kilometraje sea un String

            // Obtener el historial de mantenimiento
            List<MaintenanceHistory> maintenanceHistories = maintenanceHistoryRepository.findByCarId(car.getId());
            if (!maintenanceHistories.isEmpty()) {
                document.add(new Paragraph("Historial de Mantenimiento para: " + car.getLicensePlate()));
                for (MaintenanceHistory maintenance : maintenanceHistories) {
                    document.add(new Paragraph("Descripción: " + maintenance.getDescription()));
                    document.add(new Paragraph("Costo: " + maintenance.getCost()));
                    document.add(new Paragraph("Tipo: " + maintenance.getType()));
                    document.add(new Paragraph("Fecha: " + maintenance.getCreatedAt()));
                    document.add(new Paragraph(" ")); // Espacio entre mantenimientos
                }
            } else {
                document.add(new Paragraph("No hay historial de mantenimiento para este auto."));
                log.info("No hay historial de mantenimiento para el auto: {}", car.getLicensePlate());
            }
        }

        // Agregar la tabla al documento
        document.add(table);
        document.close();

        log.info("Reporte de autos generado exitosamente.");
        return outputStream.toByteArray();
    }
}
