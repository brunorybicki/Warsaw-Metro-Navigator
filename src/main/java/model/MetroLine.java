package model;

import java.util.Objects;

/**
 * A metro line, identified by its name.
 *
 * @param lineName the line name shown to the user, for example, "M1".
 */
public record MetroLine(String lineName) {
    /**
     * Constructor for MetroLine.
     *
     * @throws NullPointerException if the line name is null.
     */
    public MetroLine {
        Objects.requireNonNull(lineName,"line name must not be null");
    }
}
