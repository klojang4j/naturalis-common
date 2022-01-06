package nl.naturalis.common.time;

import java.io.InputStream;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

/**
 * A {@code ParseDefaults} instance can be passed to the
 * {@link FuzzyDateParser#FuzzyDateParser(InputStream, ParseDefaults) constructors} and
 * {@link FuzzyDateParser#getDefaultParser(ParseDefaults) methods} of {@link FuzzyDateParser} that create
 * {@code ParseAttempt} instances from an XML configuration file. The {@code ParseDefaults} record
 * corresponds to the &lt;ParseDefaults&gt; element within the configuration file. Any <b>non-null</b> field within
 * the {@code ParseDefaults} instance will override the corresponding default within the
 * &lt;ParseDefaults&gt; element. The {@code ParseDefaults} record is especially meant for programmatically choosing
 * or switching between locales, but you can override any of the defaults in the &lt;ParseDefaults&gt; element.
 *
 * <p>Note that you may not have many date/time patterns in the XML configuration file that are actually sensitive
 * to the {@code Locale} being used (e.g. those specifying a spelled-out week day or month), so you might not want
 * to specify a default {@code Locale} at all, and only specify them for the &lt;try&gt; elements that <i>do</i>
 * contain a locale-sensitive pattern. Note though that when specified at that level, the {@code ParseDefaults}
 * instance will <i>not</i> override them.
 */
public record ParseDefaults(Boolean caseSensitive, ResolverStyle resolverStyle, List<Locale> locales, DateStringFilter filter) {

    /**
     * Creates an empty {@code ParseDefaults} instance. In other words, one that would leave the defaults specified in
     * the &lt;ParseDefaults&gt; element intact.
     */
    public ParseDefaults() { this(null, null, null, null); }

    /**
     * Creates an empty {@code ParseDefaults} instance that will cause the {@link FuzzyDateParser} to default to the
     * specified locale, rather than the default locale(s) specified in the XML configuration file.
     * @param locale The default locale
     */
    public ParseDefaults(Locale locale) {
        this(List.of(locale));
    }

    /**
     * Creates an empty {@code ParseDefaults} instance that will cause the {@link FuzzyDateParser} to default to the
     * specified locales (trying all of them), rather than the default locale(s) specified in the XML configuration file.
     * @param locales The default locales
     */
    public ParseDefaults(List<Locale> locales) {
        this(null, null, locales, null);
    }
}
