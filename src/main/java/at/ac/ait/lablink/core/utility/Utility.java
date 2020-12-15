//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.utility;

import at.ac.ait.lablink.core.Configuration;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;
import at.ac.ait.lablink.core.rd.ResourceDiscoveryClientMeta;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.undercouch.bson4jackson.BsonFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * The Class Utility.
 */
public class Utility {

  public static final String ANSI_COLOR_RESET = "\033[0m"; // Text Reset

  // Regular Colors
  public static final String ANSI_COLOR_BLACK = "\033[0;30m"; // BLACK
  public static final String ANSI_COLOR_RED = "\033[0;31m"; // RED
  public static final String ANSI_COLOR_GREEN = "\033[0;32m"; // GREEN
  public static final String ANSI_COLOR_YELLOW = "\033[0;33m"; // YELLOW
  public static final String ANSI_COLOR_BLUE = "\033[0;34m"; // BLUE
  public static final String ANSI_COLOR_PURPLE = "\033[0;35m"; // PURPLE
  public static final String ANSI_COLOR_CYAN = "\033[0;36m"; // CYAN
  public static final String ANSI_COLOR_WHITE = "\033[0;37m"; // WHITE

  // Bold
  public static final String ANSI_COLOR_BLACK_BOLD = "\033[1;30m"; // BLACK
  public static final String ANSI_COLOR_RED_BOLD = "\033[1;31m"; // RED
  public static final String ANSI_COLOR_GREEN_BOLD = "\033[1;32m"; // GREEN
  public static final String ANSI_COLOR_YELLOW_BOLD = "\033[1;33m"; // YELLOW
  public static final String ANSI_COLOR_BLUE_BOLD = "\033[1;34m"; // BLUE
  public static final String ANSI_COLOR_PURPLE_BOLD = "\033[1;35m"; // PURPLE
  public static final String ANSI_COLOR_CYAN_BOLD = "\033[1;36m"; // CYAN
  public static final String ANSI_COLOR_WHITE_BOLD = "\033[1;37m"; // WHITE

  // Underline
  public static final String ANSI_COLOR_BLACK_UNDERLINED = "\033[4;30m"; // BLACK
  public static final String ANSI_COLOR_RED_UNDERLINED = "\033[4;31m"; // RED
  public static final String ANSI_COLOR_GREEN_UNDERLINED = "\033[4;32m"; // GREEN
  public static final String ANSI_COLOR_YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
  public static final String ANSI_COLOR_BLUE_UNDERLINED = "\033[4;34m"; // BLUE
  public static final String ANSI_COLOR_PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
  public static final String ANSI_COLOR_CYAN_UNDERLINED = "\033[4;36m"; // CYAN
  public static final String ANSI_COLOR_WHITE_UNDERLINED = "\033[4;37m"; // WHITE

  // Background
  public static final String ANSI_COLOR_BLACK_BACKGROUND = "\033[40m"; // BLACK
  public static final String ANSI_COLOR_RED_BACKGROUND = "\033[41m"; // RED
  public static final String ANSI_COLOR_GREEN_BACKGROUND = "\033[42m"; // GREEN
  public static final String ANSI_COLOR_YELLOW_BACKGROUND = "\033[43m"; // YELLOW
  public static final String ANSI_COLOR_BLUE_BACKGROUND = "\033[44m"; // BLUE
  public static final String ANSI_COLOR_PURPLE_BACKGROUND = "\033[45m"; // PURPLE
  public static final String ANSI_COLOR_CYAN_BACKGROUND = "\033[46m"; // CYAN
  public static final String ANSI_COLOR_WHITE_BACKGROUND = "\033[47m"; // WHITE

  // High Intensity
  public static final String ANSI_COLOR_BLACK_BRIGHT = "\033[0;90m"; // BLACK
  public static final String ANSI_COLOR_RED_BRIGHT = "\033[0;91m"; // RED
  public static final String ANSI_COLOR_GREEN_BRIGHT = "\033[0;92m"; // GREEN
  public static final String ANSI_COLOR_YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
  public static final String ANSI_COLOR_BLUE_BRIGHT = "\033[0;94m"; // BLUE
  public static final String ANSI_COLOR_PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
  public static final String ANSI_COLOR_CYAN_BRIGHT = "\033[0;96m"; // CYAN
  public static final String ANSI_COLOR_WHITE_BRIGHT = "\033[0;97m"; // WHITE

