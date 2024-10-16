# Proyecto de Control de Flota Vehicular

## Descripción
El **Proyecto de Control de Flota Vehicular** es un sistema diseñado para gestionar, coordinar y supervisar una flota de vehículos utilizados por una empresa. Permite realizar un seguimiento de los vehículos, conductores y órdenes de viaje, así como gestionar mantenimientos, incidencias, y evaluar el desempeño de los conductores.

## Integrantes
- **Cristhian Marecos**
- **Jorge Candia**
- **Jose Diaz**

## Funcionalidades Principales

### Módulo de Gestión de Vehículos
- Registro, edición, eliminación y consulta de vehículos.
- Historial de mantenimiento de vehículos.
- Registro y consulta de incidencias de vehículos.
- Seguimiento de kilometrajes.
- Generación de reportes de vehículos.

### Módulo de Gestión de Conductores
- Registro, edición, eliminación y consulta de conductores.
- Historial de conducción.
- Registro de incidentes y multas.
- Evaluaciones de desempeño.
- Asignación de vehículos a conductores.

### Módulo de Gestión de Órdenes de Viaje
- Creación, edición, cancelación y consulta de órdenes de viaje.
- Asignación de vehículos y conductores a las órdenes de viaje.
- Registro y seguimiento de itinerarios.
- Observaciones y notas para cada orden de viaje.

## Tecnologías Utilizadas
- **Backend**: Spring Boot (con JPA, Hibernate)
- **Base de Datos**: PostgreSQL
- **Frontend**: React
- **Dependencias**: Maven

## Requisitos Previos

1. **Java 17+**
2. **PostgreSQL**
3. **Maven**: Para gestionar las dependencias del proyecto.

## Configuración de la Base de Datos

1. En el archivo `src/main/resources/application.properties` o `application.yml`, configura los siguientes parámetros para la conexión a la base de datos:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/{database_name}
spring.datasource.username=username
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
