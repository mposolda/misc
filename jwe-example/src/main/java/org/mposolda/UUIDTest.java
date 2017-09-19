package org.mposolda;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.keycloak.common.util.Base64Url;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class UUIDTest {

    public static void main(String[] args) throws Exception {
        String uuid = UUID.randomUUID().toString();
        System.out.println("uuid: " + uuid + ", uuid length: " + uuid.length());

        UUID uuid1 = UUID.fromString(uuid);
        long l1 = uuid1.getLeastSignificantBits();
        long l2 = uuid1.getMostSignificantBits();

        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(l2);
        bb.putLong(l1);
        byte[] asBytes = bb.array();
        String s = Base64Url.encode(asBytes);
        System.out.println("S: " + s + ", s.length: " + s.length());

        // Reconstruct UUID back
        byte[] bytes = Base64Url.decode(s);
        System.out.println("Hello");
    }
}
