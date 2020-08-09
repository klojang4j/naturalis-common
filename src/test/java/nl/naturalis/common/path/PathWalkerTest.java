package nl.naturalis.common.path;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import static nl.naturalis.common.CollectionMethods.newHashMap;
import static nl.naturalis.common.CollectionMethods.newLinkedHashMap;

public class PathWalkerTest {

  private static Map<String, Company> createCompanies() throws MalformedURLException {
    return newLinkedHashMap("shell", shell(), "naturalis", naturalis());
  }

  private static Company shell() throws MalformedURLException {
    Company company = new Company();
    company.setName("Shell");
    company.setSales(new BigDecimal(Integer.MAX_VALUE));
    company.setProfit(100_000_000);
    company.setQuarterlySales(new float[][] {
        {10, 11, 12, 13},
        {20, 21, 22, 23},
        {30, 31, 32, 33},
        {40, 41, 42, 43}
    });
    company.setDepartments(new ArrayList<>());
    Department hr = new Department();
    company.getDepartments().add(hr);
    hr.setName("H&R");
    hr.setAddress(new Address("Koeienstraat", 5, "1111AA", "Rotterdam"));
    hr.setTelNos(new String[] {"040-123456"});
    hr.setEmployees(new LinkedHashSet<>());
    Employee piet = new Employee();
    piet.setId(1);
    piet.setFirstName("Piet");
    piet.setLastName("Pietersen");
    piet.setFacebook(new URL("https://facebook.com/piet"));
    piet.setTwitter(null);
    piet.setBirthDate(new int[] {1972, 1, 1});
    piet.setExtraInfo(newHashMap(
        "hobbies", "paardrijden",
        null, "This seems to be a corrupt entry",
        "numberOfPets", 2));
    hr.getEmployees().add(piet);
    Employee jan = new Employee();
    jan.setId(1);
    jan.setFirstName("Jan");
    jan.setLastName("Jansen");
    jan.setFacebook(new URL("https://facebook.com/jan"));
    jan.setTwitter(new URL("https://twitter.com/@jan"));
    jan.setBirthDate(new int[] {1972, 2, 2});
    jan.setExtraInfo(newHashMap(
        "hobbies", "null",
        "allergies", "gluten",
        "married", true,
        "numberOfPets", 0));
    hr.getEmployees().add(jan);
    hr.setManager(jan);
    DevOps devops = new DevOps();
    company.getDepartments().add(devops);
    devops.setName("DevOps");
    devops.setAddress(null);
    devops.setReactiveBingoDates(new int[][] {
        {2020, 9, 3},
        {2021, 2, 7}
    });
    devops.setHipsterFriendly(true);
    devops.setTelNos(new String[] {null, "035-123456"});
    return company;
  }

  private static Company naturalis() throws MalformedURLException {
    Company company = new Company();
    company.setName("Naturalis");
    company.setSales(new BigDecimal(Short.MAX_VALUE));
    company.setProfit(33_000);
    company.setQuarterlySales(new float[][] {
        {10, 11, 12, 13},
        {20, 21, 22, 23}
    });
    company.setDepartments(new ArrayList<>());
    Department research = new Department();
    company.getDepartments().add(research);
    research.setName("Research");
    research.setAddress(new Address("Darwinweg", 2, "22222BB", "Leiden"));
    research.setTelNos(new String[] {"070-123456"});
    research.setEmployees(new LinkedHashSet<>());
    Employee klaas = new Employee();
    klaas.setId(1);
    klaas.setFirstName("Klaas");
    klaas.setLastName("Klaassen");
    klaas.setFacebook(new URL("https://facebook.com/klaas"));
    klaas.setTwitter(null);
    klaas.setBirthDate(new int[] {1972, 3, 3});
    klaas.setExtraInfo(newHashMap(
        "hobbies", "free-jazz",
        "specialism", "boktorren",
        "married", true));
    research.getEmployees().add(klaas);
    Employee marieke = new Employee();
    marieke.setId(1);
    marieke.setFirstName("Marieke");
    marieke.setLastName("Mariekesen");
    marieke.setFacebook(new URL("https://facebook.com/marieke"));
    marieke.setTwitter(new URL("https://twitter.com/@marieke"));
    marieke.setBirthDate(new int[] {1972, 4, 4});
    marieke.setExtraInfo(newHashMap(
        "hobbies", "null",
        "specialism", "microbiology",
        "married", true,
        "numberOfPets", 0));
    research.getEmployees().add(marieke);
    research.setManager(marieke);
    DevOps devops = new DevOps();
    company.getDepartments().add(devops);
    devops.setName("DevOps");
    devops.setAddress(null);
    devops.setReactiveBingoDates(new int[][] {
        null,
        {2021, 3, 3}
    });
    devops.setHipsterFriendly(false);
    devops.setTelNos(new String[] {null, "070-123456"});
    devops.setEmployees(new LinkedHashSet<>());
    Employee trieneke = new Employee();
    trieneke.setId(1);
    trieneke.setFirstName("Trieneke");
    trieneke.setLastName("Trienekesen");
    trieneke.setFacebook(new URL("https://facebook.com/trieneke"));
    trieneke.setTwitter(new URL("https://twitter.com/@trieneke"));
    trieneke.setBirthDate(new int[] {1972, 4, 4});
    trieneke.setExtraInfo(newHashMap(
        "hobbies", new String[] {"fietsen", "lezen"},
        "married", true,
        "favoriteWebSite", new URL("https://youtube.com")));
    return company;
  }

}
