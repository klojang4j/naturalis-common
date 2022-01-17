package nl.naturalis.common.check;

import static nl.naturalis.common.check.CommonChecks.greaterThan;
import static nl.naturalis.common.check.CommonChecks.atLeast;
import static nl.naturalis.common.check.CommonChecks.containing;
import static nl.naturalis.common.check.CommonChecks.eq;
import static nl.naturalis.common.check.CommonChecks.gte;
import static nl.naturalis.common.check.CommonChecks.in;
import static nl.naturalis.common.check.CommonChecks.instanceOf;
import static nl.naturalis.common.check.CommonGetters.size;
import static nl.naturalis.common.check.Messages.createMessage;
import static nl.naturalis.common.check.Messages.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import nl.naturalis.common.ClassMethods;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class MessagesTest {

  @Test
  public void gte01() {
    MessageData md = new MessageData(gte(), false, "foo", 2, 5);
    System.out.println(msgAtLeast().apply(md));
    assertEquals("foo must be >= 5 (was 2)", msgAtLeast().apply(md));
  }
}
