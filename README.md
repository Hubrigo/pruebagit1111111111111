# ABC - Migración a microservicios (esqueleto)

Este repositorio contiene una propuesta inicial para migrar la aplicación monolítica de pedidos de ABC hacia una arquitectura basada en microservicios.

## Contenido
- Documentación de arquitectura: `documentacion/arquitectura_microservicios.md`.
- Tres microservicios de ejemplo (Usuarios, Órdenes, Pagos) implementados con **Spring Boot** y expuestos vía API REST.
- Dockerfiles independientes y `docker-compose.yml` para orquestar los servicios junto a MongoDB (persistencia simulada en local).
- `pom.xml` raíz para construir todos los servicios con un solo comando Maven.

### Estructura de carpetas
```
.
├── documentacion/                # Documentación funcional y técnica
├── services/
│   ├── users-service/            # Servicio de usuarios (puerto 8000)
│   ├── orders-service/           # Servicio de órdenes (puerto 8001)
│   └── payments-service/         # Servicio de pagos (puerto 8002)
├── docker-compose.yml            # Orquestación local con MongoDB
├── pom.xml                       # POM raíz para construir todos los módulos
└── README.md
```

## Endpoints disponibles
Cada servicio expone:
- `GET /health`: verificación básica.
- `GET /status`: metadatos del servicio (nombre, tiempo de arranque, mensaje descriptivo).

Puertos por defecto en local:
- Usuarios: `http://localhost:8000`
- Órdenes: `http://localhost:8001`
- Pagos: `http://localhost:8002`

## Ejecución local

### Con Maven (sin contenedores)
1. Requisitos: JDK 17 y Maven 3.9+ instalados.
2. Compilar todos los servicios desde la raíz del repositorio:
   ```bash
   mvn clean package -DskipTests
   ```
3. Levantar un servicio concreto (ejemplo: usuarios):
   ```bash
   mvn -pl services/users-service spring-boot:run
   ```
   Cambia el módulo (`orders-service`, `payments-service`) según el servicio que desees probar.

### Con Docker Compose
1. Construir y levantar servicios más MongoDB:
   ```bash
   docker-compose up --build
   ```
2. Verificar salud (ejemplo usuarios):
   ```bash
   curl http://localhost:8000/health
   ```
3. Detener:
   ```bash
   docker-compose down
   ```

> Las imágenes pueden subirse a Docker Hub ejecutando `docker build` y `docker push` por servicio si se requiere.
