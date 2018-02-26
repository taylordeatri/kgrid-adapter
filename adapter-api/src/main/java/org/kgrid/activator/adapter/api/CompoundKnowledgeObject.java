package org.kgrid.activator.adapter.api;

public class CompoundKnowledgeObject {

  private String inputType;
  private String outputType;
  private String code;
  private String endpoint;

  public CompoundKnowledgeObject() {
  }

  public CompoundKnowledgeObject(CompoundKnowledgeObject other) {
    this.inputType = other.inputType;
    this.outputType = other.outputType;
    this.code = other.code;
    this.endpoint = other.endpoint;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Class<?> getInputClass() {
    try {
      return Class.forName(inputType);
    } catch (NullPointerException | ClassNotFoundException e) {
      return String.class;
    }

  }

  public void setInputType(String inputType) {
    this.inputType = inputType;
  }

  public Class getOutputClass() {
    try {
      return Class.forName(outputType);
    } catch (ClassNotFoundException e) {
      return String.class;
    }
  }

  public void setOutputType(String outputType) {
    this.outputType = outputType;
  }

  @Override
  public String toString() {
    return "CompoundKnowledgeObject{" +
        "code='" + code + '\'' +
        ", endpoint='" + endpoint + '\'' +
        ", inputClass=" + getInputClass() +
        ", outputClass=" + getOutputClass() +
        '}';
  }
}
