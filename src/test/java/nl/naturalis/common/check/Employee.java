package nl.naturalis.common.check;

import java.util.List;

class Employee {
  int id;
  String fullName;
  Integer age;
  List<String> hobbies;
  float[] justSomeNumbers;

  Employee() {}

  Employee(int id, String fullName, Integer age, String... hobbies) {
    super();
    this.id = id;
    this.fullName = fullName;
    this.age = age;
    this.hobbies = List.of(hobbies);
  }

  int getId() {
    return id;
  }

  void setId(int id) {
    this.id = id;
  }

  String getFullName() {
    return fullName;
  }

  void setFullName(String fullName) {
    this.fullName = fullName;
  }

  Integer getAge() {
    return age;
  }

  void setAge(Integer age) {
    this.age = age;
  }

  List<String> getHobbies() {
    return hobbies;
  }

  void setHobbies(List<String> hobbies) {
    this.hobbies = hobbies;
  }

  float[] getJustSomeNumbers() {
    return justSomeNumbers;
  }

  void setJustSomeNumbers(float[] justSomeNumbers) {
    this.justSomeNumbers = justSomeNumbers;
  }
}
