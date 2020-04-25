package eu.hywse.lib.bukkit;

import eu.hywse.lib.text.WseChatColor;
import java.text.DecimalFormat;

public class WseTextUtil {

  /*
     █ -> full
     ▓ -> triple
     ▒ -> half
     ░ -> empty

     z.B: [█████▓░░░░] 45%
  */
  private static final String pFull = "&2█";
  private static final String pTriple = "&a█";
  private static final String pHalf = "&7█";
  private static final String pEmpty = "&8█";
  private static final DecimalFormat FORMAT = new DecimalFormat("0.00");

  /**
   * Alias to WseChatColor#translateAlternateColors
   *
   * @param i String | Input
   * @return String | Colorized input
   */
  public static String c(String i) {
    return WseChatColor.translateAlternateColorCodes('&', i);
  }

  /**
   * Alias to WseChatColor#stripColor
   *
   * @param i String | Input
   * @return String | Uncolorized input
   */
  public static String nc(String i) {
    return WseChatColor.stripColor(i);
  }

  /**
   * Converts the input to colors first, then removes it
   *
   * @param i String | Input
   * @return String | Uncolorized input
   */
  public static String ncc(String i) {
    return nc(c(i));
  }

  /**
   * Generates a graphical progressbar.
   *
   * @param percent What percentage should be displayed?
   * @param format  (ex. &8&l< &r%1$s &8&l| &r%2$s%% &8&l>)
   * @param cfull   Block symbol for FULL
   * @param ctriple Block symbol for TRIPLE
   * @param chalf   Block symbol for HALF
   * @param cempty  Block symbol for EMPTY
   * @return ProgressBar as String
   */
  private static String getProgressBar(double percent, String format, String cfull, String ctriple,
      String chalf, String cempty, DecimalFormat decimalFormat) {
    StringBuilder bar = new StringBuilder();

    double var = percent / 10;
    String varStr = String.valueOf(var);
    varStr = varStr.replaceAll("[^0-9]", "");

    int z1 = 0, z2 = 0;
    if (varStr.length() > 0) {
      z1 = Integer.parseInt(varStr.substring(0, 1));
    }
    if (varStr.length() > 1) {
      z2 = Integer.parseInt(varStr.substring(1, 2));
    }

    if (varStr.length() >= 3 && varStr.startsWith("10")) {
      z1 = 10;
    }

    for (int i = 0; i < 10; i++) {
      if (i < z1) {
        bar.append(cfull);
      } else if (i == z1) {
        if (z2 == 0) {
          bar.append(cempty);
        } else if (z2 < 5) {
          bar.append(chalf);
        } else {
          bar.append(ctriple);
        }
      } else {
        bar.append(cempty);
      }
    }

    return WseChatColor.translateAlternateColorCodes('&',
        String.format(format, bar, decimalFormat.format(percent)));
  }

  /**
   * Generates a graphical progressbar.
   *
   * @param percent What percentage should be displayed?
   * @param format  Format of Progressbar
   * @return String | (ex. ( ███████████ | 100% )
   */
  public static String generateProgressBar(double percent, String format) {
    return getProgressBar(percent, format, pFull, pTriple, pHalf, pEmpty, FORMAT);
  }

  /**
   * Generates a graphical progressbar.
   *
   * @param percent What percentage should be displayed?
   * @return String | (ex. ( ███████████ | 100% )
   */
  public static String generateProgressBar(double percent) {
    return generateProgressBar(percent, "&8&l< &r%1$s &8&l| &r%2$s%% &8&l>");
  }

  /**
   * This function appends the color code of the last word / words to each new word and the annoying
   * error that colors are no longer displayed in the next line should be fixed here
   *
   * @param input       Input
   * @param colorSymbol Color-Symbol
   * @return String
   */
  public static String repeatColor(String input, char colorSymbol) {
    char lastColor = ' ';
    StringBuilder result = new StringBuilder();
    String[] splitted = input.split(" ");

    for (int i = 0; i < splitted.length; i++) {

      // Last word?
      boolean append = (splitted.length - 1) != i;

      String word = splitted[i];
      result.append(word).append(append ? " " : "");

      // Get last color
      char[] chars = word.toCharArray();
      for (int a = 0; a < chars.length; a++) {
        char character = chars[a];

        //                &                   §
        if (character == '&' || character == '\u00A7') {
          if (chars.length < (a + 1)) {
            continue;
          }
          char color = chars[a + 1];

          // Check if char is a color char
          if ("0123456789abcdefklmnor".indexOf(color) == -1) {
            continue;
          }

          lastColor = color;
        }
      }

      // Append last color
      if (lastColor != ' ' && append) {
        result.append(colorSymbol).append(lastColor);
      }
    }

    return result.toString();
  }

  /**
   * This function appends the color code of the last word / words to each new word and the annoying
   * error that colors are no longer displayed in the next line should be fixed here
   *
   * @param input Input
   * @return String
   */
  public static String repeatColor(String input) {
    return repeatColor(input, '\u00A7');
  }

  /**
   * This function converts milliseconds into a time text: "1000 = 1 second", "61000 = 1 minute, 1
   * second", etc.
   *
   * @param input       Your input in milliseconds
   * @param calcDays    Should this function calculate days?
   * @param calcHours   Should this function calculate hours?
   * @param calcMinutes Should this function calculate minutes?
   * @param calcSeconds Should this function calculate seconds?
   * @return String       Time as String
   */
  public static String toTime(long input, boolean calcDays, boolean calcHours, boolean calcMinutes,
      boolean calcSeconds) {

    // If everything is disabled, calculate seconds
    if (!calcDays && !calcHours && !calcMinutes && !calcSeconds) {
      calcSeconds = true;
    }

    int days = 0, hours = 0, minutes = 0, seconds = 0;

    if (calcDays) {
      while (input >= 86400000) {
        days++;
        input -= 86400000;
      }
    }

    if (calcHours) {
      while (input >= 1000 * 60 * 60) {
        hours++;
        input -= 1000 * 60 * 60;
      }
    }

    if (calcMinutes) {
      while (input >= 1000 * 60) {
        minutes++;
        input -= 1000 * 60;
      }
    }

    if (calcSeconds) {
      while (input >= 1000) {
        seconds++;
        input -= 1000;
      }
    }

    String strDay = days == 1 ? "Tag" : "Tage";
    String strHour = hours == 1 ? "Stunde" : "Stunden";
    String strMinute = minutes == 1 ? "Minute" : "Minuten";
    String strSecond = seconds == 1 ? "Sekunde" : "Sekunden";

    return (calcDays ? days + " " + strDay + ", " : "") +
        (calcHours ? hours + " " + strHour + ", " : "") +
        (calcMinutes ? minutes + " " + strMinute + ", " : "") +
        (calcSeconds ? seconds + " " + strSecond : "");
  }


  /**
   * Converts "GOLDEN_APPLE" to "Golden Apple"
   *
   * @param input Your input
   * @return Your input in camel case
   */
  public static String toCamelCase(String input) {

    StringBuilder res = new StringBuilder();
    char[] chars = input.toCharArray();

    for (int i = 0; i < chars.length; i++) {
      char c = chars[i];

      if (i == 0) {
        res.append(Character.toUpperCase(c));
        continue;
      }

      if (c == '_' || c == ' ') {
        res.append(" ");
        if (chars.length >= (i + 1)) {
          res.append(Character.toUpperCase(chars[i + 1]));
          i++;
        }
        continue;
      }

      res.append(Character.toLowerCase(c));
    }

    return res.toString();
  }

}
