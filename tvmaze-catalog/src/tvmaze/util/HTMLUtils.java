package src.tvmaze.util;

public class HTMLUtils {

    public static String removeHTML(String text) {
        if (text == null) {
            return null;
        }

        // Remove tags HTML
        String clean = text.replaceAll("<[^>]*>", "");

        // Decodifica entidades HTML comuns
        clean = clean.replace("&amp;", "&");
        clean = clean.replace("&lt;", "<");
        clean = clean.replace("&gt;", ">");
        clean = clean.replace("&quot;", "\"");
        clean = clean.replace("&#39;", "'");
        clean = clean.replace("&nbsp;", " ");

        return clean.trim();
    }
}
