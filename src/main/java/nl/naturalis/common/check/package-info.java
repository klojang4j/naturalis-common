/**
 * <h1>Precondition and Postcondition Verification</h1>
 *
 * <p>The classes in this package facilitate the validation of method arguments, variables, object
 * state (preconditions) and computational outcomes (postconditions). Contrary to Google Guava's
 * <a href="https://guava.dev/releases/21.0/api/docs/com/google/common/base/Preconditions.html">Preconditions</a>
 * class and Apache's
 * <a href="https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/Validate.html">Validate</a>
 * class, validation happens through instance methods rather than class methods. For example:
 *
 * <blockquote>
 * <pre>{@code
 * // Verify that the number of chairs is greater than 0, less
 * // than or equal to 4, and that it is an even number of chairs:
 * this.numChairs = Check.that(numChairs).is(positive()).is(lte(), 4).is(even()).ok();
 * }</pre>
 * </blockquote>
 *
 *
 * <h2>Performance</h2>
 *
 * <p>In spite of the checks being carried out on an actual instance of {@link
 * nl.naturalis.common.check.IntCheck IntCheck} or {@link nl.naturalis.common.check.ObjectCheck
 * ObjectCheck}, benchmarking their performance yield no difference with hand-coded argument checks.
 * If the argument passes the test, there is literally no difference outside the error margin of the
 * benchmark. If the argument fails the test, and an exception needs to be thrown, the check
 * framework also performs equally well, except when you provide your own error message (see below).
 * In this case the message needs to be scanned for message arguments, which is relatively
 * expensive. Note, though, that in most cases you would not expect your precondition checks to be
 * violated often enough to make this is issue. Nevertheless, if you find yourself in this situation
 * and performance of the utmost importance, you can achieve it by specifying <i>exactly</i> one
 * message argument: {@code '\0'} (the NULL character). This will cause the message not to be
 * parsed, and simply be passed as-is to the exception. (Of course, the message cannot contain any
 * message arguments then.)
 *
 *
 * <p>You can view the results of the JMH benchmarks
 * <a href="https://github.com/klojang4j/naturalis-common-jmh">here</a>.
 *
 *
 * <h2>Common checks</h2>
 *
 * <p>All checks come in two variants: one where you can provide a custom error message and one
 * where you can't. The latter is mainly meant to be used in combination with the {@link
 * nl.naturalis.common.check.CommonChecks} class. This class is a grab bag of common checks for
 * arguments. They are already associated with short, informative error messages, so you don't have
 * to invent them yourself.
 * <blockquote>
 * <pre>{@code
 * import static nl.naturalis.common.check.CommonChecks.gt;
 * ...
 * ...
 * Check.that(numChairs, "numChairs").is(gt(), 0);
 * // Auto-generated error message: "numChairs must be > 0 (was -3)"
 * }</pre>
 * </blockquote>
 *
 * <h2>Custom error messages</h2>
 * <p>If you prefer to send out a custom error message, you can do so by specifying a message
 * pattern and zero or more message arguments. The first message argument can be referenced as
 * <code>${0}</code>; the second as <code>${1}</code>, etc. For example:
 * <blockquote>
 * <pre>{@code
 * // import static nl.naturalis.common.check.CommonChecks.keyIn;
 * Check.that(word).is(keyIn(), dictionary, "Spelling error. Did you mean: ${0}?", "colleague");
 * // Error message: "Spelling error. Did you mean: colleague?"
 * }</pre>
 * </blockquote>
 * <p>The following message arguments are automatically available within the message pattern:
 * <ol>
 *   <li><b><code>${test}</code></b> The name of the check that was executed. E.g. "gt" or
 *   "notNull".
 *   <li><b><code>${arg}</code></b> The argument being validated.
 *   <li><b><code>${type}</code></b> The simple class name of the argument.
 *   <li><b><code>${name}</code></b> The name of the argument, if you provided one through the
 *   static factory methods of the {@code Check} class. Otherwise it will also be the simple
 *   class name of the argument.
 *   <li><b><code>${obj}</code></b> The object of the relationship, in case the check took the
 *   form of a {@link nl.naturalis.common.function.Relation Relation} or one of its sister
 *   interfaces. For example, for the {@link nl.naturalis.common.check.CommonChecks#instanceOf()
 *   instanceOf} check <code>${obj}</code> would be the class that the argument must be an
 *   instance of. For checks expressed through a {@code Predicate} or {@code IntPredicate} ${obj}
 *   will be {@code null}.
 * </ol>
 * <p>For example:
 * <blockquote>
 * <pre>{@code
 * Check.that(word).is(keyIn(), dictionary, "Missing key: ${arg}");
 * }</pre>
 * </blockquote>
 *
 * <h2>Checking argument properties</h2>
 * <p>Besides validating arguments, you can also validate argument <i>properties</i>:
 * <blockquote>
 * <pre>{@code
 * Check.notNull(name, "name").has(String::length, "length", gte(), 10);
 * Check.notNull(employee).has(Employee::getAge, "age", lt(), 50);
 * Check.notNull(employees).has(Collection::size, "size", gte(), 100);
 * }</pre>
 * </blockquote>
 * <p>The {@link nl.naturalis.common.check.CommonGetters} class is a grab bag of common getters
 * which, again, are already associated with the name of the getter they expose:
 * <blockquote>
 * <pre>{@code
 * import static nl.naturalis.common.check.CommonChecks.gte;
 * import static nl.naturalis.common.check.CommonGetters.size;
 * // ...
 * Check.notNull(employees, "employees").has(size(), gte(), 100);
 * // Auto-generated error message: "employees.size() must be >= 100 (was 42)"
 * }</pre>
 * </blockquote>
 * <p>Note that the words "getter" and "property" are suggestive of what is being validated, but
 * also misleading. All that is required is a function that takes the argument and returns the value
 * to be validated. That could be a method reference like {@code Person::getFirstName}, but it could
 * just as well be a function that transforms the argument itself, like the square root of an {@code
 * int} argument or the uppercase version of a {@code String} argument.
 *
 * <h2>Changing the Exception type</h2>
 * <p>By default, an {@code IllegalArgumentException} is thrown if the argument fails to pass a
 * test. This can be customized through the static factory methods. For example:
 * <blockquote>
 * <pre>{@code
 * this.query = Check.on(SQLException::new, query, "query")
 *  .notHas(Query::getFrom, "from", negative())
 *  .has(Query::getLimit, "size", gte(), 10)
 *  .has(Query::getLimit, "size", lte(), 10000)
 *  .ok();
 * }</pre>
 * </blockquote>
 * <p>To save on the number of classes you need to statically import, the {@code CommonChecks} class
 * additionally contains some common exception factories. For example:
 * <blockquote>
 * <pre>{@code
 * import static nl.naturalis.common.check.CommonChecks.*;
 * // ...
 * Check.on(illegalState(), inputstream).is(open());
 * }</pre>
 * </blockquote>
 */
package nl.naturalis.common.check;
