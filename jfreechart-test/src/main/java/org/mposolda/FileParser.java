package org.mposolda;

import java.util.Map;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public interface FileParser {

    Map<String, Map<Integer, Integer>> getData();
}
