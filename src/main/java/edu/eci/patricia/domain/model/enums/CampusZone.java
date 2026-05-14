package edu.eci.patricia.domain.model.enums;

/**
 * Represents the physical zones within or around the campus where a patch
 * activity can take place. Used to categorize and filter patches by location.
 *
 * <ul>
 *   <li>{@link #BIBLIOTECA} - The campus library area.</li>
 *   <li>{@link #CAFETERIA} - The cafeteria or food court area.</li>
 *   <li>{@link #CANCHA} - Sports courts or athletic fields.</li>
 *   <li>{@link #SALON} - A classroom or indoor meeting room.</li>
 *   <li>{@link #PARQUEADERO} - The campus parking lot.</li>
 *   <li>{@link #EXTERNO} - A location outside the campus grounds.</li>
 * </ul>
 */
public enum CampusZone {
    BIBLIOTECA, CAFETERIA, CANCHA, SALON, PARQUEADERO, EXTERNO
}
