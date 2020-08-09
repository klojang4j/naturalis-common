package nl.naturalis.common.path;

import java.net.URL;
import java.util.Map;

class Employee extends Person {

  private int id;
  private double salary;
  private int[] birthDate;
  private URL twitter;
  private URL facebook;
  private Map<String, Object> extraInfo;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public double getSalary() {
    return salary;
  }

  public void setSalary(double salary) {
    this.salary = salary;
  }

  public int[] getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(int[] birthDate) {
    this.birthDate = birthDate;
  }

  public URL getTwitter() {
    return twitter;
  }

  public void setTwitter(URL twitter) {
    this.twitter = twitter;
  }

  public URL getFacebook() {
    return facebook;
  }

  public void setFacebook(URL facebook) {
    this.facebook = facebook;
  }

  public Map<String, Object> getExtraInfo() {
    return extraInfo;
  }

  public void setExtraInfo(Map<String, Object> extraInfo) {
    this.extraInfo = extraInfo;
  }

}
