package org.mposolda.undertow;

import java.io.InputStream;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class UndertowIFrameFrontChannelLogoutSampleServer {

    public static void main(String[] args) {
        bootstrapServer(8080);
        bootstrapServer(8081);
    }

    private static void bootstrapServer(int port) {
        System.out.println("Bootstrapping on port " + port);

        Undertow server = Undertow.builder()
                .addHttpListener(port, "localhost")
                .setIoThreads(8)
                .setWorkerThreads(8)
                .setHandler(new HttpHandler() {

                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        System.out.println("Handled request: " + Thread.currentThread().getName());
                        HttpString reqMethod = exchange.getRequestMethod();
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");

                        if (reqMethod.toString().equals("GET")) {
                            exchange.getResponseSender().send("<h1>Hello World!</h1>Method: GET<br>Body: "
                                    + exchange.getQueryString());
                        } else if (reqMethod.toString().equals("POST")) {
                            exchange.startBlocking();

                            InputStream is = exchange.getInputStream();
                            String ss = StreamUtil.readStringAsync(is);

                            String response = "<h1>Hello World!</h1>Method: POST<br>Body: " + ss;

                            Thread t = new Thread() {

                                public void run() {
                                    try {
                                        exchange.getResponseSender().send(response);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            };
                            t.start();
                            t.join();
                        }

                        }

                }).build();
        server.start();
    }

}
