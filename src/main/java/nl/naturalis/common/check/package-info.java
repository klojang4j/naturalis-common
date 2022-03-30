/**
 * <h1>Precondition and Postcondition Verification</h1>
 *
 * <p>The classes in this package facilitate the validation of method arguments, variables, object
 * state (preconditions) and computational outcomes (postconditions). Contrary to Google Guava's <a
 * href="https://guava.dev/releases/21.0/api/docs/com/google/common/base/Preconditions.html}
 * ">Preconditions</a> class and Apache's
 * <a href="https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/Validate.html">Validate</a>
 * class, validation happens through instance methods rather than class methods. For example:
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
 * <p>In spite of the checks being carried out on an actual instance of {@link
 * nl.naturalis.common.check.IntCheck IntCheck} or {@link nl.naturalis.common.check.ObjectCheck
 * ObjectCheck}, benchmarking their performance yield no difference with hand-coded argument checks.
 * If the argument passes the test, there is literally no difference outside the error margin of the
 * benchmark. If the argument fails the test, it depends on which type of check you choose. If you
 * choose the {@link nl.naturalis.common.check.ObjectCheck#is(java.util.function.Predicate,
 * java.util.function.Supplier) variant} where you provide your own exception, there is again no
 * difference with a hand-coded check. Otherwise, if a hand-coded check would allow you use a
 * "hard-coded" exception message (i.e. without resorting to string concatenation or {@code
 * String.format}), it performs somewhat better, because the check framework will always have to
 * assemble the exception message. Note, however, that you don't generally want to recover from pre-
 * and postcondition failures anyhow. They tend to be end-of-story failures, if not for the
 * application as a whole, then at least for the request being serviced.
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
 * <p>If you prefer to send out a custom error message, you van do so by specifying a message
 * pattern and zero or more message arguments. The first message argument can be referenced as
 * <code>${0}</code>; the second as <code>${1}</code>, etc. For example:
 *
 * <blockquote>
 *
 * <pre>{@code
 * // import static nl.naturalis.common.check.CommonChecks.keyIn;
 * Check.that(word).is(keyIn(), dictionary, "Spelling error. Did you mean: ${0}?", "colleague");
 * // Error message: "Spelling error. Did you mean: colleague?"
 * }</pre>
 *
 * </blockquote>
 *
 * <p>The following message arguments are automatically available within the message pattern:
 *
 * <ol>
 *   <li><b>${test}</b> The name of the check that was executed. E.g. "gt" or "notNull".
 *   <li><b>${arg}</b> The argument being validated.
 *   <li><b>${type}</b> The simple class name of the argument.
 *   <li><b>${name}</b> The name of the argument if you provided one through the static factory
 *   methods of the {@code Check} class. Otherwise it will also be the simple class name of the
 *   argument.
 *   <li>${obj} The object of the relationship in case the check took the form of a {@link
 *       nl.naturalis.common.function.Relation}. For example, for the
 *       {@link nl.naturalis.common.check.CommonChecks#instanceOf()} () instanceOf}
 *       check that would be the class that the argument must be an instance of. For checks
 *       expressed through a {@code Predicate} or {@code IntPredicate} ${obj} will
 *       be {@code null}.
 * </ol>
 *
 * <p>For example:
 *
 * <blockquote>
 *
 * <pre>{@code
 * // import static nl.naturalis.common.check.CommonChecks.keyIn;
 * Check.that(word).is(keyIn(), dictionary, "Missing key: ${arg}");
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
