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




