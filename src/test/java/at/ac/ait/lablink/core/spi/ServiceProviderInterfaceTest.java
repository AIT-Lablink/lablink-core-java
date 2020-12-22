//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.spi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Map;

/**
 * Unit tests for class SpiUtility.
 */
public class ServiceProviderInterfaceTest {

  @Test
  public void spiUtility_getHostImplementations_test() {
    Map<String, HostImplementationSpi> info = SpiUtility.getHostImplementations();

    assertEquals(1, info.size());  

    for (Map.Entry<String, HostImplementationSpi> impl : info.entrySet()) {
      assertEquals("LL_HOST_MQTT_IMPL", impl.getKey());
      assertEquals(false, impl.getValue().getHostSpi().isPseudoHost());
      assertEquals("Provides an implementation of the MQTT.", 
          impl.getValue().getHostSpi().description());
    }
  }
}
