package App;


import org.apache.hc.core5.net.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

public class SubSearch {

    public static String getURL(String name) throws URISyntaxException, MalformedURLException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("subsunacs.net")
                .setPath("search.php")
                .addParameter("m", name)
                .addParameter("l", "0")
                .addParameter("y", "o")
                .addParameter("action", "+++%D2%FA%F0%F1%E8+++")
                .addParameter("t", "Submit")
                .addParameter("a", "")
                .addParameter("g", "")
                .addParameter("u", "")
                .addParameter("d", "")
                .addParameter("c", "")
                .build();

        return uri.toURL().toString();
    }

    public static Set<String> findSubLinks(String name) {
        HashSet<String> set = new HashSet<>();
        try {
            Document doc = Jsoup.connect(getURL(name)).get();
            Elements links = doc.select("a[href*=/subtitles/]");
            for (Element e : links) {
                if (!e.toString().contains("comments") && !e.toString().contains("trailer")
                        && !e.toString().contains("!")) {
                    String link = e.attr("href");
                    set.add(link);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return set;
    }
    public static void downloadFile(String url){

    }


}
