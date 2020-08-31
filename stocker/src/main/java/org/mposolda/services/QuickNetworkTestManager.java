package org.mposolda.services;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import org.mposolda.StockerServer;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class QuickNetworkTestManager {

    public void test() {
        try {
            Socket socket = new Socket(StockerServer.HOST, StockerServer.PORT);
            socket.close();
            throw new RuntimeException("Address already in use " + StockerServer.HOST + ":" + StockerServer.PORT);
        } catch (ConnectException e) {
            // This is expected
        } catch (IOException e) {
            // Unexpected exception
            throw new RuntimeException(e);
        }

    }
}
