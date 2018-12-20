function math(input){

  var MyMath = Java.type("org.kgrid.adapter.javascript.utils.MyMath")

  input.result1 = MyMath.doubler(input.value1);

  var m = new MyMath();

  input.result2 = m.add(input.value2[0], input.value2[1]);

  return input;
}

function math2 (input) {

  print(endpoints);

  var executor = endpoints["a-b/d/welcome"].getExecutor();

  // input.result2 = myMath.execute(input.value2[0], input.value2[1]);
  input.result2 = executor.execute({name: "Bob"});

  var answer = input.value2[0] + input.value2[1]

  input.result2 += "! Answer is: " + answer

  print(input)

  return input;

}

function info () {
  return "Greets a person by name."
}