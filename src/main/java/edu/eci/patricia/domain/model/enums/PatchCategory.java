package edu.eci.patricia.domain.model.enums;

/**
 * Classifies a patch into a broad thematic category. Used for feed filtering,
 * search facets, and computing per-category affinity scores for recommendations.
 *
 * <ul>
 *   <li>{@link #STUDY}   - Academic or study-related activities.</li>
 *   <li>{@link #SPORTS}  - Physical or competitive sports activities.</li>
 *   <li>{@link #CULTURE} - Cultural, artistic, or social events.</li>
 *   <li>{@link #GAMING}  - Video game or board game sessions.</li>
 *   <li>{@link #FOOD}    - Food-related meetups or dining events.</li>
 *   <li>{@link #OTHER}   - Activities that do not fit the above categories.</li>
 * </ul>
 */
public enum PatchCategory {
    STUDY, SPORTS, CULTURE, GAMING, FOOD, OTHER
}
