package nl.naturalis.common.time;

import nl.naturalis.common.Bool;
import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.check.Check;
import nl.naturalis.common.util.EnumParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static nl.naturalis.common.ObjectMethods.ifNull;
import static nl.naturalis.common.ObjectMethods.isEmpty;
import static nl.naturalis.common.check.CommonChecks.*;
import static nl.naturalis.common.time.FuzzyDateException.cannotCreateFormatter;
import static nl.naturalis.common.time.Utils.*;

class ConfigReader {

  private static final String ERR_NOT_SUPPORTED = "Invalid or unsupported date/time type: ${arg}";
  private static final String ERR_UNEXPECTED_ELEM = "Element <${arg}> not allowed within <${0}>";
  private static final String ERR_BAD_ATTR = "Illegal attribute \"${arg}\" for element <${0}>";
  private static final String ERR_BLANK_ATTR = "Attribute ${0} must not have empty value";

  private static final Map<String, List<TemporalQuery<TemporalAccessor>>> supported =
      Map.of(
          Instant.class.getSimpleName(),
          List.of(Instant::from),
          OffsetDateTime.class.getSimpleName(),
          List.of(Instant::from),
          LocalDateTime.class.getSimpleName(),
          List.of(LocalDateTime::from),
          LocalDate.class.getSimpleName(),
          List.of(LocalDate::from),
          YearMonth.class.getSimpleName(),
          List.of(YearMonth::from),
          Year.class.getSimpleName(),
          List.of(Year::from));

  private static final EnumParser<ResolverStyle> resolverStyleParser =
      new EnumParser<>(ResolverStyle.class);

  private final InputStream is;

  private DateStringFilter defFilter = null;
  private ResolverStyle defResolverStyle = null;
  private Boolean defCaseSensitive = null;
  private List<Locale> defLocales = null;

  ConfigReader(InputStream is) {
    this.is = is;
  }

  ConfigReader(InputStream is, ParseDefaults defaults) {
    this.is = is;
    this.defResolverStyle = defaults.resolverStyle();
    this.defCaseSensitive = defaults.caseSensitive();
    this.defLocales = defaults.locales();
    this.defFilter = defaults.filter();
  }

