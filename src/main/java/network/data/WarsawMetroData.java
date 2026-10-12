package network.data;

import model.MetroLine;
import model.Station;
import model.StationNode;
import network.MetroNetwork;

import java.util.ArrayList;
import java.util.List;

/**
 * The Warsaw Metro: lines M1 and M2 with their stations and the transfer between them.
 * This is the only place that knows the Warsaw stations, so the rest of the program stays independent of the city.
 */
public final class WarsawMetroData {
    private static final String TRANSFER_STATION_NAME = "Świętokrzyska";

    private static final MetroLine M1 = new MetroLine("M1");
    private static final MetroLine M2 = new MetroLine("M2");

    // From north to south, Młociny to Kabaty.
    private static final List<Station> M1_STATIONS = stations(
        "Młociny", "Wawrzyszew", "Stare Bielany", "Słodowiec", "Marymont", "Plac Wilsona", "Dworzec Gdański",
        "Ratusz Arsenał", TRANSFER_STATION_NAME, "Centrum", "Politechnika", "Pole Mokotowskie", "Racławicka",
        "Wierzbno", "Wilanowska", "Służew", "Ursynów", "Stokłosy", "Imielin", "Natolin", "Kabaty");

    // From west to east, Bemowo to Bródno.
    private static final List<Station> M2_STATIONS = stations(
        "Bemowo", "Ulrychów", "Księcia Janusza", "Młynów", "Płocka", "Rondo Daszyńskiego", "Rondo ONZ",
        TRANSFER_STATION_NAME, "Nowy Świat-Uniwersytet", "Centrum Nauki Kopernik", "Stadion Narodowy",
        "Dworzec Wileński", "Szwedzka", "Targówek Mieszkaniowy", "Trocka", "Zacisze", "Kondratowicza", "Bródno");

    /**
     * Prevents creating instances: the class only offers static methods.
     */
    private WarsawMetroData() {
    }

    /**
     * Creates the Warsaw Metro network with lines M1 and M2, connected by a transfer at Świętokrzyska.
     */
    public static MetroNetwork createNetwork() {
        var network = new MetroNetwork();
        network.addLine(M1, M1_STATIONS);
        network.addLine(M2, M2_STATIONS);

        var transferStation = new Station(TRANSFER_STATION_NAME);
        network.addTransfer(new StationNode(transferStation, M1), new StationNode(transferStation, M2));

        return network;
    }

    /**
     * Turns station names into stations, keeping their order.
     */
    private static List<Station> stations(String... names) {
        var stations = new ArrayList<Station>();

        for (var name : names) {
            stations.add(new Station(name));
        }

        return stations;
    }
}
