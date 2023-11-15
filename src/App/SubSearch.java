package App;

import org.apache.commons.text.similarity.JaroWinklerDistance;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class SubSearch {

    public static String getURL(String name) throws URISyntaxException, MalformedURLException {
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("subsunacs.net")
                .setPath("search.php")
                .addParameter("m", "")
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

        return uri.toURL().toString().replaceFirst("=", "=" + name);
    }

    public static List<String> findSubLinks(String name) {
        HashSet<String> set = new HashSet<>();
        try {
            Document doc = Jsoup.connect(getURL(name)).get();
            System.out.println(doc);
            Elements links = doc.select("a[href*=/subtitles/]");
            for (Element e : links) {
                if (!e.toString().contains("comments") && !e.toString().contains("trailer")
                        && !e.toString().contains("!")) {
                    String link = e.attr("href");
                    set.add(link);
                }
            }
        } catch (Exception e) {
            new ArrayList<>();
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

    public static void downloadFile(String url, String path, String name) throws IOException, URISyntaxException {
        HttpGet request = new HttpGet(url);
        request.addHeader("Referer", getURL(name));
        CloseableHttpClient client = HttpClients.createDefault();
        try (CloseableHttpResponse response = client.execute(request)) {
            String n = "untitled.rar";
            HttpEntity entity = response.getEntity();
            System.out.println(entity.getContentType());
            if (response.containsHeader("Content-Disposition")) {
                n = response.getHeader("Content-Disposition").getValue()
                        .replaceAll("\"", "").substring(21);
            }
            File file = new File(path + "/" + n);
            file.createNewFile();
            try (InputStream inputStream = entity.getContent();
                 FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                int read;
                byte[] buffer = new byte[4096];
                while ((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    public static String parseTitle(String filename) {
        for (int i = 0; i < filename.length() - 4; i++) {
            int j = i + 4;
            if (filename.substring(i, j).matches("(19|20)\\d{2}$") && i != 0) {
                return filename.substring(0, i).replace(".", " ");
            }
        }

        String[] arr = {"HDTV", "WEB[-.]?DL", "HDDVD", "DVDRip", "DVD", "B[DR]Rip", "Blu[-.\\ ]?Ray", "HDRip", "WEBRIP"};
        int j = -1;
        for (int i = 0; i < filename.length(); i++) {
            for (String s : arr) {
                if (i > s.length() && filename.substring(i - s.length(), i).matches(s))
                    j = i - s.length();
            }
        }
        if (j != -1) filename = filename.substring(0, j);
        return filename.replace(".", " ");
    }

    public static String mostAccurateEntry(List<String> list, String name) {
        int max = -1;
        for (int i = 0; i < list.size(); i++) {
            double index = new JaroWinklerDistance().apply(list.get(i), name);
            if (index > max) max = i;
        }
        return list.get(max);
    }
}

