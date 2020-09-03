package nl.naturalis.common;

import java.util.Optional;

/** Methods for working with {@code java.util.Optional} objects. */
public class OptionalMethods {

  private OptionalMethods() {}

  /**
   * Returns the object within the provided {@code Optional}, narrow-casted to the type of the
   * receiving variable.
   *
   * <pre>
   * Optional&lt;CharSequence&gt; opt = Optional.of("Hello");
   * String s1 = opt.get(); // Does not compile!
   * String s2 = OptionalMethods.get(opt);
   * </pre>
   *
   * @param <T> The type of the object within the {@code Optional} (a superclass or interface)
   * @param <U> The return type (a subclass or implementation)
   * @param optional The {@code Optional} whose contents is to be retrieved
   * @return The narrowed contents of the {@code Optional}
   */
  @SuppressWarnings("unchecked")
  public static <T, U extends T> U get(Optional<T> optional) {
    return (U) Check.notNull(optional, "optional").get();
  }

  /**
   * Narrows the contents of the provided {@code Optional} to an instance of {@code U} and returns
   * it within a new {@code Optional}. If the provided {@code Optional} is empty, the {@code
   * Optional} itself is returned. Only use this method if you <i>know</i> that what's inside the
   * provided {@code Optional} is a subclass or implementation of {@code T} or you risk getting a
   * {@link ClassCastException} at runtime.
   *
   * <pre>
   * // OK
   * Optional&lt;Object&gt; opt1 = Optional.of("Hello");
   * Optional&lt;String&gt; opt2 = Optionals.narrow(opt1);
   *
   * // EVIL
   * Optional&lt;Object&gt; opt1 = Optional.of(Integer.MAX_VALUE);
   * Optional&lt;String&gt; opt2 = OptionalMethods.narrow(opt1); // Compiles! String is a subclass of Object
   * opt2.get().charAt(0); // ClassCastException! Integer is not a subclass of String
   * </pre>
   *
   * @param <T> The type of the object in the provided {@code Optional} (a superclass or interface)
   * @param <U> The type of the object in the returned {@code Optional} (a subclass or
   *     implementation)
   * @param optional An {@code Optional} whose contents is to be narrowed
   * @return An {@code Optional} whose contents is narrowed
   */
  @SuppressWarnings("unchecked")
  public static <T, U extends T> Optional<U> narrow(Optional<T> optional) {
    Check.notNull(optional, "optional");
    return (Optional<U>) (optional.isEmpty() ? optional : Optional.of(optional.get()));
  }

  /**
   * Widens the type of the provided optional. This method always returns the provided {@code
   * Optional} itself.
   *
   * <pre>
   * Optional&lt;String&gt; opt1 = Optional.of("Hello");
   * Optional&lt;Object&gt; opt2 = (Optional&lt;Object&gt;) opt1; // Does not compile!
   * Optional&lt;Object&gt; opt2 = OptionalMethods.widen(opt1);
   * </pre>
   *
   * @param <T> The type of the object in the returned {@code Optional} (a superclass or interface)
   * @param <U> The type of the object in the provided {@code Optional} (a subclass or
   *     implementation)
   * @param optional An {@code Optional} whose contents is to be widened
   * @return An {@code Optional} whose contents is widened
   */
  @SuppressWarnings("unchecked")
  public static <T, U extends T> Optional<T> widen(Optional<U> optional) {
    return (Optional<T>) Check.notNull(optional, "optional");
  }
}
