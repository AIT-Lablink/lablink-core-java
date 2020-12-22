//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.encodables;

import static junit.framework.TestCase.assertEquals;

import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base Tests for every payloads object.
 */
public class HeaderBaseTest {

  @Test(expected = LlCoreRuntimeException.class)
  public void validate_AppIdIsNull_shouldThrow_test() throws Exception {
    Header cut = new HeaderTestImpl(null, "group1", "client1", Arrays.asList("Sub1", "Sub2"), 12);
    cut.validate();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void validate_AppIdIsEmpty_shouldThrow_test() throws Exception {
    Header cut = new HeaderTestImpl("", "group1", "client1", Arrays.asList("Sub1", "Sub2"), 12);
    cut.validate();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void validate_GroupIdIsNull_shouldThrow_test() throws Exception {
    Header cut = new HeaderTestImpl("App", null, "client1", Arrays.asList("Sub1", "Sub2"), 12);
    cut.validate();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void validate_GroupIdIsEmpty_shouldThrow_test() throws Exception {
    Header cut = new HeaderTestImpl("App", "", "client1", Arrays.asList("Sub1", "Sub2"), 12);
    cut.validate();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void validate_ClientIdIsNull_shouldThrow_test() throws Exception {
    Header cut = new HeaderTestImpl("App", "group1", null, Arrays.asList("Sub1", "Sub2"), 12);
    cut.validate();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void validate_ClientIdIsEmpty_shouldThrow_test() throws Exception {
    Header cut = new HeaderTestImpl("App", "group1", "", Arrays.asList("Sub1", "Sub2"), 12);
    cut.validate();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void validate_SubjectIsNull_shouldThrow_test() throws Exception {
    Header cut = new HeaderTestImpl("App", "group1", "client1", null, 12);
    cut.validate();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void validate_SubjectIsEmpty_shouldThrow_test() throws Exception {
    Header cut = new HeaderTestImpl("App", "group1", "client1", new ArrayList<String>(), 12);
    cut.validate();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void validate_SubjectElementIsNull_shouldThrow_test() throws Exception {
    Header cut = new HeaderTestImpl("App", "group1", "client1", Arrays.asList("Sub1", null), 12);
    cut.validate();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void validate_SubjectElementIsEmpty_shouldThrow_test() throws Exception {
    Header cut = new HeaderTestImpl("App", "group1", "client1", Arrays.asList("Sub1", ""), 12);
    cut.validate();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void validate_TimeStampIsNull_shouldThrow_test() throws Exception {
    Header cut = new HeaderTestImpl("App", "group1", "client1", Arrays.asList("Sub1", "Sub2"), 0);
    cut.validate();
  }

  @Test
  public void getApplicationId_test() throws Exception {
    Header cut = new HeaderTestImpl("App", "group1", "client1", Arrays.asList("Sub1", "Sub2"), 12);
    assertEquals("App", cut.getApplicationId());
  }

  @Test
  public void getGroupId_test() throws Exception {
    Header cut = new HeaderTestImpl("App", "group1", "client1", Arrays.asList("Sub1", "Sub2"), 12);
    assertEquals("group1", cut.getSourceGroupId());
  }

  @Test
  public void getClientId_test() throws Exception {
    Header cut = new HeaderTestImpl("App", "group1", "client1", Arrays.asList("Sub1", "Sub2"), 12);
    assertEquals("client1", cut.getSourceClientId());
  }

  @Test
  public void getSubject_test() throws Exception {
    Header cut = new HeaderTestImpl("App", "group1", "client1", Arrays.asList("Sub1", "Sub2"), 12);
    assertEquals(Arrays.asList("Sub1", "Sub2"), cut.getSubject());
  }

  @Test
  public void getTimeStamp_test() throws Exception {
    Header cut = new HeaderTestImpl("App", "group1", "client1", Arrays.asList("Sub1", "Sub2"), 12);
    assertEquals(12, cut.getTimestamp());
  }

  class HeaderTestImpl extends Header {


    public HeaderTestImpl(String applicationId, String sourceGroupId, String sourceClientId,
                          List<String> subject, long timestamp) {
      super(applicationId, sourceGroupId, sourceClientId, subject, timestamp);
    }

    @Override
    public String getType() {
      return "test-header";
    }
  }
}