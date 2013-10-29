package Merge;

import java.text.BreakIterator;

public class StringUtil {
     /**
      * 
      * <pre>
      * StringUtil.wrap(&quot;Hello World!, &quot;
      * <br>
      * &quot;, 9) = Hello
      * <br>
      * World! 
      * StringUtil.wrap(&quot;1234512345&quot;, &quot;
      * <br>
      * &quot;, 5) = 1234512345
      * StringUtil.wrap(&quot;12345&quot;, 10) = 12345
      * </pre>
      * 
      * @param string
      *             The String
      * @param lineSeparator
      *             Line break
      * @param wrapLength
      *             The position to create a line break
      * 
      * @return String
      */
     public static String wrap(String string, String lineSeparator,
               int wrapLength) {
          // string = "Das ist ein ganz stink-normaler demo-text, mit dem ich das Verhalten dieses Wrapper testen moechte, ohne dass ein
          // Langes Wort wie Schifffahrtsmeistereiangestelltenbeauftragter irgendwie Aerger machen koennte";

          // Null or blank string should return an empty ("") string
          if (isBlank(string)) {
               return "";
          }

          int stringLength = string.length();

          if (stringLength > wrapLength) {
               //Ensure.ensureArrayIndex(wrapLength, 1, Integer.MAX_VALUE);

               // Default to HTML line break since web app is client
               /*if (isBlank(lineSeparator)) {
                    lineSeparator = "<br>";
               }*/

               StringBuffer sb = new StringBuffer(stringLength
                         + ((stringLength / wrapLength) * 2 * lineSeparator.length()));
               BreakIterator lineIterator = BreakIterator.getLineInstance();
               lineIterator.setText(string);
               int start = lineIterator.first();
               int lineStart = start;

               for (int end = lineIterator.next(); end != BreakIterator.DONE; start = end, end = lineIterator.next()) {
                    if (end - lineStart < wrapLength) {
                         sb.append(string.substring(start, end));
                    } else {
                         // wrap
                         if (true || end - start < wrapLength) {
                              sb.append(lineSeparator);
                              sb.append(string.substring(start, end));
                         } else {
                              // TODO
                              // truncate
                         }
                         lineStart = end;
                    }
               }
               string = sb.toString();
          }

          return string;
     }

     /**
      * Check to see if the string has no value
      * 
      * @param String
      *             the string
      * 
      * @return boolean (true meaning no value)
      */
     public static boolean isBlank(String string) {
          return (string == null || string.trim().equals(""));
     }
}
