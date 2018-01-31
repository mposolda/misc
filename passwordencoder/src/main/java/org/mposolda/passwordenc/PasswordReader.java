package org.mposolda.passwordenc;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public abstract class PasswordReader {

    public static String readPassword() throws IOException {
        Console console = System.console();
        if (console == null) {
            System.out.print("Password: ");
            String line = new BufferedReader(new InputStreamReader(System.in)).readLine();
            return line;
        } else {
            char[] passwd = System.console().readPassword("%s", "Password:");
            String str = new String(passwd);
            return str;
        }
    }
}
