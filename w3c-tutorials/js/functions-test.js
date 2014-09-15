// ANONYMOUS JAVASCRIPT FUNCTION
(function (window, message) {

  // MULTIPLE POSSIBILITIES HOW TO DEFINE FUNCTION
  function test1(arg1) {
    console.log("test1: " + arg1);
  }

  test2 = function(arg2) {
    console.log("test2: " + arg2);
  }

  test1(message);
  test2(message);

}) (window, "ahojky");

// ----------------------------- TYPEOF EXAMPLE

b1 = "a";
b2 = true;
b3 = false;

options = {
  b5: false,
  b7: "falsee"
}

if (b1) {
  console.log("b1");
}

if (b2) {
  console.log("b2");
}

if (b3) {
  console.log("b3");
}

/* THIS IS ERROR
if (b4) {
  console.log("b4");
}
*/

if (options.b5) {
  console.log("options.b5");
}

if (options.b6) {
  console.log("options.b6");
}

options.b7 = options.b7 || "goo";
console.log("options.b7=" + options.b7);

//console.log(typeof b1);
//console.log(typeof b2);
//console.log(typeof options.b7);

if (typeof options === "object") {
  console.log("OPTIONS IS OBJECT!!!")
}

if (typeof options.b7 === "object") {
   console.log("OPTIONS.B7 IS OBJECT!!!")
}

if (typeof options.b7 === "string") {
   console.log("OPTIONS.B7 IS String!!!")
}

b8 = null;
b8 = b8 || "kokos";
console.log("b8: " + b8);

// DEFINE WINDOW FUNCTIONS

var class1 = function() {
  this.a = "val1";
};

(function () {
  var class2 = function() {
    this.a = "val2";
    var b = "kokos";
    console.log("Creating instance of class2");
    // THIS IS NOT WORKING. "a" is not defined
    // console.log("a=" + a);
    console.log("this.a=" + this.a);
    console.log("b=" + b);
  }

  // expose it to window
  window.class2 = class2;
}) ();

obj1 = new class1();

// Only "a" will be part of the object, but not "b"!!!!
obj2 = new class2();

obj3 = class1();

console.log(obj1);
console.log(obj2);
console.log(obj3);






