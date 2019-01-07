function math(input){

  var MyMath = Java.type("org.kgrid.adapter.javascript.utils.MyMath")

  input.result1 = MyMath.doubler(input.value1);

  var m = new MyMath();

  input.result2 = m.add(input.value2[0], input.value2[1]);

  return input;
}

function math2 (input) {

  print("context: " + context);

  print(input.value2);

  var exec = context.getExecutor("a-b/d/welcome");

  input.execResult = exec.execute({name: "Ted"});

  var answer = input.value2[0] + input.value2[1]

  input.execResult += "! Answer is: " + answer

  print(input)

  return input;

}