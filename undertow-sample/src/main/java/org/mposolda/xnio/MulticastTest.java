package org.mposolda.xnio;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class MulticastTest {

    private static final String CRLF = "\r\n";


    private static final String server = "4c31f0eb-fe2b-4825-b9f8-3ece9e4296b9";
    private static final String host = "localhost";
    private static final int port = 8080;
    private static final String protocol = "http";
    private static final String path = "/";

    public static final String RFC_822_FMT = "EEE, d MMM yyyy HH:mm:ss Z";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(RFC_822_FMT, Locale.US);
    private static volatile int seq = 0;



    public static void main(String[] args) throws Exception {
        final InetAddress group = InetAddress.getByName("224.0.1.105");
        InetSocketAddress bindAddress = new InetSocketAddress(0);
        InetSocketAddress address = new InetSocketAddress(group, 0); // Here is an error!

        DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);

        channel.configureBlocking(false);
        channel.socket().setReuseAddress(true);
        channel.socket().bind(bindAddress);

        final String date = DATE_FORMAT.format(new Date(System.currentTimeMillis()));
        final String digestString = getDigest(date);
        final StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.0 200 OK").append(CRLF)
                .append("Date: ").append(date).append(CRLF)
                .append("Sequence: ").append(seq).append(CRLF)
                .append("Digest: ").append(digestString).append(CRLF)
                .append("Server: ").append(server).append(CRLF)
                .append("X-Manager-Address: ").append(host).append(":").append(port).append(CRLF)
                .append("X-Manager-Url: ").append(path).append(CRLF)
                .append("X-Manager-Protocol: ").append(protocol).append(CRLF)
                .append("X-Manager-Host: ").append(host).append(CRLF);

        final String payload = builder.toString();
        final ByteBuffer byteBuffer = ByteBuffer.wrap(payload.getBytes(StandardCharsets.US_ASCII));

        int sent = channel.send(byteBuffer, address);
        System.out.println("Sent: " + sent);


        /*final Charset charset = Charset.forName("utf-8");
        final Xnio xnio = Xnio.getInstance();
        xnio.
        try {
            final TcpConnector connector = xnio.createTcpConnector(OptionMap.EMPTY);
            final IoFuture<TcpChannel> futureConnection = connector.connectTo(new InetSocketAddress("localhost", 12345), null, null);
            final TcpChannel channel = futureConnection.get();
            try {
                // Send the greeting
                Channels.writeBlocking(channel, ByteBuffer.wrap("Hello world!\n".getBytes(charset)));
                // Make sure all data is written
                Channels.flushBlocking(channel);
                // And send EOF
                channel.shutdownWrites();
                System.out.println("Sent greeting string!  The response is...");
                ByteBuffer recvBuf = ByteBuffer.allocate(128);
                // Now receive and print the whole response
                while (Channels.readBlocking(channel, recvBuf) != -1) {
                    recvBuf.flip();
                    final CharBuffer chars = charset.decode(recvBuf);
                    System.out.print(chars);
                    recvBuf.clear();
                }
            } finally {
                IoUtils.safeClose(channel);
            }
        } finally {
            IoUtils.safeClose(xnio);
        }*/
    }

    private static String getDigest(String date) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        final byte[] ssalt = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        final String seq = "" + MulticastTest.seq++;

        final byte[] digest;
        synchronized (md) {
            md.reset();
            md.update(ssalt);
            digestString(md, date);
            digestString(md, seq);
            digestString(md, server);
            digest = md.digest();
        }
        return bytesToHexString(digest);
    }

    private static void digestString(MessageDigest md, String securityKey) {
        byte[] buf = securityKey.getBytes();
        md.update(buf);
    }

    private static final char[] TABLE = "0123456789abcdef".toCharArray();
    static String bytesToHexString(final byte[] bytes) {
        final StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            builder.append(TABLE[b >> 4 & 0x0f]).append(TABLE[b & 0x0f]);
        }
        return builder.toString();
    }
}