  // Bold High Intensity
  public static final String ANSI_COLOR_BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
  public static final String ANSI_COLOR_RED_BOLD_BRIGHT = "\033[1;91m"; // RED
  public static final String ANSI_COLOR_GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
  public static final String ANSI_COLOR_YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
  public static final String ANSI_COLOR_BLUE_BOLD_BRIGHT = "\033[1;94m"; // BLUE
  public static final String ANSI_COLOR_PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
  public static final String ANSI_COLOR_CYAN_BOLD_BRIGHT = "\033[1;96m"; // CYAN
  public static final String ANSI_COLOR_WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

  // High Intensity backgrounds
  public static final String ANSI_COLOR_BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
  public static final String ANSI_COLOR_RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
  public static final String ANSI_COLOR_GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN
  public static final String ANSI_COLOR_YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW
  public static final String ANSI_COLOR_BLUE_BACKGROUND_BRIGHT = "\033[0;104m";// BLUE
  public static final String ANSI_COLOR_PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
  public static final String ANSI_COLOR_CYAN_BACKGROUND_BRIGHT = "\033[0;106m"; // CYAN
  public static final String ANSI_COLOR_WHITE_BACKGROUND_BRIGHT = "\033[0;107m"; // WHITE

  /** The logger. */
  private static Logger logger = LogManager.getLogger(Utility.class.getCanonicalName());

  /**
   * Parses the string and replace the environment variable token with its value. The token can be
   * specified with enclosing the name of the environment variable with a $ sign e.g. $LLCONFIG$.
   * The function will return the original string if it was not able to find the request environment
   * variable or if there is not token in the provided string.
   *
   * @param input the input with token enclosed in $TOKEN$
   * @return the string containing the value of the token substituted
   */
  public static String parseWithEnvironmentVariable(String input) {
    String output = null;
    String ev = StringUtils.substringBetween(input, "%", "%");
    if (StringUtils.isEmpty(ev)) {
      output = input;
    } else {
      String evVal = System.getenv(ev);
      if (evVal == null) {
        output = input;
      } else {
        output = input.replace("%" + ev + "%", evVal);
      }
    }

    return output;
  }

  /**
   * Gets the resource discovery meta json.
   *
   * @param meta the meta
   * @return the resource discovery meta json
   */
  public static String getResourceDiscoveryMetaJson(ResourceDiscoveryClientMeta meta) {
    ObjectMapper jsonObjectMapper = new ObjectMapper();

    String json = null;

    try {
      json = jsonObjectMapper.writeValueAsString(meta);
    } catch (JsonProcessingException ex) {
      logger.error(ex.getMessage());
    }

    logger.info("Generated JSON {}", json);

    return json;
  }


  /**
   * Gets the json.
   *
   * @param obj the obj
   * @return the json
   */
  public static String getJson(Object obj) {
    ObjectMapper jsonObjectMapper = new ObjectMapper();

    String json = null;

    try {
      json = jsonObjectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException ex) {
      logger.error(ex.getMessage());
    }

    logger.info("Generated JSON {}", json);

    return json;
  }

