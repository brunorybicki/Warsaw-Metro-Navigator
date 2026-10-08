package model;

import java.util.Objects;

/**
 * A physical metro station, identified by its name.
 *
 * @param stationName the station name, exactly as shown to the user.
 */
public record Station(String stationName) {
    /**
     * Constructor for Station.
     *
     * @throws NullPointerException if the station name is null.
     */
    public Station {
        Objects.requireNonNull(stationName, "station name must not be null");
    }
}
