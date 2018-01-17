package org.mposolda.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FileDownloader {

    private static int valid = 0;
    private static int invalid = 0;

    //public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        String zoneramaSourceFile = System.getProperty("sourceFile");
        String targetDir = System.getProperty("targetDir");

        List<String> photos = readZoneramaFile(zoneramaSourceFile);


        CloseableHttpClient client = null;
        try {
            client = HttpClients.createDefault();

            System.out.println("Open initial page");

            CloseableHttpResponse response = client.execute(new HttpGet("https://www.zonerama.com/famigliafotografo/Album/3902578?secret=a4umCvS2V3CuKU9e7dc2P1e63"));
            response.close();

            System.out.println("Start downloading");



            File outputDir = new File(targetDir);
            if (!outputDir.exists()) {
                throw new FileNotFoundException(outputDir.getAbsolutePath());
            }

            System.out.println("Downloading to directory: " + outputDir.getAbsolutePath());

            for (String pictureURL : photos) {
                String pictureName = pictureURL.substring(32, 53) + ".jpg";

                downloadFile(client, pictureURL, pictureName, outputDir);
            }

            System.out.println("Downloaded all pictures in directory: " + outputDir.getAbsolutePath() + ". Valid pictures: " + valid + ", invalid pictures: "  + invalid);
        } finally {
            if (client != null) {
                client.close();
            }
        }

    }


    private static List<String> readZoneramaFile(String zoneramaSourceFile) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(zoneramaSourceFile)));

        List<String> photos = new LinkedList<>();

        try {
            boolean finished = false;
            while (!finished) {
                String line = r.readLine();

                if (line == null || line.trim().length() == 0) {
                    finished = true;
                } else {
                    int start = line.indexOf("https://");
                    int end = line.indexOf("\"", start + 1);
                    String substr = line.substring(start, end);

                    substr = substr.replace("{width}", "12000");
                    substr = substr.replace("{height}", "18000");

                    photos.add(substr);
                }
            }
        } finally {
            r.close();
        }

        return photos;
    }


    private static void downloadFile(CloseableHttpClient client, String pictureURL, String pictureName, File outputDir) throws Exception {
        CloseableHttpResponse response1 = client.execute(new HttpGet("https://www.zonerama.com/famigliafotografo/Album/3902578?secret=a4umCvS2V3CuKU9e7dc2P1e63"));
        response1.close();

        System.out.println("downloading picture: " + pictureName);
        long start = System.currentTimeMillis();

        File file = new File(outputDir + "/" + pictureName);

        try (CloseableHttpResponse response = client.execute(new HttpGet(pictureURL))) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (FileOutputStream outstream = new FileOutputStream(file)) {
                    entity.writeTo(outstream);
                }
            }
        }

        // Invalid picture
        long took = System.currentTimeMillis() - start;
        System.out.println("Downloaded valid picture: " + pictureName + ". Took: " + took + " ms");
        valid++;

        //http://images.all-free-download.com/images/graphiclarge/beautiful_flowers_highdefinition_picture_166908.jpg
    }
}
