package nl.naturalis.common.sql;

public class TestObjectMapper extends ResultSetMapper<TestObject> {

  TestObjectMapper() {
    super(TestObject::new, TestObject.class);
  }
}
