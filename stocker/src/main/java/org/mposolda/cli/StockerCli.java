package org.mposolda.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.mposolda.services.Services;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class StockerCli {

    private static final Logger log = Logger.getLogger(StockerCli.class);

    private static final Class<?>[] BUILTIN_COMMANDS = {
            CompanyProfileCommand.class,
            DateConvertCommand.class,
            TimestamptToDateCommand.class,
            QuoteCommand.class,
            StockCandleCommand.class,
            CurrencyConvertCommand.class,
            ExitCommand.class,
            HelpCommand.class,
    };

    private final Map<String, Class<? extends AbstractCommand>> commands = new LinkedHashMap<>();
    private final Services services;

    public StockerCli(Services services) {
        this.services = services;

        // register builtin commands
        for (Class<?> clazz : BUILTIN_COMMANDS) {
            Class<? extends AbstractCommand> commandClazz = (Class<? extends AbstractCommand>) clazz;
            try {
                AbstractCommand command = commandClazz.newInstance();
                commands.put(command.getName(), commandClazz);
            } catch (Exception ex) {
                log.error("Error registering command of class: " + commandClazz.getName(), ex);
            }
        }
    }

    public void registerCommand(String name, Class<? extends AbstractCommand> command) {
        commands.put(name, command);
    }

    // WARNING: Stdin blocking operation
    public void start() throws IOException {
        log.info("Starting testsuite CLI. Exit with 'exit' . Available commands with 'help' ");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        System.out.print("$ ");
        try {
            while ((line = reader.readLine()) != null) {
                String[] splits = line.split(" ");
                String commandName = splits[0];
                Class<? extends AbstractCommand> commandClass = commands.get(commandName);
                if (commandClass == null) {
                    log.errorf("Unknown command: %s", commandName);
                } else {
                    try {
                        AbstractCommand command = commandClass.newInstance();
                        List<String> args = new ArrayList<>(Arrays.asList(splits));
                        args.remove(0);
                        command.injectProperties(args, this, services);
                        command.runCommand();

                        // Just special handling of ExitCommand
                        if (command instanceof ExitCommand) {
                            return;
                        }

                    } catch (InstantiationException ex) {
                        log.error(ex);
                    } catch (IllegalAccessException ex) {
                        log.error(ex);
                    }
                }

                System.out.print("$ ");
            }
        } finally {
            log.info("Exit testsuite CLI");
            reader.close();
        }
    }

    public static class ExitCommand extends AbstractCommand {

        @Override
        public String getName() {
            return "exit";
        }

        @Override
        protected void doRunCommand() {
            log.info("Exiting");
            System.exit(0);
        }
    }

    public static class HelpCommand extends AbstractCommand {

        private List<String> commandNames = new ArrayList<>();

        @Override
        public void injectProperties(List<String> args, StockerCli cli, Services services) {
            for (String commandName : cli.commands.keySet()) {
                commandNames.add(commandName);
            }
        }

        @Override
        public String getName() {
            return "help";
        }

        @Override
        public void runCommand() {
            log.info("Available commands: " + commandNames.toString());
        }

        @Override
        protected void doRunCommand() {
            // no need to implement
        }
    }
}
