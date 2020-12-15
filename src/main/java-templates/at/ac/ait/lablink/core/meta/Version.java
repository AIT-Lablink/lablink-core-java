package at.ac.ait.lablink.core.meta;

public final class Version {

  private static final String VERSION = "${project.version}";
  private static final String GROUPID = "${project.groupId}";
  private static final String ARTIFACTID = "${project.artifactId}";
  private static final String REVISION = "${buildNumber}";

  public static String getVersion() {
    return VERSION;
  }
  // other getters...
}
