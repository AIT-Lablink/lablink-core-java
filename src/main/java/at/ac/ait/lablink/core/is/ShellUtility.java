//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.is;

import at.ac.ait.lablink.core.meta.Version;
import at.ac.ait.lablink.core.utility.Utility;

public class ShellUtility {

  public static String SHELL_PROMT = "llclient";
  public static String SHELL_VERSION = Version.getVersion();
  public static String SHELL_WELCOME_MESSAGE = Utility.INFO_PRODUCT_ASCII_ART
      + Utility.INFO_COPYRIGHTS_TEXT + "\n\nWelcome to the AIT Lablink Client Shell!"
      + "\nPlease type your commands below." + "\nYou can enter ?l to list the available commands.";
}
