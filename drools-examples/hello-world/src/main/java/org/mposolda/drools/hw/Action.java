package org.mposolda.drools.hw;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class Action {

    public void performAction(Message message) {
        message.printMessage();
    }

}
