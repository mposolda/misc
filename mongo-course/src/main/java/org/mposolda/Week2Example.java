package org.mposolda;

import com.mongodb.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class Week2Example {

    public static void main(String[] args) throws Exception {
        MongoClient client = new MongoClient("localhost", 27017);
        DB db = client.getDB("students");
        DBCollection grades = db.getCollection("grades");

        DBCursor cursor = grades.find(new BasicDBObject("type", "homework")).sort(new BasicDBObject("student_id", 1).append("score", 1));

        int counter = 0;
        List list = new ArrayList();
        while (cursor.hasNext()) {
            DBObject next = cursor.next();
            System.out.println(++counter + ": " + next.get("_id"));
            if (counter % 2 == 1) {
                list.add(next.get("_id"));
            }
        }

        System.out.println(list);

        grades.remove(new BasicDBObject("_id", new BasicDBObject("$in", list)));
    }
}
