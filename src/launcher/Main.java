package launcher;

import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import controller.LocalController;
import logic.gamemodes.ClassicGameMode;
import view.ConsoleView;
import view.MainMenu;
import view.ViewMode;

public class Main {
	
	private static CommandLineParser commandLineParser;
	private static Options commandLineOptions;
	private static ViewMode viewMode;
	private static boolean showHelp;
	
	static {
		commandLineParser = new DefaultParser();
		commandLineOptions = new Options();
		commandLineOptions.addOption(Option.builder("h").longOpt("help").desc("Show information about command line arguments.").hasArg(false).build());
		commandLineOptions.addOption(Option.builder("m").longOpt("mode").desc("Choose view mode (command line or GUI).").hasArg().argName("console/GUI").build());
		viewMode = null;
		showHelp = false;
	}
	
	private static void showHelp() {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp("Domino", commandLineOptions, true);
	}
	
	private static void parseArguments(String[] args) {
		try {
			CommandLine commandLine = commandLineParser.parse(commandLineOptions, args);
			if(commandLine.hasOption('h')) {
				showHelp = true;
			}
			viewMode = ViewMode.fromString(commandLine.getOptionValue('m', "GUI"));
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	private static void startConsoleMode() {
		LocalController localController = new LocalController();
		ConsoleView consoleView = new ConsoleView(localController);
		List<String> playerNames = consoleView.askHumanPlayers();
		localController.reset(playerNames, consoleView.askAiPlayers(playerNames.size()), new ClassicGameMode(consoleView.askRoundCount()));
	}
	
	public static void main(String[] args) {
		parseArguments(args);
		if(showHelp) {
			showHelp();
		}
		else {
			if(viewMode == ViewMode.CONSOLE) {
				startConsoleMode();
			}
			else {
				SwingUtilities.invokeLater(() -> new MainMenu());
			}
		}
	}
}
