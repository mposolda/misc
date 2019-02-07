package org.mposolda.expiration;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FooTest {

    public static void main(String[] args) {
        printSegmentsCount(0, 100, -1);
        printSegmentsCount(0, 100, 256);
        printSegmentsCount(5, 100, 256);
        printSegmentsCount(99, 100, 256);
        printSegmentsCount(100, 100, 256);
        printSegmentsCount(101, 100, 256);
        printSegmentsCount(199, 100, 256);
        printSegmentsCount(1785, 100, 256);

        printSegmentsCount(3199, 100, 256);
        printSegmentsCount(3200, 100, 256);
        printSegmentsCount(3201, 100, 256);

        printSegmentsCount(1000, 100, 256);
        printSegmentsCount(10000, 100, 256);
        printSegmentsCount(1000000, 100, 256);
        printSegmentsCount(10000000, 100, 256);
    }

    protected static void printSegmentsCount(int sessionsTotal, int sessionsPerSegment, int ispnSegments) {
        System.out.println(sessionsTotal + ", " + sessionsPerSegment + " = " + getSegmentsCount(sessionsTotal, sessionsPerSegment, ispnSegments));
    }

    protected static int getSegmentsCount(int sessionsTotal, int sessionsPerSegment, int ispnSegments) {
        // No support by remote ISPN cache for segments. This can happen if remoteCache is local (non-clustered)
        if (ispnSegments < 0) {
            return 1;
        }

        int seg = sessionsTotal / sessionsPerSegment;
        if (sessionsTotal % sessionsPerSegment > 0) {
            seg = seg + 1;
        }

        int seg2 = 1;
        while (seg2<seg && seg2<ispnSegments) {
            seg2 = seg2 << 1;
        }

        return seg2;
    }
}