  /**
   * Gets the bson.
   *
   * @param obj the obj
   * @return the bson
   * @throws JsonGenerationException the json generation exception
   * @throws JsonMappingException the json mapping exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static byte[] getBson(Object obj)
      throws JsonGenerationException, JsonMappingException, IOException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectMapper mapper = new ObjectMapper(new BsonFactory());
    mapper.writeValue(baos, obj);


    return baos.toByteArray();
  }

  /**
   * Gets the rd from bson.
   *
   * @param reply the reply
   * @return the rd from bson
   * @throws JsonParseException the json parse exception
   * @throws JsonMappingException the json mapping exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static ResourceDiscoveryClientMeta getRdFromBson(byte[] reply)
      throws JsonParseException, JsonMappingException, IOException {
    ObjectMapper jsonObjectMapper = new ObjectMapper();

    ByteArrayInputStream bais = new ByteArrayInputStream(reply);
    ResourceDiscoveryClientMeta meta =
        jsonObjectMapper.readValue(bais, ResourceDiscoveryClientMeta.class);
    return meta;
  }

  /**
   * Gets the resource discovery meta encoded.
   *
   * @param meta the meta
   * @return the resource discovery meta encoded
   * @throws JsonGenerationException the json generation exception
   * @throws JsonMappingException the json mapping exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static byte[] getResourceDiscoveryMetaEncoded(Object meta)
      throws JsonGenerationException, JsonMappingException, IOException {
    byte[] reply;

    if (Configuration.RESOURCE_DISCOVERY_ENCODING_USE
        .equals(Configuration.RESOURCE_DISCOVERY_ENCODING_TYPE_BSON)) {
      reply = Utility.getBson(meta);
    } else {
      reply = Utility.getJson(meta).getBytes();
    }

    return reply;
  }

  private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");
  private static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss";
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_TIME_FORMAT);

  /**
   * Convert date-time string in the format dd.MM.yyyy HH:mm:ss to a long representing this date
   * as number of milliseconds from unix epoch.
   *
   * @param dateTimeString date-time string in the format dd.MM.yyyy HH:mm:ss
   * @return number of milliseconds from unix epoch
   */
  public static long dateStrToUnix(String dateTimeString) {

    DATE_FORMAT.setTimeZone(Utility.TIME_ZONE);
    try {
      return (DATE_FORMAT.parse(dateTimeString).getTime());
    } catch (ParseException ex) {
      throw new LlCoreRuntimeException(
          "Unable to parse unix timestamp from given date-time string '" + dateTimeString
              + "'. Expected format is '" + DATE_TIME_FORMAT + "'.");
    }
  }


  /**
   * Convert unix time (milliseconds since epoch) to a date-time string in the format dd.MM.yyyy
   * HH:mm:ss
   *
   * @param unixStampMs milliseconds since epoch
   * @return date-time string in the format dd.MM.yyyy HH:mm:ss
   */
  public static String unixToDateStr(long unixStampMs) {
    DATE_FORMAT.setTimeZone(Utility.TIME_ZONE);
    return (DATE_FORMAT.format(new Date(unixStampMs)));
  }


  /**
   * Convert unix time (milliseconds since epoch) to a date-time string in the format dd.MM.yyyy
   * HH:mm:ss
   *
   * @param unixStampMs milliseconds since epoch
   * @return date-time string in the format yyyyMMdd_HHmmss
   */
  public static String unixToIdentifierStr(long unixStampMs) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    dateFormat.setTimeZone(Utility.TIME_ZONE);

    return (dateFormat.format(new Date(unixStampMs)));
  }

  public static String INFO_PRODUCT_ASCII_ART = "\n"
      + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░\n"
      + "░░░█████╗░██╗████████╗░░░██╗░░░░░░█████╗░██████╗░██╗░░░░░██╗███╗░░██╗██╗░░██╗░░\n"
      + "░░██╔══██╗██║╚══██╔══╝░░░██║░░░░░██╔══██╗██╔══██╗██║░░░░░██║████╗░██║██║░██╔╝░░\n"
      + "░░███████║██║░░░██║░░░░░░██║░░░░░███████║██████╔╝██║░░░░░██║██╔██╗██║█████╔╝░░░\n"
      + "░░██╔══██║██║░░░██║░░░░░░██║░░░░░██╔══██║██╔══██╗██║░░░░░██║██║╚████║██╔═██╗░░░\n"
      + "░░██║░░██║██║░░░██║░░░░░░███████╗██║░░██║██████╔╝███████╗██║██║░╚███║██║░╚██╗░░\n"
      + "░░╚═╝░░╚═╝╚═╝░░░╚═╝░░░░░░╚══════╝╚═╝░░╚═╝╚═════╝░╚══════╝╚═╝╚═╝░░╚══╝╚═╝░░╚═╝░░\n"
      + "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░\n\n";

  public static String INFO_PRODUCT = "AIT Lablink";

  public static String INFO_COPYRIGHTS = "Copyright © 2020";

  public static String INFO_WEBSITE = "https://www.ait.ac.at";

  public static String INFO_ORGANIZATION = "AIT Austrian Institute of Technology GmbH";

  public static String INFO_LICENSE = "Distributed under the terms of the Modified BSD License";

  public static String INFO_COPYRIGHTS_TEXT = INFO_PRODUCT + " [" + Configuration.VERSION + "]\n"
      + INFO_COPYRIGHTS + " " + INFO_ORGANIZATION + ".\n" + INFO_LICENSE + ".\n"
      + "Visit " + INFO_WEBSITE + " for more information.\n";
}
