package nl.naturalis.common.collection;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import nl.naturalis.common.check.Check;
import static nl.naturalis.common.ClassMethods.*;
import static nl.naturalis.common.check.CommonChecks.neverNull;

/**
 * A {@link TreeSet} for {@code Class} objects that applies a very strict ordering of the elements
 * in the set.
 *
 * @author Ayco Holleman
 */
public class TypeSet extends TreeSet<Class<?>> {

  private static Comparator<Class<?>> cmp =
      (c1, c2) -> {
        int i = Integer.MIN_VALUE;
        int j = Integer.MAX_VALUE;
        Class<?> c3;
        if (c1 == c2) {
          return 0;
        } else if (c1 == Object.class) {
          return j--;
        } else if (c2 == Object.class) {
          return i++;
        } else if (!c1.isInterface() && c2.isInterface()) {
          return i++;
        } else if (c1.isInterface() && !c2.isInterface()) {
          return j--;
        } else if ((c3 = c1.getSuperclass()) != null && c3 == c2) {
          return i++;
        } else if ((c3 = c2.getSuperclass()) != null && c3 == c1) {
          return j--;
        } else if (isDescendant(c1, c2)) {
          return i++;
        } else if (isDescendant(c2, c1)) {
          return j--;
        } else if (implementsDirectly(c1, c2)) {
          return i++;
        } else if (implementsDirectly(c2, c1)) {
          return j--;
        } else if (implementsIndirectly(c1, c2)) {
          return i++;
        } else if (implementsIndirectly(c2, c1)) {
          return j--;
        } else if (implementsByDefault(c1, c2)) {
          return i++;
        } else if (implementsByDefault(c2, c1)) {
          return j--;
        } else if (indirectlyImplementsByDefault(c1, c2)) {
          return i++;
        } else if (indirectlyImplementsByDefault(c2, c1)) {
          return j--;
        } else if (isSubInterface(c1, c2)) {
          return i++;
        } else if (isSubInterface(c2, c1)) {
          return j--;
        } else if (isDescendantInterface(c1, c2)) {
          return i++;
        } else if (isDescendantInterface(c2, c1)) {
          return j--;
        }
        return c1.hashCode() - c2.hashCode();
      };

  public TypeSet() {
    super(cmp);
  }

  public void addTypes(Class<?>... types) {
    Check.notNull(types);
    for (Class<?> t : types) {
      add(t);
    }
  }

  @Override
  public boolean add(Class<?> e) {
    return super.add(Check.notNull(e).ok());
  }

  @Override
  public boolean addAll(Collection<? extends Class<?>> c) {
    Check.that(c).is(neverNull());
    return super.addAll(c);
  }
}
