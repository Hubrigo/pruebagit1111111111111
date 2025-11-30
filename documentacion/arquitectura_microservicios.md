# Propuesta de migración a microservicios para ABC

## 1. Dominios y módulos
- **Usuarios / Identidad**: autenticación, perfiles, control de acceso.
- **Órdenes**: carrito, creación de pedidos, estado y lifecycle.
- **Pagos**: orquestación de cobros, conciliación y reversos.
- **Catálogo / Productos** (futuro): SKUs, precios, disponibilidad.
- **Notificaciones** (futuro): correos/SMS/Push ligados a eventos.
- **API Gateway**: puerta de entrada, auth, rate limiting, observabilidad.

Cada servicio es independiente, con su propia base de datos y pipeline de despliegue. Los módulos que se comunican solo vía API/eventos son desacoplados (usuarios, órdenes, pagos). Catálogo y notificaciones permanecen fuera del MVP pero alineados al modelo.

## 2. Almacenamiento por servicio
| Servicio | Motor recomendado | Justificación |
| --- | --- | --- |
| Usuarios | PostgreSQL | Relacional fuerte para identidades, claves foráneas, integridad y soporte a consultas complejas de seguridad. |
| Órdenes | PostgreSQL | Transaccionalidad ACID para operaciones críticas y reporting; buen soporte a particionado para escalar. |
| Pagos | MongoDB | Flexibilidad para distintos proveedores, esquemas variables y alta tasa de escritura; documentos inmutables por intento de pago. |
| Catálogo (futuro) | MongoDB o PostgreSQL | Depende del modelo de producto; MongoDB para catálogos flexibles, PostgreSQL si se requieren joins avanzados. |
| Notificaciones (futuro) | Redis Streams o MongoDB | Persistencia ligera y entrega asíncrona. |

### Criterios técnicos considerados
- **Aislamiento de fallos**: cada base de datos es propiedad del servicio.
- **Escalabilidad**: motores que soportan particionado/replicación (PostgreSQL, MongoDB).
- **Modelo de datos**: relaciones fuertes → SQL; eventos y trazas flexibles → NoSQL.
- **Operabilidad**: motores con ecosistema y tooling amplio.

## 3. Comunicación entre servicios
- **Síncrona (consultas y comandos)**: REST para consumo externo y simplicidad; gRPC para llamadas internas de baja latencia y contratos fuertes (p. ej., Órdenes → Usuarios para validar cliente).
- **Asíncrona (eventos de negocio)**: mensajería basada en Kafka (alto volumen, ordering por topic/partition) o RabbitMQ (enrutamiento flexible y patrones work queue). Ejemplos: `order.created`, `payment.authorized`, `order.cancelled`.
- **Criterios de elección**: latencia requerida, acoplamiento, necesidad de reintentos y orden, requisitos de streaming vs. enrutamiento.

## 4. Gestión de cambios entre servicios
- Contratos versionados (OpenAPI/proto) y despliegues canary/blue-green.
- Feature flags para liberar capacidades sin romper clientes.
- Compatibilidad hacia atrás en eventos (añadir campos opcionales, nunca eliminar sin deprecación).
- Observabilidad centralizada (tracing distribuido, métricas por servicio) para detectar regressiones.

## 5. Esqueleto de servicios
Se proveen tres microservicios (usuarios, órdenes, pagos) con endpoints REST básicos (`/health`, `/status`) implementados en **Spring Boot**. Cada servicio incluye su propio Dockerfile y puede orquestarse junto a MongoDB mediante `docker-compose` para pruebas locales.
