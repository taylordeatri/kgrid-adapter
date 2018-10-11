import math


def exp(inputs):
  if isinstance(inputs, dict):
    number = float(inputs.get("num"))
  else:
    number = inputs
  return math.exp(number)
