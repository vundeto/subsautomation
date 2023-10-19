package App;


import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.net.URIBuilder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

    public static List<String> findSubLinks(String name) {
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
        return set.stream().toList();
    }

    public static BasicCookieStore getCookies() throws IOException, URISyntaxException {
        Connection.Response response = Jsoup.connect("https://subsunacs.net/subtitles/Sound_Of_Freedom-151165/").execute();
        BasicCookieStore cs = new BasicCookieStore();
        Map<String, String> cookies = response.cookies();
        System.out.println(response.cookies());
        for (Map.Entry<String, String> cookieEntry : cookies.entrySet()) {
            System.out.println(cookieEntry);
            BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
            cs.addCookie(cookie);
        }
        return cs;
    }

    public static void downloadFile(String url, String path) throws IOException {
        HttpGet request = new HttpGet(url);
        request.addHeader("Referer", "https://subsunacs.net");
        CloseableHttpClient client = HttpClients.createDefault();
        try (CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            String name = response.getHeader("Content-Disposition").getValue().replaceAll("\"", "").substring(21);
            File file = new File(path + "/" + name);
            file.createNewFile();
            if (entity != null) {
                try (InputStream inputStream = entity.getContent();
                     FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    int read;
                    byte[] buffer = new byte[4096];
                    while ((read = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, read);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

}
