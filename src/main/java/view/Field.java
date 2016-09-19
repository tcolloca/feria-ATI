package view;

public class Field {

  private final String name;
  private final String hint;

  public Field(String name, String hint) {
    this.name = name;
    this.hint = hint;
  }

  public String getName() {
    return name;
  }

  public String getHint() {
    return hint;
  }
}
