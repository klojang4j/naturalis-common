package nl.naturalis.common.time;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.UnaryOperator;
import nl.naturalis.common.ClassMethods;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.common.check.Check;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;

import static nl.naturalis.common.CollectionMethods.newLinkedHashMap;
import static nl.naturalis.common.StringMethods.ifBlank;

/**
 * An extension of {@link Properties} dedicated to reading configurations for the {@link
 * FuzzyDateParser}.
 *
 * @author Ayco Holleman
 */
class ParseSpecProperties extends Properties {

  /**
   * A map of all supported {@link Temporal} classes, mapped to the {@link TemporalQuery} used to
   * instantiate them.
   */
  private static final Map<String, TemporalQuery<?>[]> supportedDateTypes =
      newLinkedHashMap(
          Instant.class.getSimpleName(), new TemporalQuery[] {Instant::from},
          OffsetDateTime.class.getSimpleName(), new TemporalQuery[] {OffsetDateTime::from},
          LocalDateTime.class.getSimpleName(), new TemporalQuery[] {LocalDateTime::from},
          LocalDate.class.getSimpleName(), new TemporalQuery[] {LocalDate::from},
          YearMonth.class.getSimpleName(), new TemporalQuery[] {YearMonth::from},
          Year.class.getSimpleName(), new TemporalQuery[] {Year::from});

  private final String globalResolverStyle;
  private final String globalCaseSensitive;
  private final String globalFilter;

  ParseSpecProperties(InputStream is) {
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
    for (String dateType : supportedDateTypes.keySet()) {
      parseSpecs.addAll(createParseSpecs(dateType));
    }
    return parseSpecs;
  }

  private List<ParseInfo> createParseSpecs(String dateType) throws FuzzyDateException {
    List<ParseInfo> parseSpecs = new ArrayList<>();
    for (int i = 0; ; i++) {
      String key = dateType + "." + i + ".name";
      String val = getProperty(key);
      if (val != null) {
        UnaryOperator<String> filter = getFilter(dateType, i);
        DateTimeFormatter formatter = getNamedFormatter(val);
        TemporalQuery<?>[] parseInto = getParseInto(dateType);
        parseSpecs.add(new ParseInfo(filter, formatter, parseInto));
      } else {
        key = dateType + "." + i + ".pattern";
        val = getProperty(key);
        if (val == null) {
          break;
        }
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        builder.appendPattern(val);
        if (!getCaseSensitive(dateType, i)) {
          builder.parseCaseInsensitive();
        }
        ResolverStyle resolverStyle = getResolverStyle(dateType, i);
        if (resolverStyle == ResolverStyle.LENIENT) {
          builder.parseLenient();
        }
        UnaryOperator<String> filter = getFilter(dateType, i);
        DateTimeFormatter formatter = builder.toFormatter();
        TemporalQuery<?>[] parseInto = getParseInto(dateType);
        parseSpecs.add(new ParseInfo(filter, formatter, parseInto));
      }
    }
    return parseSpecs;
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

  private static TemporalQuery<?>[] getParseInto(String dateType) throws FuzzyDateException {
    TemporalQuery<?>[] parseInto = supportedDateTypes.get(dateType);
    Check.that(
        parseInto != null,
        () -> new FuzzyDateException("Non-existent or unsupported date/time class: " + dateType));
    return parseInto;
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
        Check.that(
            isStatic(m) && isPublic(m),
            () -> cannotCreateFormatter(name, "not a public static field"));
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
