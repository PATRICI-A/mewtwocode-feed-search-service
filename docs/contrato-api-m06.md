# M06 — Feed & Búsqueda · Contrato de API

> **Base URL:** `http://localhost:8081/api/v1`  
> **Formato de fechas:** `YYYY-MM-DDTHH:mm:ss` (LocalDateTime) · `YYYY-MM-DD` (LocalDate)  
> **Todos los enums van en MAYÚSCULAS** — minúsculas o tildes causan error 400.

---

## Endpoints

### 1. Feed personalizado

```
GET /feed/parches
```

**Query params:**

| Campo    | Tipo | Obligatorio | Default | Notas                   |
|----------|------|:-----------:|---------|-------------------------|
| `userId` | UUID | ✅          | —       | ID del usuario          |
| `page`   | int  | ❌          | `0`     | Base 0                  |
| `size`   | int  | ❌          | `20`    | Resultados por página   |

**Respuesta `200`** — array de `PatchSummaryResponse`:

| Campo          | Tipo    | Descripción                                 |
|----------------|---------|---------------------------------------------|
| `id`           | UUID    | ID del parche                               |
| `title`        | String  | Nombre del parche                           |
| `description`  | String  | Descripción                                 |
| `category`     | enum    | Categoría — ver tabla de enums              |
| `campusZone`   | enum    | Zona del campus — ver tabla de enums        |
| `startTime`    | DateTime| Fecha y hora de inicio                      |
| `capacity`     | int     | Cupo máximo                                 |
| `currentCount` | int     | Personas inscritas actualmente              |
| `status`       | enum    | Estado del parche — ver tabla de enums      |
| `isPublic`     | boolean | Si el parche es público                     |
| `creatorName`  | String  | Nombre del creador                          |
| `affinityScore`| float   | Relevancia para el usuario (`0.0` – `1.0`)  |
| `userIsMember` | boolean | Si el usuario ya está unido al parche       |

---

### 2. Registrar interacción con un parche

```
POST /feed/parches/{patchId}/interact
```

**Path param:**

| Campo     | Tipo | Obligatorio |
|-----------|------|:-----------:|
| `patchId` | UUID | ✅          |

**Query param:**

| Campo    | Tipo | Obligatorio |
|----------|------|:-----------:|
| `userId` | UUID | ✅          |

**Body JSON:**

```json
{
  "action": "JOIN"
}
```

| Campo    | Tipo | Valores válidos           | Obligatorio |
|----------|------|---------------------------|:-----------:|
| `action` | enum | `VIEW` · `JOIN` · `SKIP`  | ✅          |

**Efecto de cada acción en el score de categoría:**

| Acción | Efecto en score | Efecto adicional                                     |
|--------|-----------------|------------------------------------------------------|
| `VIEW` | `+0.1`          | —                                                    |
| `JOIN` | `+1.0`          | Excluye el parche de futuras recomendaciones         |
| `SKIP` | `−10.0`         | —                                                    |

> El score por categoría se mantiene entre `0` y `100` y decae con el tiempo si no hay actividad.

**Respuesta `204`** — sin body.

---

### 3. Recomendaciones personalizadas

```
GET /feed/recommended
```

**Query param:**

| Campo    | Tipo | Obligatorio |
|----------|------|:-----------:|
| `userId` | UUID | ✅          |

**Respuesta `200`** — array de máximo 10 elementos:

| Campo          | Tipo    | Descripción                                                         |
|----------------|---------|---------------------------------------------------------------------|
| `patchId`      | UUID    | ID del parche                                                       |
| `patch`        | objeto  | Mismo objeto `PatchSummaryResponse` del Endpoint 1                  |
| `affinityScore`| float   | Score de afinidad (`0.0` – `1.0`)                                   |
| `reason`       | String  | Texto legible. Ej: `"compatible con tu perfil"` / `"Popular en tu campus"` |

> Si el usuario no tiene historial de interacciones, retorna los parches más populares del campus.

---

### 4. Búsqueda con filtros

```
GET /parches/search
```

**Query params:**

| Campo              | Tipo    | Obligatorio | Default | Notas                                            |
|--------------------|---------|:-----------:|---------|--------------------------------------------------|
| `userId`           | UUID    | ❌          | —       | Si se omite, `userIsMember` siempre `false`      |
| `q`                | String  | ❌          | —       | Mínimo 2 caracteres. Busca en título y descripción |
| `category`         | enum    | ❌          | —       | Ver tabla de enums                               |
| `campusZone`       | enum    | ❌          | —       | Ver tabla de enums                               |
| `status`           | enum    | ❌          | —       | Ver tabla de enums                               |
| `dateFrom`         | Date    | ❌          | —       | Formato `YYYY-MM-DD`. Inicio del rango (inclusive)|
| `dateTo`           | Date    | ❌          | —       | Formato `YYYY-MM-DD`. Fin del rango (inclusive)  |
| `maxGroupSize`     | int     | ❌          | —       | Solo parches con capacidad ≤ este valor          |
| `hasAvailableSpots`| boolean | ❌          | —       | `true` = solo parches con cupo disponible        |
| `page`             | int     | ❌          | `0`     | Base 0                                           |
| `size`             | int     | ❌          | `20`    | Resultados por página                            |

**Respuesta `200`:**

| Campo        | Tipo  | Descripción                            |
|--------------|-------|----------------------------------------|
| `results`    | array | Array de `PatchSummaryResponse`        |
| `total`      | long  | Total de resultados encontrados        |
| `page`       | int   | Página actual                          |
| `size`       | int   | Tamaño de página                       |
| `totalPages` | int   | Total de páginas                       |

---

## Tabla de enums

| Enum         | Valores válidos                                              |
|--------------|--------------------------------------------------------------|
| `category`   | `STUDY` · `SPORTS` · `CULTURE` · `GAMING` · `FOOD` · `OTHER`|
| `campusZone` | `BIBLIOTECA` · `CAFETERIA` · `CANCHA` · `SALON` · `PARQUEADERO` · `EXTERNO` |
| `status`     | `OPEN` · `FULL` · `CLOSED` · `CANCELLED`                    |
| `action`     | `VIEW` · `JOIN` · `SKIP`                                     |

---

## Códigos de error comunes

| Código | Causa                                                         |
|--------|---------------------------------------------------------------|
| `400`  | Parámetro inválido (ej. `q` con menos de 2 caracteres, enum incorrecto) |
| `401`  | JWT inválido o ausente                                        |
| `204`  | Éxito sin body (solo en POST interact)                        |
