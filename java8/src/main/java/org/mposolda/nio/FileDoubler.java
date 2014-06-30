package org.mposolda.nio;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FileDoubler {

    public static void main(String[] args) throws Exception {
        RandomAccessFile file = new RandomAccessFile("/tmp/some.txt", "rw");
        FileChannel fileChannel = file.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(12);
        int size = fileChannel.read(buffer);
        while (size != -1) {
            System.out.println("Size=" + size);
            buffer.flip();

            fileChannel.write(buffer);
//            while (buffer.hasRemaining()) {
//                char c = (char)buffer.get();
//                System.out.print(c);
//            }
            buffer.clear();

            size = fileChannel.read(buffer);
        }
    }
}
