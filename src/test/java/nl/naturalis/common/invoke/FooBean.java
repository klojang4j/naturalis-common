package nl.naturalis.common.invoke;

import java.time.LocalDate;
import java.util.List;

public class FooBean {

  private int id;
  private String firstName;
  private String lastName;
  private LocalDate date;
  private List<String> hobbies;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public LocalDate lastModified() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public List<String> getHobbies() {
    return hobbies;
  }

  public void setHobbies(List<String> hobbies) {
    this.hobbies = hobbies;
  }
}
