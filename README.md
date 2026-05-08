<div align="center">

#  Mewtwo-Code — Microservicio de Feed y Búsqueda (M06)

### *"Descubre tu próximo parche — rápido, relevante y a un clic"*

---

###  Stack Tecnológico

![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql&logoColor=white)

###  Infraestructura & Calidad

![Redis](https://img.shields.io/badge/Redis-Cache-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Container-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

###  Arquitectura

![Hexagonal](https://img.shields.io/badge/Architecture-Hexagonal-blueviolet?style=for-the-badge)
![Clean Architecture](https://img.shields.io/badge/Clean-Architecture-blue?style=for-the-badge)
![REST API](https://img.shields.io/badge/REST-API-009688?style=for-the-badge)

</div>

---

##  Tabla de Contenidos

1. [ Integrantes](#1--integrantes)
2. [ Objetivo del Microservicio](#2--objetivo-del-microservicio)
3. [ Funcionalidades Principales](#3--funcionalidades-principales)
4. [ Estrategia de Versionamiento y Branches](#4--manejo-de-estrategia-de-versionamiento-y-branches)
    - [4.1 Convenciones para crear ramas](#41-convenciones-para-crear-ramas)
    - [4.2 Convenciones para crear commits](#42-convenciones-para-crear-commits)
5. [ Tecnologías Utilizadas](#5--tecnologias-utilizadas)
6. [ Funcionalidad](#6--funcionalidad)
7. [ Diagramas](#7--diagramas)
8. [ Manejo de Errores](#8--manejo-de-errores)
9. [ Evidencia de Pruebas y Ejecución](#9--evidencia-de-las-pruebas-y-como-ejecutarlas)
10. [ Scaffolding](#10--scaffolding-del-microservicio)
11. [ Ejecución del Proyecto](#11--ejecucion-del-proyecto)
12. [ CI/CD y Despliegue](#12--cicd-y-despliegue)
13. [ Contribuciones](#13--contribuciones)

---

## 1.  Integrantes

- Juan Esteban Rodriguez
- Fabian Andrade
- Diego Rozo
- Juan David Gomez
- Adrian Ducuara

---

## 2.  Objetivo del Microservicio

El microservicio de **Feed y Búsqueda ** tiene por función principal convertirse en el punto de descubrimiento de parches de la plataforma PATRIC.IA, siendo que el microservicio construye y expone un feed de parches públicos y activos personalizados y ordenados por relevancia, permite la búsqueda y filtrado en tiempo real con respuesta a las peticiones de búsqueda inferior a 1 segundo, ofrece recomendaciones basadas en los intereses de los usuarios y, además, gestiona la acción de unirse a un parche público a través del feed . Este microservicio corre sobre el puerto `8081`, se encuentra integrado con Redis para caché distribuida y PostgreSQL como su base de datos principal.

---

## 3.  Funcionalidades Principales

<div align="center">

<table>
  <thead>
    <tr>
      <th> Funcionalidad</th>
      <th>Descripción</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><strong>Feed Personalizado </strong></td>
      <td>Retorna parches activos y públicos ordenados por un score de relevancia que combina intereses del usuario (50%), cercanía temporal (20%) y proximidad geográfica (30%). Soporta paginación.</td>
    </tr>
    <tr>
      <td><strong>Registro de Interacciones</strong></td>
      <td>Registra acciones del usuario sobre parches (VIEW, JOIN, SKIP) para alimentar el motor de recomendaciones y personalizar el feed.</td>
    </tr>
    <tr>
      <td><strong>Búsqueda y Filtrado </strong></td>
      <td>Búsqueda dinámica de parches públicos por texto libre, categoría, zona del campus, estado, rango de fechas, tamaño de grupo y disponibilidad de cupos. Respuesta garantizada en menos de 1 segundo.</td>
    </tr>
    <tr>
      <td><strong>Recomendaciones </strong></td>
      <td>Retorna hasta 10 parches recomendados basados en historial e intereses del usuario. Para usuarios nuevos retorna los más populares del campus.</td>
    </tr>
    <tr>
      <td><strong>Unirse a un Parche </strong></td>
      <td>Permite al usuario unirse a un parche público con cupo disponible directamente desde el feed, validando reglas de negocio antes de crear la membresía.</td>
    </tr>
  </tbody>
</table>

</div>

---

## 4.  Manejo de Estrategia de Versionamiento y Branches

### Estrategia de Ramas (Git Flow)

#### `main`
- **Propósito:** Rama estable con la versión final lista para demo/producción.
- **Reglas:** Solo recibe merges desde `release/*` y `hotfix/*`. Cada merge crea un tag SemVer (`vX.Y.Z`). Rama protegida con PR obligatorio y checks de CI en verde.

#### `develop`
- **Propósito:** Integración continua de trabajo; base de nuevas funcionalidades.
- **Reglas:** Recibe merges desde `feature/*` y `release/*`. Rama protegida.

#### `feature/*`
- **Propósito:** Desarrollo de una funcionalidad, refactor o spike.
- **Base:** `develop`. Se fusiona a `develop` mediante PR.


---

### 4.1 Convenciones para crear ramas

#### `feature/*`
```
feature/[nombre-funcionalidad]
```
**Ejemplos:**
- `feature/feed`
- `feature/inicio`
- `feature/servicediego`
- `feature/Adrian`

**Reglas:** Descripción clara, máximo 50 caracteres.


### 4.2 Convenciones para crear commits

```
[tipo]: [descripción específica de la acción]
```

**Tipos de commit:**
- `feat`: Nueva funcionalidad
- `fix`: Corrección de errores
- `docs`: Cambios en documentación

---

## 5.  Tecnologías Utilizadas

| **Tecnología / Herramienta** | **Uso principal en el proyecto** |
|------------------------------|----------------------------------|
| **Java 21 (OpenJDK)** | Lenguaje base del módulo con soporte para Spring Boot y Virtual Threads para operaciones I/O eficientes. |
| **Spring Boot 3.3.0** | Framework que agrupa JPA, Redis, Security y Swagger en un solo ecosistema sin fricciones de integración. |
| **Spring Web** | Exposición de los 5 endpoints REST del módulo (feed, search, join, interact, recommendations). |
| **Spring Security + JWT (JJWT 0.12.6)** | Protección de endpoints mediante tokens JWT como OAuth2 Resource Server. |
| **Spring Data JPA** | Acceso a PostgreSQL con `JpaSpecificationExecutor` para búsquedas dinámicas y filtros acumulables. |
| **PostgreSQL** | Base de datos relacional principal para parches, membresías, interacciones e intereses de usuario. |
| **Spring Data Redis** | Caché distribuida para resultados de feed y recomendaciones, reduciendo latencia a sub-milisegundo. |
| **Apache Maven** | Gestión de dependencias y automatización de builds en el pipeline CI/CD. |
| **Lombok 1.18.46** | Reducción de boilerplate con `@Getter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`. |
| **H2** | Base de datos en memoria para pruebas unitarias y de integración sin PostgreSQL real. |
| **JUnit 5** | Framework de pruebas unitarias para validar casos de uso, servicios y lógica de dominio. |
| **Mockito** | Simulación de puertos y repositorios en pruebas unitarias sin acceder a infraestructura real. |
| **JaCoCo 0.8.12** | Medición de cobertura de pruebas integrada al pipeline. |
| **SpringDoc OpenAPI 2.5.0** | Generación automática de Swagger UI desde anotaciones `@Operation`, `@ApiResponse` en los controladores. |
| **Postman** | Validación manual de endpoints y colección de ejemplos de uso. |
| **Docker** | Contenedorización del microservicio para garantizar consistencia entre ambientes. |
| **Vercel** | Despliegue y conexión con el backend para generar una demo funcional accesible. |
| **GitHub Actions** | Pipeline de CI que compila, ejecuta tests y construye la imagen Docker en cada push. |
| **SonarQube** | Análisis estático para verificar clean code y detectar deuda técnica. |

---

## 6. Funcionalidades

---

###  Feed Personalizado de Parches 

Retorna parches activos y públicos ordenados por un score de relevancia calculado dinámicamente para cada usuario.

**Endpoint principal:**
`GET /api/v1/feed/parches`

---

###  Estructura de la Solicitud (Request)

<div align="center">

|  Campo |  Tipo |       Restricciones       |  Descripción                |
|---|---|:-------------------------:|-----------------------------|
| userId | UUID | Obligatorio (query param) | ID del usuario autenticado. |
| page | Integer |    Opcional, default 0    | Número de página (base 0).  |
| size | Integer |   Opcional, default 20    | Resultados por página.      |

</div>

---

### Estructura de la Respuesta (Response)

<div align="center">

|  Campo |  Tipo |  Descripción |
|---|---|---|
| id | UUID | Identificador único del parche. |
| title | String | Título del parche. |
| description | String | Descripción del parche. |
| category | Enum | Categoría: STUDY, SPORTS, CULTURE, GAMING, FOOD, OTHER. |
| campusZone | Enum | Zona del campus: BIBLIOTECA, CAFETERIA, CANCHA, SALON, PARQUEADERO, EXTERNO. |
| startTime | LocalDateTime | Fecha y hora de inicio. |
| capacity | Integer | Cupo máximo. |
| currentCount | Integer | Participantes actuales. |
| affinityScore | Double | Score de relevancia para el usuario (0.0 – 1.0). |
| isMember | Boolean | Indica si el usuario ya es miembro. |

</div>

---

###  Happy Path (Ejemplo de Uso Exitoso)

1. El estudiante autenticado accede al feed enviando su `userId`.
2. El sistema consulta Redis para verificar si hay caché vigente.
3. Si no hay caché, se calculan los scores combinando intereses (50%), cercanía temporal (20%) y zona del campus (30%).
4. Los resultados se ordenan por score descendente y se retornan paginados.
5. Se retorna `200 OK` con la lista de parches.

**Request:**
```
GET /api/v1/feed/parches?userId=550e8400-e29b-41d4-a716-446655440001&page=0&size=20
```

**Response:**
```json
[
  {
    "id": "770e8400-e29b-41d4-a716-446655440000",
    "title": "Parche de estudio cálculo",
    "category": "STUDY",
    "campusZone": "BIBLIOTECA",
    "startTime": "2026-06-15T14:00:00",
    "capacity": 10,
    "currentCount": 4,
    "affinityScore": 0.87,
    "isMember": false
  }
]
```

---

###  Tipos de errores manejados

<div align="center">

|  **Código HTTP** |  **Escenario**                     | **Mensaje de Error**     |
|:------------------:|:-----------------------------------|:---------------------------|
| ![401](https://img.shields.io/badge/401-Unauthorized-red?style=flat) | JWT inválido o ausente             | `"JWT inválido o ausente"` |
| ![503](https://img.shields.io/badge/503-Service_Unavailable-critical?style=flat) | Servicio dependiente no disponible | `"SERVICE_UNAVAILABLE"`    |

</div>

---

###  Registrar Interacción con un Parche

Registra una acción del usuario sobre un parche para alimentar el motor de recomendaciones.

**Endpoint principal:**
`POST /api/v1/feed/parches/{patchId}/interact`

---

###  Estructura de la Solicitud (Request)

<div align="center">

|  Campo |  Tipo |       Restricciones       | Descripción |
|---|---|:-------------------------:|---|
| patchId | UUID |    Obligatorio (path)     | ID del parche con el que se interactúa. |
| userId | UUID | Obligatorio (query param) | ID del usuario autenticado. |
| action | Enum |    Obligatorio (body)     | Tipo de interacción: VIEW, JOIN, SKIP. |

</div>

---

###  Happy Path (Ejemplo de Uso Exitoso)

1. El usuario visualiza un parche en el feed → se registra `VIEW`.
2. El usuario omite el parche → se registra `SKIP` (reduce su aparición futura).
3. El usuario se une al parche → se registra `JOIN` (excluye de futuras recomendaciones).
4. Se retorna `204 No Content`.

**Request:**
```json
"POST /api/v1/feed/parches/770e8400-e29b-41d4-a716-446655440000/interact?userId=550e8400-..."

{
  "action": "VIEW"
}
```

---

###  Tipos de errores manejados

<div align="center">

|  **Código HTTP** |  **Escenario**         |  **Mensaje de Error** |
|:------------------:|:-----------------------|:------------------------|
| ![400](https://img.shields.io/badge/400-Bad_Request-red?style=flat) | Acción nula o inválida | `"VALIDATION_ERROR: action: Defina al menos una acción"` |
| ![401](https://img.shields.io/badge/401-Unauthorized-red?style=flat) | JWT inválido o ausente | `"JWT inválido o ausente"` |

</div>

---

###  Búsqueda y Filtrado de Parches (RF18)

Búsqueda dinámica de parches públicos con múltiples filtros acumulables. Cumple RNF01: respuesta < 1 segundo.

**Endpoint principal:**
`GET /api/v1/parches/search`

---

### Estructura de la Solicitud (Request)

<div align="center">

|  Campo |  Tipo |       Restricciones       |  Descripción |
|---|---|:-------------------------:|---|
| userId | UUID | Obligatorio (query param) | ID del usuario que realiza la búsqueda. |
| q | String | Opcional, mínimo 2 chars  | Texto a buscar en título y descripción. |
| category | Enum |         Opcional          | STUDY, SPORTS, CULTURE, GAMING, FOOD, OTHER. |
| campusZone | Enum |         Opcional          | BIBLIOTECA, CAFETERIA, CANCHA, SALON, PARQUEADERO, EXTERNO. |
| status | Enum |         Opcional          | Estado del parche: OPEN, CLOSED, CANCELLED. |
| dateFrom | LocalDate |         Opcional          | Fecha inicio del rango (yyyy-MM-dd). |
| dateTo | LocalDate |         Opcional          | Fecha fin del rango (yyyy-MM-dd). |
| maxGroupSize | Integer |         Opcional          | Capacidad máxima del grupo. |
| hasAvailableSpots | Boolean |         Opcional          | Solo parches con cupos disponibles. |
| page | Integer |    Opcional, default 0    | Número de página. |
| size | Integer |   Opcional, default 20    | Resultados por página. |

</div>

---

###  Estructura de la Respuesta (Response)

<div align="center">

|  Campo |  Tipo |  Descripción |
|---|---|---|
| content | List | Lista de parches que cumplen los filtros. |
| page | Integer | Página actual. |
| size | Integer | Tamaño de página. |
| totalElements | Long | Total de resultados. |
| totalPages | Integer | Total de páginas. |
| hasNext | Boolean | Indica si hay más páginas. |

</div>

---

### Happy Path (Ejemplo de Uso Exitoso)

1. El estudiante envía una búsqueda con filtros opcionales acumulables.
2. El sistema aplica los filtros mediante `JpaSpecificationExecutor` dinámicamente.
3. Se retorna `200 OK` con la lista paginada de parches que cumplen todos los criterios.

**Request:**
```
GET /api/v1/parches/search?userId=550e...&q=fútbol&campusZone=CANCHA&hasAvailableSpots=true&page=0&size=20
```

**Response:**
```json
{
  "content": [
    {
      "id": "770e8400-...",
      "title": "Fútbol 5 tarde",
      "category": "SPORTS",
      "campusZone": "CANCHA",
      "startTime": "2026-06-15T16:00:00",
      "capacity": 10,
      "currentCount": 6
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1,
  "hasNext": false
}
```

---

### Tipos de errores manejados

<div align="center">

|  **Código HTTP** |  **Escenario**                |  **Mensaje de Error** |
|:------------------:|:------------------------------|:------------------------|
| ![400](https://img.shields.io/badge/400-Bad_Request-red?style=flat) | `q` con menos de 2 caracteres | `"VALIDATION_ERROR: q: El término de búsqueda debe tener al menos 2 caracteres"` |
| ![401](https://img.shields.io/badge/401-Unauthorized-red?style=flat) | JWT inválido o ausente        | `"JWT inválido o ausente"` |

</div>

---

###  Recomendaciones de Parches 

Retorna hasta 10 parches recomendados personalizados para el usuario basados en su historial e intereses.

**Endpoint principal:**
`GET /api/v1/feed/recommended`

---

###  Estructura de la Solicitud (Request)

<div align="center">

|  Campo |  Tipo |       Restricciones       | Descripción |
|---|---|:-------------------------:|---|
| userId | UUID | Obligatorio (query param) | ID del usuario autenticado. |

</div>

---

###  Estructura de la Respuesta (Response)

<div align="center">

|  Campo |  Tipo |  Descripción                                               |
|---|---|------------------------------------------------------------|
| patchId | UUID | ID del parche recomendado.                                 |
| patch | PatchSummaryResponse | Datos completos del parche.                                |
| affinityScore | Double | Score de afinidad calculado (0.0 – 1.0).                   |
| reason | String | Razón de la recomendación (ej. "Basado en tus intereses"). |

</div>

---

###  Happy Path (Ejemplo de Uso Exitoso)

1. El estudiante solicita sus recomendaciones.
2. El sistema calcula scores basados en interacciones previas e intereses registrados.
3. Para usuarios nuevos sin historial, retorna los parches más populares del campus.
4. Se retorna `200 OK` con máximo 10 recomendaciones ordenadas por afinidad.

**Request:**
```
GET /api/v1/feed/recommended?userId=550e8400-e29b-41d4-a716-446655440001
```

**Response:**
```json
[
  {
    "patchId": "770e8400-...",
    "patch": { "title": "Jam de música", "category": "CULTURE", "campusZone": "SALON" },
    "affinityScore": 0.92,
    "reason": "Basado en tus intereses"
  }
]
```

---

###  Tipos de errores manejados

<div align="center">

|  **Código HTTP** |  **Escenario**                     |  **Mensaje de Error** |
|:------------------:|:-----------------------------------|:------------------------|
| ![401](https://img.shields.io/badge/401-Unauthorized-red?style=flat) | JWT inválido o ausente             | `"JWT inválido o ausente"` |
| ![503](https://img.shields.io/badge/503-Service_Unavailable-critical?style=flat) | Servicio dependiente no disponible | `"SERVICE_UNAVAILABLE"` |

</div>

---

###  Unirse a un Parche desde el Feed 

Permite al usuario unirse a un parche público con cupo disponible directamente desde el feed.

**Endpoint principal:**
`POST /api/v1/feed/{patchId}/join`

---

###  Estructura de la Solicitud (Request)

<div align="center">

|  Campo |  Tipo |       Restricciones       |  Descripción |
|---|---|:-------------------------:|---|
| patchId | UUID |    Obligatorio (path)     | ID del parche al que desea unirse. |
| userId | UUID | Obligatorio (query param) | ID del usuario autenticado. |

</div>

---

###  Happy Path (Ejemplo de Uso Exitoso)

1. El estudiante presiona "Unirse" en el feed.
2. El sistema verifica que el parche exista, esté abierto y tenga cupo.
3. Verifica que el usuario no sea ya miembro.
4. Se crea la membresía y se registra la interacción `JOIN`.
5. Se retorna `200 OK`.

**Request:**
```
POST /api/v1/feed/770e8400-e29b-41d4-a716-446655440000/join?userId=550e8400-...
```

---

###  Tipos de errores manejados

<div align="center">

|  **Código HTTP** | **Escenario**            |  **Mensaje de Error** |
|:------------------:|:-------------------------|:------------------------|
| ![404](https://img.shields.io/badge/404-Not_Found-orange?style=flat) | Parche no encontrado     | `"PATCH_NOT_FOUND"` |
| ![409](https://img.shields.io/badge/409-Conflict-orange?style=flat) | El usuario ya es miembro | `"BUSINESS_RULE_VIOLATION"` |
| ![422](https://img.shields.io/badge/422-Unprocessable-orange?style=flat) | Parche lleno o cerrado   | `"BUSINESS_RULE_VIOLATION"` |
| ![401](https://img.shields.io/badge/401-Unauthorized-red?style=flat) | JWT inválido o ausente   | `"JWT inválido o ausente"` |

</div>

---

## 7.  Diagramas

### Diagrama de Contexto

<div align="center">
<img src="docs/DiagramaContexto.png" alt="Diagrama de Contexto" width="600"/>
</div>

---

###  Diagrama de Clases del Dominio

<div align="center">
<img src="docs/Diagrama_Clases.jpg" alt="Diagrama de Clases" width="600"/>
</div>

**Resumen del diseño de dominio:**

- **`Patch`** — entidad central con `title`, `category`, `campusZone`, `startTime`, `capacity`, `currentCount`, `status`, `isPublic` y lógica de negocio (`isFull()`, `isOpen()`).
- **`PatchMembership`** — vincula un usuario con un parche con `MembershipStatus` (ACTIVE, CANCELLED).
- **`FeedInteraction`** — registra acciones del usuario (VIEW, JOIN, SKIP) para alimentar el motor de scoring.
- **`UserInterest`** — almacena las categorías de interés del usuario para personalizar el feed.
- **`ScoredPatch`** — value object que combina un `Patch` con su `affinityScore` y `reason` para las recomendaciones.

---

###  Diagrama de Entidad-Relación

<div align="center">
<img src="docs/Diagrama_Entidad.jpg" alt="Diagrama Entidad-Relación" width="600"/>
</div>

####  Tabla: `patches`

<div align="center">

|  Campo          |  Tipo        |  Descripción |  Restricciones               |
|:------------------|:---------------|:---|:-----------------------------|
| **id**            | `UUID`         | Identificador único del parche | PK, `GenerationType.UUID`    |
| **title**         | `VARCHAR(80)`  | Título del parche | NOT NULL                     |
| **description**   | `TEXT`         | Descripción detallada | Opcional                     |
| **category**      | `VARCHAR(20)`  | STUDY, SPORTS, CULTURE, GAMING, FOOD, OTHER | NOT NULL                     |
| **location**      | `VARCHAR(250)` | Descripción textual del lugar | Opcional                     |
| **campus_zone**   | `VARCHAR(20)`  | BIBLIOTECA, CAFETERIA, CANCHA, SALON, PARQUEADERO, EXTERNO | NOT NULL                     |
| **start_time**    | `TIMESTAMP`    | Fecha y hora de inicio | NOT NULL                     |
| **end_time**      | `TIMESTAMP`    | Fecha y hora de fin | Opcional                     |
| **capacity**      | `INTEGER`      | Cupo máximo de participantes | NOT NULL                     |
| **current_count** | `INTEGER`      | Participantes actuales | NOT NULL, DEFAULT 0          |
| **status**        | `VARCHAR(20)`  | OPEN, CLOSED, CANCELLED | NOT NULL, DEFAULT 'OPEN'     |
| **creator_id**    | `UUID`         | ID del creador del parche | NOT NULL                     |
| **is_public**     | `BOOLEAN`      | Visibilidad del parche | NOT NULL, DEFAULT true       |
| **created_at**    | `TIMESTAMP`    | Fecha de creación | NOT NULL, auto `@PrePersist` |

</div>

####  Tabla: `patch_memberships`

<div align="center">

| Campo       |  Tipo       |  Descripción |  Restricciones   |
|:--------------|:--------------|:---|:-----------------|
| **id**        | `UUID`        | Identificador único de la membresía | PK               |
| **patch_id**  | `UUID`        | Parche al que pertenece | FK → patches(id) |
| **user_id**   | `UUID`        | ID del usuario miembro | NOT NULL         |
| **status**    | `VARCHAR(20)` | ACTIVE, CANCELLED | NOT NULL         |
| **joined_at** | `TIMESTAMP`   | Fecha de ingreso | NOT NULL         |

</div>

####  Tabla: `feed_interactions`

<div align="center">

|  Campo             |  Tipo       |  Descripción | ️ Restricciones |
|:---------------------|:--------------|:---|:----------------|
| **id**               | `UUID`        | Identificador único de la interacción | PK              |
| **user_id**          | `UUID`        | ID del usuario | NOT NULL        |
| **patch_id**         | `UUID`        | ID del parche | NOT NULL        |
| **interaction_type** | `VARCHAR(10)` | VIEW, JOIN, SKIP | NOT NULL        |
| **created_at**       | `TIMESTAMP`   | Fecha de la interacción | NOT NULL        |

</div>

####  Tabla: `user_interests`

<div align="center">

|  Campo     |  Tipo       |  Descripción         |  Restricciones |
|:-------------|:--------------|:---------------------|:---------------|
| **id**       | `UUID`        | Identificador único  | PK             |
| **user_id**  | `UUID`        | ID del usuario       | NOT NULL       |
| **category** | `VARCHAR(20)` | Categoría de interés | NOT NULL       |

</div>

---

###  Diagrama de Despliegue

<div align="center">
<img src="docs/Diagrama_Despliegue.jpg" alt="Diagrama de Despliegue" width="600"/>
</div>

---

## 8.  Manejo de Errores

El microservicio implementa un `GlobalExceptionHandler` con `@RestControllerAdvice` que centraliza todas las excepciones y retorna siempre el mismo formato JSON estandarizado:

```json
{
  "error": "TIPO_ERROR",
  "message": "descripción legible del problema",
  "status": "4xx"
}
```

###  Excepciones de dominio manejadas

<div align="center">

|  **Excepción**                    |  **HTTP** |  **Error Code** |  **Escenario** |
|:----------------------------------|:----------:|:-----------------|:----------------|
| `MethodArgumentNotValidException` | 400 | `VALIDATION_ERROR` | Validación de campos fallida (ej. `q` con menos de 2 caracteres, `action` nula) |
| `PatchNotFoundException`          | 404 | `PATCH_NOT_FOUND` | El parche solicitado no existe en la base de datos |
| `BusinessRuleException`           | 422 | `BUSINESS_RULE_VIOLATION` | Regla de negocio violada: parche lleno, cerrado, o usuario ya miembro |
| `AlreadyMemberException`          | 422 | `BUSINESS_RULE_VIOLATION` | El usuario ya es miembro del parche |
| `PatchFullException`              | 422 | `BUSINESS_RULE_VIOLATION` | El parche ha alcanzado su cupo máximo |
| `ServiceUnavailableException`     | 503 | `SERVICE_UNAVAILABLE` | Servicio externo o dependencia no disponible |
| `Exception`                       | 500 | `INTERNAL_ERROR` | Error inesperado del servidor |

</div>

---

## 9.  Evidencia de las Pruebas y Cómo Ejecutarlas

###  Tipos de pruebas implementadas

<div align="center">

|  **Tipo de Prueba** |  **Descripción** |  **Herramientas**                                                                                                                                             |
|:---------------------|:-------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Pruebas Unitarias** | Validan `FeedService`, `RecommendationService` y `SearchPatchesUseCaseImpl` con mocks de puertos | ![JUnit](https://img.shields.io/badge/JUnit_5-25A162?style=flat&logo=junit5&logoColor=white) ![Mockito](https://img.shields.io/badge/Mockito-C5D928?style=flat) |
| **Cobertura de Código** | JaCoCo genera reporte HTML con métricas de cobertura por clase y método | ![JaCoCo](https://img.shields.io/badge/JaCoCo-D1322B?style=flat)                                                                                                |

</div>

###  Cómo ejecutar las pruebas

```bash
# Pruebas unitarias
./mvnw test

# Todas las pruebas + reporte JaCoCo
./mvnw verify

# Reporte de cobertura JaCoCo
./mvnw clean test jacoco:report
# Reporte en: target/site/jacoco/index.html

# Prueba específica
./mvnw test -Dtest=FeedServiceTest
./mvnw test -Dtest=RecommendationServiceTest
./mvnw test -Dtest=SearchPatchesUseCaseTest
```

### Clases de prueba implementadas

```
src/test/java/edu/eci/patricia/
├── application/service/
│   ├── FeedServiceTest.java          → Pruebas del feed con scoring y paginación
│   └── RecommendationServiceTest.java → Pruebas del motor de recomendaciones
└── application/usecase/
    └── SearchPatchesUseCaseTest.java  → Pruebas de búsqueda con filtros dinámicos
```

###  Criterios de aceptación de pruebas

-  Todas las pruebas en estado PASSED
- Cero errores de compilación
-  Casos felices y de error implementados por caso de uso
-  Ports mockeados correctamente sin acceso a infraestructura real

---

## 10.  Scaffolding del microservicio

```
mewtwocode-feed-search-service/
│
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/edu/eci/patricia/
│   │   │   │
│   │   │   ├── 📁 application/                              # 🔵 CAPA DE APLICACIÓN
│   │   │   │   ├── 📁 dto/
│   │   │   │   │   ├── 📁 request/
│   │   │   │   │   │   ├── InteractRequest.java            
│   │   │   │   │   │   └── SearchRequest.java               
│   │   │   │   │   └── 📁 response/
│   │   │   │   │       ├── PatchSummaryResponse.java        
│   │   │   │   │       ├── PatchRecommendationResponse.java 
│   │   │   │   │       └── SearchResponse.java              
│   │   │   │   ├── 📁 mapper/
│   │   │   │   │   └── PatchDomainMapper.java
│   │   │   │   └── 📁 service/
│   │   │   │       ├── FeedService.java                     
│   │   │   │       ├── JoinPatchService.java                
│   │   │   │       ├── RecommendationService.java           
│   │   │   │       └── RegisterInteractionService.java      
│   │   │   │   └── 📁 usecase/
│   │   │   │       └── SearchPatchesUseCaseImpl.java        
│   │   │   │
│   │   │   ├── 📁 domain/                                   # 🟢 CAPA DE DOMINIO
│   │   │   │   ├── 📁 exceptions/
│   │   │   │   │   ├── AlreadyMemberException.java
│   │   │   │   │   ├── BusinessRuleException.java
│   │   │   │   │   ├── PatchFullException.java
│   │   │   │   │   ├── PatchNotFoundException.java
│   │   │   │   │   └── ServiceUnavailableException.java
│   │   │   │   ├── 📁 model/
│   │   │   │   │   ├── Patch.java                           
│   │   │   │   │   ├── PatchMembership.java
│   │   │   │   │   ├── FeedInteraction.java
│   │   │   │   │   ├── UserInterest.java
│   │   │   │   │   └── 📁 enums/
│   │   │   │   │       ├── CampusZone.java                  
│   │   │   │   │       ├── InteractionType.java             
│   │   │   │   │       ├── MembershipStatus.java
│   │   │   │   │       ├── PatchCategory.java             
│   │   │   │   │       └── PatchStatus.java
│   │   │   │   ├── 📁 ports/
│   │   │   │   │   ├── 📁 in/
│   │   │   │   │   │   ├── FeedUseCase.java
│   │   │   │   │   │   ├── GetRecommendationsPort.java
│   │   │   │   │   │   ├── JoinPatchUseCase.java
│   │   │   │   │   │   ├── RegisterInteractionPort.java
│   │   │   │   │   │   └── SearchPatchesUseCase.java
│   │   │   │   │   └── 📁 out/
│   │   │   │   │       ├── FeedInteractionRepositoryPort.java
│   │   │   │   │       ├── MembershipRepositoryPort.java
│   │   │   │   │       ├── PatchRepositoryPort.java
│   │   │   │   │       ├── PatchWriteRepositoryPort.java
│   │   │   │   │       └── UserInterestRepositoryPort.java
│   │   │   │   └── 📁 valueobjects/
│   │   │   │       └── ScoredPatch.java                     
│   │   │   │
│   │   │   ├── 📁 entrypoints/                              # 🟠 ENTRADA (DRIVING ADAPTERS)
│   │   │   │   ├── 📁 advice/
│   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   └── 📁 rest/controller/
│   │   │   │       ├── FeedController.java                  (GET /api/v1/feed/parches, POST /api/v1/feed/parches/{patchId}/interact)
│   │   │   │       ├── SearchController.java                (GET /api/v1/parches/search)
│   │   │   │       ├── JoinPatchController.java             (POST /api/v1/feed/{patchId}/join)
│   │   │   │       └── RecommendationController.java        (GET /api/v1/feed/recommended)
│   │   │   │
│   │   │   └── 📁 infrastructure/                           # 🟠 INFRAESTRUCTURA (DRIVEN ADAPTERS)
│   │   │       ├── 📁 adapters/
│   │   │       │   ├── 📁 adapter/
│   │   │       │   │   ├── FeedInteractionRepositoryAdapter.java
│   │   │       │   │   ├── MembershipRepositoryAdapter.java
│   │   │       │   │   ├── PatchRepositoryAdapter.java
│   │   │       │   │   ├── PatchWriteRepositoryAdapter.java
│   │   │       │   │   └── UserInterestRepositoryAdapter.java
│   │   │       │   └── 📁 persistence/
│   │   │       │       ├── PatchSpecification.java           
│   │   │       │       ├── 📁 entity/
│   │   │       │       │   ├── PatchEntity.java
│   │   │       │       │   ├── PatchMembershipEntity.java
│   │   │       │       │   ├── FeedInteractionEntity.java
│   │   │       │       │   ├── UserInterestEntity.java
│   │   │       │       │   ├── RecommendationCacheEntity.java
│   │   │       │       │   └── SearchIndexEntity.java
│   │   │       │       ├── 📁 mapper/
│   │   │       │       │   └── PatchEntityMapper.java
│   │   │       │       └── 📁 repository/
│   │   │       │           ├── PatchJpaRepository.java
│   │   │       │           ├── PatchMembershipJpaRepository.java
│   │   │       │           ├── FeedInteractionJpaRepository.java
│   │   │       │           └── UserInterestJpaRepository.java
│   │   │       └── 📁 config/
│   │   │           ├── RedisConfig.java
│   │   │           ├── SecurityConfig.java                  (JWT OAuth2 Resource Server)
│   │   │           ├── SwaggerConfig.java
│   │   │           └── WebConfig.java
│   │   │
│   │   └── 📁 resources/
│   │       ├── application.properties                       (puerto 8081, PostgreSQL, Redis)
│   │       └── application-local.properties
│   │
│   └── 📁 test/
│       └── 📁 java/edu/eci/patricia/
│           └── 📁 application/
│               ├── 📁 service/
│               │   ├── FeedServiceTest.java
│               │   └── RecommendationServiceTest.java
│               └── 📁 usecase/
│                   └── SearchPatchesUseCaseTest.java
│
├── 📁 docs/
│   ├── DiagramaContexto.png
│   ├── Diagrama_Clases.jpg
│   ├── Diagrama_Despliegue.jpg
│   └── Diagrama_Entidad.jpg
│
├── 📁 .github/workflows/
│   └── ci.yml
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── seed.sql
└── README.md
```

###  Arquitectura Hexagonal Implementada

<div align="center">

| **Capa**             | **Responsabilidad**                                                                                                                            |  **Dependencias**       |
|:---------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------|:--------------------------|
| **Domain**           | Modelos (`Patch`, `PatchMembership`, `FeedInteraction`, `UserInterest`, `ScoredPatch`), enums, excepciones y puertos                             | Ninguna (independiente)   |
| **Application**      | Servicios (`FeedService`, `RecommendationService`, `RegisterInteractionService`, `JoinPatchService`) y casos de uso (`SearchPatchesUseCaseImpl`) | Solo `Domain`             |
| **Entrypoints**      | 4 controladores REST + `GlobalExceptionHandler`                                                                                                  | `Domain` + `Application`  |
| **Infrastructure** | 5 adaptadores JPA, `PatchSpecification` para filtros dinámicos, `RedisConfig`, `SecurityConfig`                                                  |  `Domain` + `Application` |

</div>

**Flujo de dependencias:** `Entrypoints / Infrastructure → Application → Domain`

---

## 11. Ejecución del Proyecto

### Prerrequisitos
- **Java 21**
- **Maven 3.9+**
- **Docker & Docker Compose**
- **PostgreSQL** (si ejecutas localmente sin Docker)
- **Redis** (si ejecutas localmente sin Docker)

###  Opción 1: Ejecución Local (Maven)

```bash
# 1. Clonar repositorio
git clone https://github.com/<org>/mewtwocode-feed-search-service.git

# 2. Levantar base de datos y Redis
docker compose up -d postgres redis

# 3. Ejecutar con perfil local
./mvnw spring-boot:run -Dspring.profiles.active=local
```

**URL Local:** `http://localhost:8081`
**Swagger UI:** `http://localhost:8081/swagger-ui.html`
**OpenAPI Docs:** `http://localhost:8081/v3/api-docs`

###  Opción 2: Ejecución con Docker Compose

```bash
docker compose up --build
```

###  Variables de Entorno

| Variable | Valor por defecto | Descripción |
|:---------|:-----------------|:------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/m06_feed` | URL de PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `patricia` | Usuario de PostgreSQL |
| `SPRING_DATASOURCE_PASSWORD` | `patricia` | Contraseña de PostgreSQL |
| `SPRING_DATA_REDIS_HOST` | `localhost` | Host de Redis |
| `SPRING_DATA_REDIS_PORT` | `6379` | Puerto de Redis |
| `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_SECRET` | `patriciasecret` | Secreto JWT |
| `PORT` | `8081` | Puerto del servidor |

---

## 12.  CI/CD y Despliegue

###  Pipeline de Automatización (GitHub Actions)

El flujo en `.github/workflows/ci.yml` se ejecuta en cada push a `main`, `develop` o `feature/**` y en cada PR a `main` o `develop`:

1. **Checkout** — Clona el repositorio con `actions/checkout@v4`.
2. **Java 21** — Configura el JDK con `actions/setup-java@v4` (distribución Temurin).
3. **Cache Maven** — Restaura dependencias cacheadas para acelerar el build.
4. **Compilar** — `./mvnw compile` verifica que el código sea válido.
5. **Tests** — `./mvnw test` ejecuta todas las pruebas unitarias.
6. **Docker Build** — Construye la imagen `m06-feed-busqueda:{sha}` para validar el Dockerfile.

### Infraestructura

<div align="center">

|  **Componente** |  **Descripción** |
|------------------|-------------------|
| Vercel | Hosting del microservicio y demo funcional |
| PostgreSQL | Base de datos relacional para parches, membresías e interacciones |
| Redis | Caché distribuida para feed y recomendaciones |
| GitHub Actions | Pipeline CI de compilación, pruebas y Docker build |
| Swagger UI | Documentación interactiva en `/swagger-ui.html` |

</div>

---

## 13. Contribuciones y Metodología

El equipo **Mewtwo-Code** aplicó la metodología **Scrum** con sprints semanales para garantizar una entrega incremental y mejora continua.

### Equipo Scrum

| Rol | Responsabilidad |
|:---|:---|
| **Product Owner** | Priorización del Backlog y maximización de valor. |
| **Scrum Master** | Facilitador del proceso y eliminación de impedimentos. |
| **Developers** | Diseño, implementación y pruebas de funcionalidades. |

###  Eventos y Artefactos

- **Sprints Semanales**: Ciclos cortos de desarrollo.
- **Daily Scrum**: Sincronización diaria (15 min).
- **Sprint Review & Retrospective**: Demostración de incrementos y mejora de procesos.

---

<div align="center">

###  Equipo **Mewtwo-Code**

![Team](https://img.shields.io/badge/Team-Mewtwo--Code-blueviolet?style=for-the-badge&logo=github&logoColor=white)
![Module](https://img.shields.io/badge/Module-M06_Feed_%26_B%C3%BAsqueda-orange?style=for-the-badge)
![Course](https://img.shields.io/badge/Course-DOSW-orange?style=for-the-badge)
![Year](https://img.shields.io/badge/Year-2026--1-blue?style=for-the-badge)

>  **PATRIC.IA Feed & Search Service** es el punto central de descubrimiento de parches en el campus, diseñado para responder en menos de 1 segundo con recomendaciones personalizadas y búsqueda en tiempo real.

** Escuela Colombiana de Ingeniería Julio Garavito**

</div>