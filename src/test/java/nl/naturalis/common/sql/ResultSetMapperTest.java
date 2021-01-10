package nl.naturalis.common.sql;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ResultSetMapperTest {

  @Test
  public void test00() {
    MockResultSet rs =
        new MockResultSet() {
          @Override
          public String getString(String columnLabel) {
            return "Hello World!";
          }

          @Override
          public int getInt(String columnLabel) {
            return 42;
          }
        };
    TestObjectMapper mapper = new TestObjectMapper();
    TestObject bean = mapper.read(rs);
    assertEquals("Hello World!", bean.getMyString());
    assertEquals(42, bean.getMyInt());
  }
}
