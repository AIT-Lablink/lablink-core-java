//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.topic;

import static org.junit.Assert.assertEquals;

import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Unit test for class RpcSubject.
 */
public class RpcSubjectTest {

  @Test
  public void createSubject_Correct_test() throws Exception {
    RpcSubject
        actual =
        RpcSubject.getBuilder().addSubjectElement("measurement").addSubjectElement("voltage")
            .addSubjectElement("L1").addSubjectElements(Arrays.asList("Test", "List")).build();

    List<String> expected = Arrays.asList("measurement", "voltage", "L1", "Test", "List");

    assertEquals(expected, actual.getSubject());
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createSubject_NoElements_test() throws Exception {
    RpcSubject.getBuilder().build();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void createSubject_EmptyElements_test() throws Exception {
    RpcSubject.getBuilder().addSubjectElement("measurement").addSubjectElement("")
        .addSubjectElement("L1").build();
  }
}