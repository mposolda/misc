package others;

import java.util.HashSet;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class Question7 {

    public static void main(String[] args) throws Exception {
        MongoClient c =  new MongoClient(new MongoClientURI("mongodb://localhost"));
        DB db = c.getDB("exam");

        DBCollection images = db.getCollection("images");
        DBCollection albums = db.getCollection("albums");

        DBCursor imageCursor = images.find(new BasicDBObject(), new BasicDBObject("_id", 1));
        int counter = 0;

        Set<Integer> imagesToRemove = new HashSet<Integer>();
        while (imageCursor.hasNext()) {
            counter++;
            if (counter % 50 == 0) {
                System.out.println("Counter: " + counter + ", imagesToRemove: " + imagesToRemove);
            }

            DBObject image = imageCursor.next();
            int imageId = (Integer)image.get("_id");

            DBCursor albumsCursor = albums.find(new BasicDBObject("images", imageId));
            if (!albumsCursor.hasNext()) {
                imagesToRemove.add(imageId);
            }
        }

        System.out.println("Total images to remove: " + imagesToRemove);
        counter = 0;
        for (Integer imageId : imagesToRemove) {
            images.remove(new BasicDBObject("_id", imageId));
            counter++;
            if (counter % 50 == 0) {
                System.out.println("Removed images: " + counter);
            }
        }
    }
}
