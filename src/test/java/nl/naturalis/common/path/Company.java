package nl.naturalis.common.path;

import java.math.BigDecimal;
import java.util.List;

public class Company {

  private String name;
  private BigDecimal sales;
  private float profit;
  private float[][] quarterlySales;
  private List<Department> departments;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getSales() {
    return sales;
  }

  public void setSales(BigDecimal sales) {
    this.sales = sales;
  }

  public float getProfit() {
    return profit;
  }

  public void setProfit(float profit) {
    this.profit = profit;
  }

  public float[][] getQuarterlySales() {
    return quarterlySales;
  }

  public void setQuarterlySales(float[][] quarterlySales) {
    this.quarterlySales = quarterlySales;
  }

  public List<Department> getDepartments() {
    return departments;
  }

  public void setDepartments(List<Department> departments) {
    this.departments = departments;
  }

}
