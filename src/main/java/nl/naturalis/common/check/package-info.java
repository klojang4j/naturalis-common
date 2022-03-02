/**
 *
 *
 * <h1>Precondition and Postcondition Verification</h1>
 *
 * <p>The classes in this package facilitate the validation of method arguments, variables, object
 * state (preconditions) and computational outcomes (postconditions). Contrary to Google Guava's <a
 * href="https://guava.dev/releases/21.0/api/docs/com/google/common/base/Preconditions.html}">Preconditions</a>
 * class and Apache's <a
 * href="https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/Validate.html">Validate</a>
 * class, validation happens through instance methods. For example:
 *
 * <blockquote>
 *
 * <pre>{@code
 * // Verify that the number of chairs is greater than 0, less than
 * // or equal to 4, and that it is an even number of chairs:
 * this.numChairs = Check.that(numChairs).is(gt(), 0).is(lte(), 4).is(even()).ok();
 * }</pre>
 *
 * </blockquote>
 *
 * <h2>Performance</h2>
 *
 * <p>In spite of the checks being carried out on an instance of {@link
 * nl.naturalis.common.check.IntCheck IntCheck} or {@link nl.naturalis.common.check.ObjectCheck
 * ObjectCheck} microbenchmarking their performance using JMH yield no difference with manually
 * coded argument checks. Clearly, the compiler is quite capable of compiling the whose {@code
 * Check} instance away. If the argument fails the test, the {@code check} becomes somewhat slower
 * than a manually coded test because the construction of the exception, notably the error message,
 * takes more time. However, you generally don't to recover from pre- and postcondition failures
 * anyhow. They are end-of-story failures, if not for the application as a whoe, then at least for
 * the rquest being serviced.
 *
 * <h2>Common checks</h2>
 *
 * <p>All checks come in two variants: one where you can provide a custom error message and one
 * where you can't. The latter is mainly meant to be used in combination with the {@link
 * nl.naturalis.common.check.CommonChecks} class. This class is a grab bag of common checks for
 * arguments. They are already associated with short, informative error messages, so you don't have
 * to invent them yourself.
 *
 * <blockquote>
 *
 * <pre>{@code
 * import static nl.naturalis.common.check.CommonChecks.gt;
 * ...
 * ...
 * Check.that(numChairs, "numChairs").is(gt(), 0);
 * // Auto-generated error message: "numChairs must be > 0 (was -3)"
 * }</pre>
 *
 * </blockquote>
 *
 * <h2>Custom error messages</h2>
 *
 * <p>If you prefer to send out a custom error message, you van do so by specifying a {@code
 * String.format} message pattern and zero or more message arguments. Note, however, that the
 * following five message arguments are tacitly prefixed to your own message arguments:
 *
 * <ol>
 *   <li>The name of the check that was executed. E.g. "gt" or "notNull". Within the message pattern
 *       this message argument can be referenced using a standard {@code printf} indexed message
 *       argument: <code>%1$s</code>. You can also use <code>${check}</code> to reference this
 *       message argument. This will get translated into <code>%1$s</code> before the message
 *       pattern is passed off to {@code String.format}.
 *   <li>The argument being validated. Within the message pattern this message argument can be
 *       referenced as <code>${arg}</code> or <code>%2$s</code>.
 *   <li>The simple class name of the argument, or {@code null} if the argument was {@code null}.
 *       Within the message pattern this message argument can be referenced as <code>
 *       ${type}</code> or <code>%3$s</code>.
 *   <li>The name of the argument, or {@code null} if no argument name was provided. Within the
 *       message pattern this message argument can be referenced as <code>${name}</code> or <code>
 *       %4$s</code>.
 *   <li>The object of the relationship in case the check took the form of a {@link
 *       nl.naturalis.common.function.Relation}. For example, for the {@code gt} (greater-than)
 *       check that would be the number that the argument must surpass. Within the message pattern
 *       this message argument can be referenced as <code>
 *       ${obj}</code> or <code>%5$s</code>. For checks expressed through a {@link
 *       java.util.function.Predicate} this message argument will be {@code null}.
 * </ol>
 *
 * <p>In other words, if your custom message only references these elements of the check, you don't
 * need to specify any message arguments yourself at all. The first of your own message arguments
 * can be referenced from within the message pattern as <code>${0}</code> or <code>%6$s
 * </code>, the second as <code>${1}</code> or <code>%7$s</code>, etc.
 *
 * <p>Examples:
 *
 * <blockquote>
 *
 * <pre>{@code
 * // import static nl.naturalis.common.check.CommonChecks.keyIn;
 * Check.that(word).is(keyIn(), dictionary, "Missing key: ${arg}");
 * // Or as pure printf-style format string:
 * Check.that(word).is(keyIn(), dictionary, "Missing key: %2$s");
 *
 * Check.that(word).is(keyIn(), dictionary, "You forgot about ${0}", "your spelling");
 * // Or as pure printf-style format string:
 * Check.that(word).is(keyIn(), dictionary, "You forgot about %6$s", "your spelling");
 * }</pre>
 *
 * </blockquote>
 *
 * <h2>Checking argument properties</h2>
 *
 * <p>>A {@code Check} object lets you validate not just arguments but also argument properties:
 *
 * <blockquote>
 *
 * <pre>{@code
 * Check.notNull(name, "name").has(String::length, "length", gte(), 10);
 * Check.notNull(employee).has(Employee::getAge, "age", lt(), 50);
 * Check.notNull(employees).has(Collection::size, "size", gte(), 100);
 * }</pre>
 *
 * </blockquote>
 *
 * <p>The {@link nl.naturalis.common.check.CommonGetters} is a grab bag of common getters which,
 * again, are already associated with the name of the getter they expose:
 *
 * <blockquote>
 *
 * <pre>{@code
 * // import static nl.naturalis.common.check.CommonChecks.gte;
 * // import static nl.naturalis.common.check.CommonGetters.size;
 * Check.notNull(employees, "employees").has(size(), gte(), 100);
 * // Auto-generated error message: "employees.size() must be >= 100 (was 42)"
 * }</pre>
 *
 * </blockquote>
 *
 * <p>Note that the words "getter" and "property" are suggestive of what is being validated, but
 * also misleading. All that is required is a function that takes the argument and returns the value
 * to be validated. That could be a method reference like {@code Person::getFirstName}, but it could
 * just as well be a function that transforms the argument itself, like the square root of an {@code
 * int} argument or the uppercase version of a {@code String} argument.
 *
 * <h2>Changing the Exception type</h2>
 *
 * <p>By default, an {@code IllegalArgumentException} is thrown if the argument fails to pass a
 * test. This can be customized through the static factory methods. For example:
 *
 * <blockquote>
 *
 * <pre>{@code
 * this.query = Check.on(SQLException::new, query, "query")
 *  .notHas(Query::getFrom, "from", negative())
 *  .has(Query::getLimit, "size", gte(), 10)
 *  .has(Query::getLimit, "size", lte(), 10000)
 *  .ok();
 * }</pre>
 *
 * </blockquote>
 *
 * <p>To save on the number of classes you need to statically import, the {@code CommonChecks} class
 * additionally contains some common exception factories. For example:
 *
 * <blockquote>
 *
 * <pre>{@code
 * // import static nl.naturalis.common.check.CommonChecks.*;
 * Check.on(illegalState(), inputstream).is(open());
 * }</pre>
 *
 * </blockquote>
 */
package nl.naturalis.common.check;
