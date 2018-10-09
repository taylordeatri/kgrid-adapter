def content(inputs):
  if isinstance(inputs, dict):
    name = inputs.get("name");
  else:
    name = inputs
  return '{}test'.format(name)
