package nl.naturalis.common;

import java.util.Optional;
import nl.naturalis.common.check.Check;

/** Methods for working with {@code java.util.Optional} objects. */
public class OptionalMethods {

  private OptionalMethods() {}

  /**
   * Returns the contents of the provided {@code Optional}, narrowed to the type of the receiving
   * variable, or null if the {@code Optional} is empty.
   *
   * <pre>
   * Optional&lt;CharSequence&gt; opt = Optional.of("Hello");
   * //String s1 = opt.get(); *** Does not compile ***
   * String s2 = OptionalMethods.contentsOf(opt);
   * </pre>
   *
   * @param <T> The type of the object within the {@code Optional} (a superclass or interface)
   * @param <U> The return type (a subclass or implementation)
   * @param optional The {@code Optional} whose contents is to be retrieved
   * @return The narrowed contents of the {@code Optional}
   */
  @SuppressWarnings("unchecked")
  public static <T, U extends T> U contentsOf(Optional<T> optional) {
    return (U) Check.notNull(optional).ok().orElse(null);
  }

  /**
   * Narrows the contents of the provided {@code Optional} to an instance of {@code U} and returns
   * it within a new {@code Optional}. Only use this method if you know that what's inside the
   * {@code Optional} is an instance of {@code T} or you risk getting a {@link ClassCastException}
   * at runtime.
   *
   * <pre>
   * // OK
   * Optional&lt;Object&gt; opt1 = Optional.of("Hello");
   * Optional&lt;String&gt; opt2 = Optionals.narrow(opt1);
   *
   * // Bad
   * Optional&lt;Object&gt; opt1 = Optional.of(Integer.MAX_VALUE);
   * Optional&lt;String&gt; opt2 = OptionalMethods.narrow(opt1); // Compiles. String is a subclass of Object
   * opt2.get().charAt(0); // ClassCastException! Integer is not a subclass of String
   * </pre>
   *
   * @param <T> The type of the object in the provided {@code Optional} (a superclass or interface)
   * @param <U> The type of the object in the returned {@code Optional} (a subclass or
   *     implementation)
   * @param optional An {@code Optional} whose contents is to be narrowed
   * @return A new {@code Optional} with narrow-casted contents or the same {@code Optional} if it
   *     was empty.
   */
  @SuppressWarnings("unchecked")
  public static <T, U extends T> Optional<U> narrow(Optional<T> optional) {
    Check.notNull(optional);
    return (Optional<U>) (optional.isEmpty() ? optional : Optional.of((U) optional.get()));
  }

  /**
   * Widens the type of the provided optional. This method simply returns the specified {@code
   * Optional}, but circumvents the compilation error caused by brute force casting:
   *
   * <pre>
   * Optional&lt;String&gt; opt1 = Optional.of("Hello");
   * // Does not compile:
   * // Optional&lt;Object&gt; opt2 = (Optional&lt;Object&gt;) opt1;
   * Optional&lt;Object&gt; opt2 = OptionalMethods.widen(opt1);
   * </pre>
   *
   * @param <T> The type of the object in the returned {@code Optional}
   * @param <U> The type of the object in the specified {@code Optional}
   * @param optional The {@code Optional} to be widened
   * @return An {@code Optional} with a widened type
   */
  @SuppressWarnings("unchecked")
  public static <T, U extends T> Optional<T> widen(Optional<U> optional) {
    return (Optional<T>) Check.notNull(optional).ok();
  }
}
