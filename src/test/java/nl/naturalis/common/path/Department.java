package nl.naturalis.common.path;

import java.util.Set;

public class Department {

  private String name;
  private Address address;
  private String[] telNos;
  private Employee manager;
  private Set<Employee> employees;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public String[] getTelNos() {
    return telNos;
  }

  public void setTelNos(String[] telNos) {
    this.telNos = telNos;
  }

  public Employee getManager() {
    return manager;
  }

  public void setManager(Employee manager) {
    this.manager = manager;
  }

  public Set<Employee> getEmployees() {
    return employees;
  }

  public void setEmployees(Set<Employee> employees) {
    this.employees = employees;
  }

}
