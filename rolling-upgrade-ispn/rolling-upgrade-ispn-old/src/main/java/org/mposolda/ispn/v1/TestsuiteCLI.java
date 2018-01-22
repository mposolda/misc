package org.mposolda.ispn.v1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.infinispan.Cache;
import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class TestsuiteCLI<K, V> {

    private static final Logger log = Logger.getLogger(TestsuiteCLI.class);

    private static final Class<?>[] BUILTIN_COMMANDS = {
            ExitCommand.class,
            HelpCommand.class,
            CreateSessionCommand.class,
            ListSessionsCommand.class
            /*AbstractSessionCacheCommand.GetMultipleCommand.class,
            AbstractSessionCacheCommand.GetLocalCommand.class,
            AbstractSessionCacheCommand.SizeLocalCommand.class,
            AbstractSessionCacheCommand.RemoveCommand.class,
            AbstractSessionCacheCommand.SizeCommand.class,
            AbstractSessionCacheCommand.ListCommand.class,
            AbstractSessionCacheCommand.ClearCommand.class,
            AbstractSessionCacheCommand.CreateManySessionsCommand.class,
            AbstractSessionCacheCommand.CreateManySessionsProviderCommand.class,
            PersistSessionsCommand.class,
            LoadPersistentSessionsCommand.class,
            UserCommands.Create.class,
            UserCommands.Remove.class,
            UserCommands.Count.class,
            UserCommands.GetUser.class,
            SyncDummyFederationProviderCommand.class,
            RoleCommands.CreateRoles.class,
            CacheCommands.ListCachesCommand.class,
            CacheCommands.GetCacheCommand.class,
            CacheCommands.CacheRealmObjectsCommand.class,
            ClusterProviderTaskCommand.class,
            LdapManyObjectsInitializerCommand.class,
            LdapManyGroupsInitializerCommand.class*/
    };

    private final Map<String, Class<? extends AbstractCommand>> commands = new LinkedHashMap<>();

    private final Cache<K, V> cache;

    public TestsuiteCLI(Cache<K, V> cache) {
        this.cache = cache;

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
                        AbstractCommand<K, V> command = commandClass.newInstance();
                        List<String> args = new ArrayList<>(Arrays.asList(splits));
                        args.remove(0);
                        command.injectProperties(args, this, cache);
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

    public static class ExitCommand<K, V> extends AbstractCommand<K, V> {

        @Override
        public String getName() {
            return "exit";
        }

        @Override
        public void runCommand() {
            // no need to implement. Exit handled in parent
        }

        @Override
        protected void doRunCommand(Cache<K, V> cache) {
            // no need to implement
        }

        @Override
        public String printUsage() {
            return getName();
        }
    }

    public static class HelpCommand<K, V> extends AbstractCommand<K, V> {

        private List<String> commandNames = new ArrayList<>();

        @Override
        public void injectProperties(List<String> args, TestsuiteCLI<K, V> cli, Cache<K, V> cache) {
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
        protected void doRunCommand(Cache<K, V> cache) {
            // no need to implement
        }
    }
}
