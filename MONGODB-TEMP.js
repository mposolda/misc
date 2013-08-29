// HRATKY S JAVASCRIPTEM

function man(name, age) {
  this.name = name;
  this.age = age;
  this.getDescription = function(descPrefix) {
     return descPrefix + ", name=" + this.name + ", age=" + this.age;
  }
}

kok = new man("jouda", 20);
kok.getDescription("ahoj");

kok.setAge = function(age) {
  this.age = age;
}


// MAP-REDUCE HRATKY

db.orders.insert({"cust_id": 1, "cost": 500});
db.orders.insert({"cust_id": 1, "cost": 100});
db.orders.insert({"cust_id": 2, "cost": 200});

// Checking
db.orders.find();

var map1 = function() {  
  emit(this.cust_id, 0);
  emit(this.cust_id, this.cost);
}

var reduce1 = function(key, values) {  
  var k = 1000;
  if (typeof values == "object") {    
    for (i=0 ; i<values.length ; i++) {
      k -= values[i];
      //k -= 1; 
      // k = k + "values[" + i + "]=" + values[i] + ", key=" + key;
    }
    return k;
  } else {
    // Handle the case with single value (not array)
    k -= values;
  }
}

var reduce2 = function(key, values) {
  return Array.sum(values);
}

// Trigger map-reduce and see results
db.orders.mapReduce(map1, reduce1, {"out": "some"});
db.some.find();



// MORE COMPLICATED MAP-REDUCE WITH FINAL EXAMPLE

> db.orders.find();
{ "_id" : ObjectId("521d051831da6bcf356c0969"), "cust_id" : 1, "order_id" : 1, "items" : [ 	{ 	"item_name" : "myitem", 	"cost" : 500 }, 	{ 	"item_name" : "youritem", 	"cost" : 1000 } ] }
{ "_id" : ObjectId("521d068231da806375f0e641"), "cust_id" : 1, "order_id" : 2, "items" : [ 	{ 	"item_name" : "item3", 	"cost" : 50 }, 	{ 	"item_name" : "item4", 	"cost" : 100 } ] }
{ "_id" : ObjectId("521d06a031da4dbe90bd0a0f"), "cust_id" : 2, "order_id" : 3, "items" : [ 	{ 	"item_name" : "item5", 	"cost" : 50 }, 	{ 	"item_name" : "item6", 	"cost" : 10 } ] }


var map1 = function() {
  for (i=0 ; i<this.items.length ; i++) {
    var item = this.items[i];
    emit(this.cust_id, item.cost);
  }
}

var reduce1 = function(key, values) {
  var sum = 0;
  var count = 0;
  for (i=0 ; i<values.length; i++) {
    count ++;
    sum += values[i];
  }
  var result = {"count": count, "sum": sum }
  return result;
}

var finalize1 = function(key, result) {
  var avg = result.sum / result.count;
  result.avg = avg;
  return result;
  // Even this is possible. Return just final average instead of full result;
  // return avg;
}

db.orders.mapReduce(map1, reduce1, { out: "some", "finalize": finalize1 });




