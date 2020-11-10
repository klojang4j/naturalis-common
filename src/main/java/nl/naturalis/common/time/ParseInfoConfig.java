package nl.naturalis.common.time;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.*;
import java.util.function.UnaryOperator;
import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static nl.naturalis.common.StringMethods.ifBlank;
import static nl.naturalis.common.check.CommonChecks.keyIn;

/**
 * An extension of {@link Properties} dedicated to reading configurations for the {@link
 * FuzzyDateParser}.
 *
 * @author Ayco Holleman
 */
class ParseInfoConfig extends Properties {

  /**
   * Maps the supported {@link Temporal} classes to the {@link TemporalQuery} used to instantiate
   * them.
   */
  private static final Map<String, List<TemporalQuery<TemporalAccessor>>> supported =
      new LinkedHashMap<>();

  static {
    supported.put(Instant.class.getSimpleName(), List.of(Instant::from));
    supported.put(OffsetDateTime.class.getSimpleName(), List.of(Instant::from));
    supported.put(LocalDateTime.class.getSimpleName(), List.of(LocalDateTime::from));
    supported.put(LocalDate.class.getSimpleName(), List.of(LocalDate::from));
    supported.put(YearMonth.class.getSimpleName(), List.of(YearMonth::from));
    supported.put(Year.class.getSimpleName(), List.of(Year::from));
  }

  private final String globalResolverStyle;
  private final String globalCaseSensitive;
  private final String globalFilter;

  ParseInfoConfig(InputStream is) {
    try {
      load(is);
    } catch (IOException e) {
      throw ExceptionMethods.uncheck(e);
    }
    globalResolverStyle = ifBlank(getProperty("resolverStyle"), "STRICT");
    globalCaseSensitive = ifBlank(getProperty("caseSensitive"), "true");
    globalFilter = ifBlank(getProperty("filter"), null);
  }

  /**
   * Produces a list of {@code ParseSpec} instances from this {@code Properties instance}.
   *
   * @return
   * @throws FuzzyDateException
   */
  List<ParseInfo> createParseInfos() throws FuzzyDateException {
    List<ParseInfo> parseSpecs = new ArrayList<>();
    for (String dateType : supported.keySet()) {
      parseSpecs.addAll(createParseInfos(dateType));
    }
    return parseSpecs;
  }

  private List<ParseInfo> createParseInfos(String dateTimeClass) throws FuzzyDateException {
    List<ParseInfo> infos = new ArrayList<>();
    for (int i = 0; ; i++) {
      String key = dateTimeClass + "." + i + ".name";
      String val = getProperty(key);
      if (val != null) {
        UnaryOperator<String> filter = getFilter(dateTimeClass, i);
        DateTimeFormatter formatter = getNamedFormatter(val);
        List<TemporalQuery<TemporalAccessor>> parseInto = getParseInto(dateTimeClass);
        infos.add(new ParseInfo(formatter, parseInto, filter));
      } else {
        key = dateTimeClass + "." + i + ".pattern";
        val = getProperty(key);
        if (val == null) {
          break;
        }
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        builder.appendPattern(val);
        if (!getCaseSensitive(dateTimeClass, i)) {
          builder.parseCaseInsensitive();
        }
        ResolverStyle resolverStyle = getResolverStyle(dateTimeClass, i);
        if (resolverStyle == ResolverStyle.LENIENT) {
          builder.parseLenient();
        }
        UnaryOperator<String> filter = getFilter(dateTimeClass, i);
        DateTimeFormatter formatter = builder.toFormatter();
        List<TemporalQuery<TemporalAccessor>> parseInto = getParseInto(dateTimeClass);
        infos.add(new ParseInfo(formatter, parseInto, filter));
      }
    }
    return infos;
  }

  private ResolverStyle getResolverStyle(String dateType, int i) throws FuzzyDateException {
    String key = dateType + "." + i + ".resolverStyle";
    String val = ifBlank(getProperty(key), globalResolverStyle);
    switch (val.toUpperCase()) {
      case "SMART":
        return ResolverStyle.SMART;
      case "STRICT":
        return ResolverStyle.STRICT;
      case "LENIENT":
        return ResolverStyle.LENIENT;
      default:
        throw new FuzzyDateException("Invalid resolver style: " + val);
    }
  }

  private boolean getCaseSensitive(String dateType, int i) {
    String key = dateType + "." + i + ".caseSensitive";
    String val = ifBlank(getProperty(key), globalCaseSensitive);
    return val.toLowerCase().equals("true");
  }

  @SuppressWarnings("unchecked")
  private UnaryOperator<String> getFilter(String dateType, int i) throws FuzzyDateException {
    String key = dateType + "." + i + ".filter";
    String val = ifBlank(getProperty(key), globalFilter);
    if (val == null) {
      return null;
    }
    try {
      return (UnaryOperator<String>) Class.forName(val).getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new FuzzyDateException(String.format("Could not create filter for \"%s\": %s", val, e));
    }
  }

  private static List<TemporalQuery<TemporalAccessor>> getParseInto(String className)
      throws FuzzyDateException {
    return Check.that(className, FuzzyDateException::new)
        .is(keyIn(), supported, "Unknown or unsupported date/time class: %s", className)
        .ok(supported::get);
  }

  /*
   * Creates an instance of DateTimeFormatter using the provided name. The name must be either the
   * simple name of a public static DateTimeFormatter field on the DateTimeFormatter class (like
   * "BASIC_ISO_DATE"), or the fully-qualified name of a public static DateTimeFormatter field on an
   * arbitrary class.
   */
  private static DateTimeFormatter getNamedFormatter(String name) throws FuzzyDateException {
    Field field;
    try {
      field = DateTimeFormatter.class.getDeclaredField(name);
    } catch (NoSuchFieldException e0) {
      int i = name.lastIndexOf('.');
      Check.that(
          i > 0 && name.length() > 3 && !name.endsWith("."), () -> cannotCreateFormatter(name));
      try {
        Class<?> clazz = Class.forName(name.substring(0, i));
        field = clazz.getDeclaredField(name.substring(i + 1));
        int m = field.getModifiers();
        if (!isStatic(m) || !isPublic(m)) {
          throw cannotCreateFormatter(name, "not a public static field");
        }
      } catch (Exception e1) {
        throw cannotCreateFormatter(name, e1.toString());
      }
    }
    if (!ClassMethods.isA(field.getType(), DateTimeFormatter.class)) {
      throw cannotCreateFormatter(name, "not a DateTimeFormatter instance");
    }
    try {
      return (DateTimeFormatter) field.get(null);
    } catch (IllegalAccessException e2) {
      throw cannotCreateFormatter(name, e2.toString());
    }
  }

  private static FuzzyDateException cannotCreateFormatter(String name) {
    return new FuzzyDateException(
        String.format("Cannot create DateTimeFormatter for \"%s\"", name));
  }

  private static FuzzyDateException cannotCreateFormatter(String name, String reason) {
    return new FuzzyDateException(
        String.format("Cannot create DateTimeFormatter for \"%s\": %s", name, reason));
  }
}
