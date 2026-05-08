# M06 — Feed & Search · API Contract

> **Base URL (local):** `http://localhost:8081/api/v1`  
> **Base URL (Docker):** `http://localhost:8080/api/v1`  
> **Date format:** `YYYY-MM-DDTHH:mm:ss` (LocalDateTime) · `YYYY-MM-DD` (LocalDate)  
> **All enums in UPPERCASE** — lowercase or accented characters return 400.

---

## Endpoints

### 1. Personalized patch feed

```
GET /feed/parches
```

**Query params:**

| Field        | Type   | Required | Default | Notes                                          |
|--------------|--------|:--------:|---------|------------------------------------------------|
| `userId`     | UUID   | ✅       | —       | Authenticated user ID                          |
| `category`   | enum   | ❌       | —       | Filter by patch category — see enum table      |
| `campusZone` | enum   | ❌       | —       | Filter by campus zone — see enum table         |
| `dateFrom`   | Date   | ❌       | —       | `YYYY-MM-DD`. Only patches starting on or after|
| `page`       | int    | ❌       | `0`     | Zero-based                                     |
| `size`       | int    | ❌       | `20`    | Results per page                               |

**Scoring formula:**

Patches are ranked by a composite score before pagination:

| Component           | Weight | Source                                      |
|---------------------|--------|---------------------------------------------|
| Category affinity   | 50%    | `user_category_scores` — continuous 0–100   |
| Temporal proximity  | 20%    | Hours until patch start time                |
| Geographic proximity| 30%    | **Pending** — Feign to M04 (Geolocation)    |

**Response `200`** — array of `PatchSummaryResponse`:

| Field          | Type    | Description                                      |
|----------------|---------|--------------------------------------------------|
| `id`           | UUID    | Patch ID                                         |
| `title`        | String  | Patch name                                       |
| `description`  | String  | Description                                      |
| `category`     | enum    | Patch category — see enum table                  |
| `campusZone`   | enum    | Campus zone — see enum table                     |
| `startTime`    | DateTime| Start date and time                              |
| `capacity`     | int     | Maximum group size                               |
| `currentCount` | int     | Current number of members                        |
| `status`       | enum    | Patch status — see enum table                    |
| `isPublic`     | boolean | Whether the patch is public                      |
| `creatorName`  | String  | Creator display name                             |
| `affinityScore`| float   | Relevance for this user (`0.0` – `1.0`)          |
| `userIsMember` | boolean | Whether the user has already joined              |

---

### 2. Register interaction with a patch

```
POST /feed/parches/{patchId}/interact
```

**Path param:**

| Field     | Type | Required |
|-----------|------|:--------:|
| `patchId` | UUID | ✅       |

**Query param:**

| Field    | Type | Required |
|----------|------|:--------:|
| `userId` | UUID | ✅       |

**Body JSON:**

```json
{
  "action": "JOIN"
}
```

| Field    | Type | Valid values          | Required |
|----------|------|-----------------------|:--------:|
| `action` | enum | `VIEW` · `JOIN` · `SKIP` | ✅    |

**Effect of each action on the category score:**

| Action | Score delta | Additional effect                                |
|--------|-------------|--------------------------------------------------|
| `VIEW` | `+0.1`      | —                                                |
| `JOIN` | `+1.0`      | Excludes patch from future recommendations       |
| `SKIP` | `−10.0`     | —                                                |

**Time decay formula applied before adding event weight:**

```
S_new = S_current × e^(−0.01 × days_since_last_update) + event_weight
S_new = clamp(S_new, 0, 100)
```

> Category score is maintained between `0` and `100` and decays toward zero if there is no activity.

**Response `204`** — no body.

---

### 3. Personalized recommendations

```
GET /feed/recommended
```

**Query param:**

| Field    | Type | Required |
|----------|------|:--------:|
| `userId` | UUID | ✅       |

**Response `200`** — array of up to 10 items:

