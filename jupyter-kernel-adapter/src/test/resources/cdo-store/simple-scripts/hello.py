def hello(inputs):
  if isinstance(inputs, dict):
    name = inputs.get("name")
  else:
    name = inputs
  return 'hello {}'.format(name)