  List<ParseAttempt> getConfig()
      throws ParserConfigurationException, IOException, SAXException, FuzzyDateException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newDefaultInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document doc = db.parse(is);
    Element root = doc.getDocumentElement();
    Element defaults = xmlGetChildElement(root, "ParseDefaults");
    setDefaults(defaults); // Must be called *before* processing other elements!!
    List<ParseAttempt> attempts = new ArrayList<>();
    for (Element e : xmlGetChildElements(root)) {
      if (!e.getTagName().equals("ParseDefaults")) {
        addParseAttempts(attempts, e);
      }
    }
    return attempts;
  }

  private void setDefaults(Element e) throws FuzzyDateException {
    // The defaults passed in through the constructor (in de ParseDefaults object)
    // take precedence over the defaults in the <ParseDefaults> element. Therefore,
    // only set the defXXX fields if they are still null.
    if (e == null) {
      defResolverStyle = ifNull(defResolverStyle, ResolverStyle.LENIENT);
      defCaseSensitive = ifNull(defCaseSensitive, false);
      defLocales = ifNull(defLocales, List.of(Locale.getDefault()));
    } else {
      if (defFilter == null) {
        defFilter = getFilter(xmlGetTextContent(e, "filter"));
      }
      if (defResolverStyle == null) {
        defResolverStyle = getResolverStyle(xmlGetTextContent(e, "resolverStyle"));
      }
      if (defCaseSensitive == null) {
        defCaseSensitive = Bool.from(xmlGetTextContent(e, "caseSensitive"));
      }
      if (defLocales == null) {
        defLocales = getLocales(xmlGetTextContent(e, "locales"));
      }
    }
  }

  private void addParseAttempts(List<ParseAttempt> attempts, Element dateTimeElem)
      throws FuzzyDateException {
    String dateTimeClass = dateTimeElem.getTagName();
    checkThat(dateTimeClass).is(keyIn(), supported, ERR_NOT_SUPPORTED);
    for (Element tryElem : xmlGetChildElements(dateTimeElem)) {
      checkThat(tryElem.getTagName()).is(equalTo(), "try", ERR_UNEXPECTED_ELEM, dateTimeClass);
      processTryElement(attempts, tryElem);
    }
  }

  private class TryAttribs {
    String tag;
    ResolverStyle resolverStyle;
    boolean predefined;
    boolean caseSensitive;
    List<Locale> locales;
    DateStringFilter filter;

    TryAttribs() {
      resolverStyle = defResolverStyle;
      caseSensitive = defCaseSensitive;
      locales = defLocales;
      filter = defFilter;
    }
  }

  private void processTryElement(List<ParseAttempt> attempts, Element tryElement)
      throws FuzzyDateException {
    String target = tryElement.getParentNode().getNodeName();
    var parseInto = supported.get(target);
    String content = xmlGetRequiredTextContent(tryElement);
    TryAttribs attribs = processAttributes(tryElement.getAttributes());
    if (attribs.predefined) {
      DateTimeFormatter formatter = getFormatter(content);
      for (Locale locale : attribs.locales) {
        String tag = ifNull(attribs.tag, content + " (" + locale + ")");
        ParseAttempt.configure(formatter)
            .withTag(tag)
            .withFilter(attribs.filter)
            .withResolverStyle(attribs.resolverStyle)
            .withLocale(locale)
            .parseInto(parseInto)
            .addTo(attempts);
      }
    } else {
      for (Locale locale : attribs.locales) {
        String tag = ifNull(attribs.tag, content + " (" + locale + ")");
        ParseAttempt.configure(content)
            .withTag(tag)
            .withFilter(attribs.filter)
            .withResolverStyle(attribs.resolverStyle)
            .withLocale(locale)
            .caseSensitive(attribs.caseSensitive)
            .parseInto(parseInto)
            .addTo(attempts);
      }
    }
  }

  private TryAttribs processAttributes(NamedNodeMap attrs) throws FuzzyDateException {
    TryAttribs attribs = new TryAttribs();
    for (int j = 0; j < attrs.getLength(); ++j) {
      Attr attr = (Attr) attrs.item(j);
      String name = attr.getNodeName();
      String val = attr.getNodeValue();
      Check.that(val).isNot(blank(), ERR_BLANK_ATTR, name);
      if ("tag".equals(name)) {
        attribs.tag = val;
      } else if ("predefined".equals(name)) {
        attribs.predefined = Bool.from(val);
      } else if ("resolverStyle".equals(name)) {
        attribs.resolverStyle = getResolverStyle(val);
      } else if ("caseSensitive".equals(name)) {
        attribs.caseSensitive = Bool.from(val);
      } else if ("locales".equals(name)) {
        attribs.locales = getLocales(val);
      } else if ("filter".equals(name)) {
        attribs.filter = getFilter(val);
      } else {
        return Check.fail(FuzzyDateException::new, ERR_BAD_ATTR, name, "try");
      }
    }
    return attribs;
  }

  private static DateTimeFormatter getFormatter(String name) throws FuzzyDateException {
    Field field;
    try {
      field = DateTimeFormatter.class.getDeclaredField(name);
    } catch (NoSuchFieldException exc0) {
      int i = name.lastIndexOf('.');
      try {
        Class<?> clazz = Class.forName(name.substring(0, i));
        field = clazz.getDeclaredField(name.substring(i + 1));
        int m = field.getModifiers();
        if (!isStatic(m) || !isPublic(m)) {
          throw cannotCreateFormatter(name, "not a public static field");
        }
      } catch (Exception exc1) {
        throw cannotCreateFormatter(name, exc1.toString());
      }
    }
    if (!ClassMethods.isA(field.getType(), DateTimeFormatter.class)) {
      throw cannotCreateFormatter(name, "not a DateTimeFormatter instance");
    }
    try {
      return (DateTimeFormatter) field.get(null);
    } catch (IllegalAccessException exc2) {
      throw cannotCreateFormatter(name, exc2.toString());
    }
  }

  private static ResolverStyle getResolverStyle(String s) {
    if (isEmpty(s)) {
      return ResolverStyle.LENIENT;
    }
    return resolverStyleParser.parse(s);
  }

  private static List<Locale> getLocales(String s) throws FuzzyDateException {
    if (isEmpty(s)) {
      return List.of(Locale.getDefault());
    }
    String[] localeStrings = s.split(";");
    List<Locale> locales = new ArrayList<>(localeStrings.length);
    for (String localeString : localeStrings) {
      locales.add(toLocale(localeString));
    }
    return locales;
  }

  private static DateStringFilter getFilter(String className) throws FuzzyDateException {
    if (isEmpty(className)) {
      return null;
    }
    Class<?> clazz = null;
    try {
      clazz = Class.forName(className);
    } catch (ClassNotFoundException e) {
      return Check.fail(FuzzyDateException::new, "Class not found: \"${0}\"", className);
    }
    checkThat(clazz, "filter").is(instanceOf(), DateStringFilter.class);
    try {
      return (DateStringFilter) clazz.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      return Check.fail(FuzzyDateException::new, "Failed to instantiate ${0}", className);
    }
  }
}
