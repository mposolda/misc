// Javascript to fix homework 3.1 in mongo shell
cursor = db.students.find(); null

while (cursor.hasNext()) {
  dd = cursor.next();
  min=100000;
  for (i=0; i<dd.scores.length ; i++) {
     sc = dd.scores[i];
     if (sc.type == "homework" && min>sc.score) {
       min=sc.score;
     }
  }

  ddnew = [];
  for (i=0; i<dd.scores.length ; i++) {
     sc = dd.scores[i];
     if (sc.score != min) {
        ddnew.push(sc);
     }
  }
  dd.scores = ddnew;

  db.students.update({"_id":dd._id}, {$set: {"scores": dd.scores}});
}
