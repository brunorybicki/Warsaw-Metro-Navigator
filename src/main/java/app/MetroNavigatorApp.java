package app;

import routing.RouteFinder;
import ui.ConsoleUI;
import ui.InputEndedException;
import ui.RouteFormatter;

import java.util.Objects;

/**
 * Runs one session of the navigator: welcome, start, destination, route. It only coordinates the other classes
 * and calculates or formats nothing itself.
 */
public class MetroNavigatorApp {
    private final ConsoleUI consoleUI;
    private final RouteFinder routeFinder;
    private final RouteFormatter routeFormatter;

    /**
     * Constructor for MetroNavigatorApp.
     *
     * @param consoleUI      talks to the user.
     * @param routeFinder    finds the route between the chosen stations.
     * @param routeFormatter turns the route into text.
     * @throws NullPointerException if any argument is null.
     */
    public MetroNavigatorApp(ConsoleUI consoleUI, RouteFinder routeFinder, RouteFormatter routeFormatter) {
        this.consoleUI = Objects.requireNonNull(consoleUI, "consoleUI must not be null");
        this.routeFinder = Objects.requireNonNull(routeFinder, "routeFinder must not be null");
        this.routeFormatter = Objects.requireNonNull(routeFormatter, "routeFormatter must not be null");
    }

    /**
     * Guides the user from the welcome message to the route.
     * <p>
     * If the input ends in the middle, the program says goodbye instead of crashing, because there is nobody
     * left to answer the questions.
     */
    public void run() {
        try {
            consoleUI.showWelcome();
            var start = consoleUI.chooseStart();
            var destination = consoleUI.chooseDestination();

            consoleUI.showSearching();
            var route = routeFinder.findRoute(start, destination);
            consoleUI.showRoute(routeFormatter.format(route));
        } catch (InputEndedException exception) {
            consoleUI.showInputEnded();
        }
    }
}
