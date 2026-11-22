import app.MetroNavigatorApp;
import network.data.WarsawMetroData;
import routing.RouteFinder;
import ui.ConsoleUI;
import ui.InputReader;
import ui.RouteFormatter;

import java.util.Scanner;

/**
 * Starts the Warsaw Metro Navigator. It only builds the objects and hands over to the application.
 */
public class Main {
    /**
     * Builds the application for the Warsaw Metro and the terminal, and runs it.
     */
    public static void main(String[] args) {
        var network = WarsawMetroData.createNetwork();
        var inputReader = new InputReader(new Scanner(System.in), System.out);
        var consoleUI = new ConsoleUI(network, inputReader, System.out);

        new MetroNavigatorApp(consoleUI, new RouteFinder(network), new RouteFormatter(network)).run();
    }
}
