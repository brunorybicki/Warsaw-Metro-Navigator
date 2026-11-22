package ui;

import model.MetroLine;
import model.Station;
import model.StationNode;
import network.MetroNetwork;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Talks to the user in the terminal: shows the welcome, lets the user pick the start and the destination
 * from numbered menus, and prints the route. It does not calculate routes.
 */
public class ConsoleUI {
    private final MetroNetwork network;
    private final InputReader inputReader;
    private final PrintStream output;

    /**
     * Constructor for ConsoleUI.
     *
     * @param network     the network whose lines and stations are offered in the menus.
     * @param inputReader reads the numbers the user chooses.
     * @param output      where everything is printed.
     * @throws NullPointerException if any argument is null.
     */
    public ConsoleUI(MetroNetwork network, InputReader inputReader, PrintStream output) {
        this.network = Objects.requireNonNull(network, "network must not be null");
        this.inputReader = Objects.requireNonNull(inputReader, "inputReader must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * Prints the welcome message with every line and its terminals, from the first station to the last.
     */
    public void showWelcome() {
        output.println("WELCOME TO THE WARSAW METRO NAVIGATOR!");
        output.println();
        output.println("Available lines:");

        for (var line : network.lines()) {
            var stations = network.stationsOf(line);
            var first = stations.getFirst().stationName();
            var last = stations.getLast().stationName();

            output.println(line.lineName() + " - " + first + " → " + last);
        }

        output.println();
    }

    /**
     * Lets the user choose the line and the station where the journey starts.
     */
    public StationNode chooseStart() {
        return chooseStationNode("starting", "START:");
    }

    /**
     * Lets the user choose the line and the station where the journey ends.
     */
    public StationNode chooseDestination() {
        return chooseStationNode("destination", "DESTINATION:");
    }

    /**
     * Tells the user that the route is being searched for.
     */
    public void showSearching() {
        output.println("Finding the best route...");
        output.println();
    }

    /**
     * Prints a route that has already been turned into text.
     */
    public void showRoute(String routeText) {
        output.print(routeText);
    }

    /**
     * Tells the user that the program ends because there is no more input to read.
     */
    public void showInputEnded() {
        output.println("Input ended. Goodbye!");
    }

    /**
     * Asks for a line and then for a station on it, and confirms the choice under the given heading.
     *
     * @param purpose the word used in the questions, such as "starting" or "destination".
     * @param heading the heading printed above the chosen station, such as "START:".
     */
    private StationNode chooseStationNode(String purpose, String heading) {
        var line = chooseLine(purpose);
        var station = chooseStation(purpose, line);

        output.println(heading);
        output.println(line.lineName() + " " + station.stationName());
        output.println();

        return new StationNode(station, line);
    }

    /**
     * Asks for one of the lines of the network.
     */
    private MetroLine chooseLine(String purpose) {
        var lines = network.lines();
        var names = new ArrayList<String>();

        for (var line : lines) {
            names.add(line.lineName());
        }

        return lines.get(chooseIndex("Choose your " + purpose + " metro line:", names));
    }

    /**
     * Asks for one of the stations of a line.
     */
    private Station chooseStation(String purpose, MetroLine line) {
        var stations = network.stationsOf(line);
        var names = new ArrayList<String>();

        for (var station : stations) {
            names.add(station.stationName());
        }

        var question = "Choose your " + purpose + " station on the " + line.lineName() + " line:";

        return stations.get(chooseIndex(question, names));
    }

    /**
     * Prints the question and the numbered options and returns the position of the option the user picked,
     * counted from 0.
     */
    private int chooseIndex(String question, List<String> options) {
        output.println(question);

        for (int i = 0; i < options.size(); i++) {
            output.println("(" + (i + 1) + ") " + options.get(i));
        }

        return inputReader.readChoice(options.size()) - 1;
    }
}