| Field          | Type   | Description                                                          |
|----------------|--------|----------------------------------------------------------------------|
| `patchId`      | UUID   | Patch ID                                                             |
| `patch`        | object | Same `PatchSummaryResponse` as Endpoint 1                            |
| `affinityScore`| float  | Affinity score (`0.0` – `1.0`)                                       |
| `reason`       | String | Human-readable label, e.g. `"matches your profile"` / `"Popular on campus"` |

> If the user has no interaction history, returns the most popular patches on campus.

---

### 4. Search with filters

```
GET /parches/search
```

**Query params:**

| Field               | Type    | Required | Default | Notes                                              |
|---------------------|---------|:--------:|---------|-----------------------------------------------------|
| `userId`            | UUID    | ❌       | —       | If omitted, `userIsMember` is always `false`        |
| `q`                 | String  | ❌       | —       | Min 2 characters. Searches title and description   |
| `category`          | enum    | ❌       | —       | See enum table                                     |
| `campusZone`        | enum    | ❌       | —       | See enum table                                     |
| `status`            | enum    | ❌       | —       | See enum table                                     |
| `dateFrom`          | Date    | ❌       | —       | `YYYY-MM-DD`. Start of range (inclusive)           |
| `dateTo`            | Date    | ❌       | —       | `YYYY-MM-DD`. End of range (inclusive)             |
| `maxGroupSize`      | int     | ❌       | —       | Only patches with capacity ≤ this value            |
| `hasAvailableSpots` | boolean | ❌       | —       | `true` = only patches with open spots              |
| `page`              | int     | ❌       | `0`     | Zero-based                                         |
| `size`              | int     | ❌       | `20`    | Results per page                                   |

Results are sorted by `startTime` ascending.

**Response `200`:**

| Field        | Type  | Description                   |
|--------------|-------|-------------------------------|
| `results`    | array | Array of `PatchSummaryResponse`|
| `total`      | long  | Total matching results        |
| `page`       | int   | Current page                  |
| `size`       | int   | Page size                     |
| `totalPages` | int   | Total pages                   |

---

## Enum table

| Enum         | Valid values                                                    |
|--------------|-----------------------------------------------------------------|
| `category`   | `STUDY` · `SPORTS` · `CULTURE` · `GAMING` · `FOOD` · `OTHER`   |
| `campusZone` | `BIBLIOTECA` · `CAFETERIA` · `CANCHA` · `SALON` · `PARQUEADERO` · `EXTERNO` |
| `status`     | `OPEN` · `FULL` · `CLOSED` · `CANCELLED`                       |
| `action`     | `VIEW` · `JOIN` · `SKIP`                                        |

---

## Common error codes

| Code  | Cause                                                          |
|-------|----------------------------------------------------------------|
| `400` | Invalid parameter (e.g. `q` shorter than 2 chars, bad enum)   |
| `401` | Invalid or missing JWT                                         |
| `204` | Success with no body (POST interact only)                      |
| `404` | Patch not found                                               |
| `409` | User is already a member of the patch                          |
| `503` | Dependent service unavailable                                  |

---

## Database tables (M06)

| Table                  | Purpose                                              |
|------------------------|------------------------------------------------------|
| `patches`              | Read replica of patch data (synced from M02)         |
| `patch_memberships`    | Tracks which users joined which patches              |
| `feed_interactions`    | Raw interaction log (VIEW/JOIN/SKIP per user+patch)  |
| `user_category_scores` | Continuous affinity score per user per category      |

### `user_category_scores` schema

| Column         | Type      | Notes                                   |
|----------------|-----------|-----------------------------------------|
| `id`           | UUID (PK) |                                         |
| `user_id`      | UUID      | Unique per (user_id, category)          |
| `category`     | VARCHAR   | Matches `PatchCategory` enum            |
| `score_total`  | FLOAT     | Clamped 0–100. Decays over time.        |
| `last_updated` | TIMESTAMP | Used to compute decay since last event  |
