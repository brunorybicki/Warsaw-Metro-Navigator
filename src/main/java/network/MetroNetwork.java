package network;

import model.MetroLine;
import model.Station;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A generic metro network made of lines, each with an ordered list of stations.
 * It knows nothing about any particular city.
 */
public class MetroNetwork {
    private final Map<MetroLine, List<Station>> stationsByLine = new LinkedHashMap<>();

    /**
     * Adds a line together with its stations, in travel order from one terminal to the other.
     *
     * @param line     the line to add.
     * @param stations the stations of the line, in order.
     * @throws NullPointerException     if the line or the stations are null.
     * @throws IllegalArgumentException if the line is already in the network or has no stations.
     */
    public void addLine(MetroLine line, List<Station> stations) {
        Objects.requireNonNull(line, "line must not be null");
        Objects.requireNonNull(stations, "stations must not be null");

        if (stationsByLine.containsKey(line)) {
            throw new IllegalArgumentException("line is already in the network: " + line.lineName());
        }

        if (stations.isEmpty()) {
            throw new IllegalArgumentException("line must have at least one station: " + line.lineName());
        }

        stationsByLine.put(line, List.copyOf(stations));
    }

    /**
     * Returns the lines of the network, in the order in which they were added.
     */
    public List<MetroLine> lines() {
        return List.copyOf(stationsByLine.keySet());
    }

    /**
     * Returns the stations of a line, in travel order. The first and the last one are the terminals of the line.
     *
     * @param line the line whose stations are requested.
     * @throws IllegalArgumentException if the line is not in the network.
     */
    public List<Station> stationsOf(MetroLine line) {
        var stations = stationsByLine.get(line);

        if (stations == null) {
            throw new IllegalArgumentException("line is not in the network: " + line);
        }

        return stations;
    }
}
