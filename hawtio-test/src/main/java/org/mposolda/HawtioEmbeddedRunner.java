package org.mposolda;

import io.hawt.embedded.Main;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class HawtioEmbeddedRunner {

    public static void main(String[] args) throws Exception {
        System.out.println("Running embedded hawtio");

        Main main = new Main();
        main.setWar("/home/mposolda/tmp/hawtio.war");
        main.run();

        System.out.println("hawtio executed");
    }
}
