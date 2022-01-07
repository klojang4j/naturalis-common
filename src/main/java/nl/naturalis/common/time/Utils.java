package nl.naturalis.common.time;

import nl.naturalis.common.check.Check;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nl.naturalis.common.ObjectMethods.e2n;
import static nl.naturalis.common.check.CommonChecks.blank;
import static nl.naturalis.common.check.CommonChecks.lt;

class Utils {

  private  static final String ERR_OCCURRENCE = "<${0}> may not occur more than once in <${1}>";
  private static final String ERR_BLANK_ELEM = "Element <${0}> must not be empty";
  private static final String ERR_BLANK_CHILD = "Element <${0}> within <${1}> must not be empty";
  private static final String ERR_BAD_LOCALE = "Invalid locale: \"${0}\". Must match ${1}";

  private static final String LOCALE_REGEX="^([a-zA-Z]{2,8})_([a-zA-Z]{2}|[0-9]{3})$";
  private static final Pattern LOCALE_PATTERN = Pattern.compile(LOCALE_REGEX);

  static <T> Check<T, FuzzyDateException> checkThat(T arg) {
    return Check.on(FuzzyDateException::new, arg);
  }

  static <T> Check<T, FuzzyDateException> checkThat(T arg, String argName) {
    return Check.on(FuzzyDateException::new, arg, argName);
  }

  static <T> T fail(String msg, Object... msgArgs) throws FuzzyDateException {
    return Check.fail(FuzzyDateException::new, msg, msgArgs);
  }

  static List<Element> xmlGetChildElements(Element e) {
    NodeList nl = e.getChildNodes();
    List<Element> children = new ArrayList<>(nl.getLength());
    for(int i=0;i< nl.getLength();++i) {
      if(nl.item(i) instanceof  Element elem) {
        children.add(elem);
      }
    }
    return children;
  }

  static String xmlGetRequiredTextContent(Element e) throws FuzzyDateException {
    return checkThat(e.getTextContent())
            .isNot(blank(), ERR_BLANK_ELEM, e.getTagName())
            .ok(String::strip);
  }

   static String xmlGetTextContent(Element e, String childTagName) {
    NodeList nl = e.getElementsByTagName(childTagName);
    Check.that(nl).has(NodeList::getLength, lt(), 2, ERR_OCCURRENCE, childTagName, e.getTagName());
    if (nl.getLength() == 0) {
      return null;
    }
    return e2n(nl.item(0).getTextContent().strip());
  }

  static Element xmlGetChildElement(Element e, String childTagName) {
    NodeList nl = e.getElementsByTagName(childTagName);
    Check.that(nl).has(NodeList::getLength, lt(), 2, ERR_OCCURRENCE, childTagName, e.getTagName());
    return nl.getLength() == 0 ? null : ((Element) nl.item(0));
  }

  static Locale toLocale(String s) throws FuzzyDateException {
    Matcher m = LOCALE_PATTERN.matcher(s = s.strip());
    if (m.matches()) {
      return new Locale.Builder().setLanguage(m.group(1)).setRegion(m.group(2)).build();
    }
    return fail(ERR_BAD_LOCALE, s, LOCALE_PATTERN);
  }
}
