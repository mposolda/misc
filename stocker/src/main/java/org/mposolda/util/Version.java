package org.mposolda.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Version {
    public static final String UNKNOWN = "UNKNOWN";
    public static String NAME;
    public static String NAME_FULL;
    public static String NAME_HTML;
    public static String VERSION;
    public static String VERSION_STOCKER;
    public static String RESOURCES_VERSION;
    public static String BUILD_TIME;
    public static String DEFAULT_PROFILE;

    static {
        try (InputStream is = Version.class.getResourceAsStream("/stocker-version.properties")) {
            Properties props = new Properties();
            props.load(is);
            Version.NAME = props.getProperty("name");
            Version.NAME_FULL = props.getProperty("name-full");
            Version.NAME_HTML = props.getProperty("name-html");
            Version.DEFAULT_PROFILE = props.getProperty("default-profile");
            Version.VERSION = props.getProperty("version");
            Version.VERSION_STOCKER = props.getProperty("version-stocker");
            Version.BUILD_TIME = props.getProperty("build-time");
            Version.RESOURCES_VERSION = Version.VERSION.toLowerCase();

            if (Version.RESOURCES_VERSION.endsWith("-snapshot")) {
                Version.RESOURCES_VERSION = Version.RESOURCES_VERSION.replace("-snapshot", "-" + Version.BUILD_TIME.replace(" ", "").replace(":", "").replace("-", ""));
            }
        } catch (IOException e) {
            Version.VERSION = Version.UNKNOWN;
            Version.BUILD_TIME = Version.UNKNOWN;
        }
    }

}
