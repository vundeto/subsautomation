import gui.FileChooser;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {

    public static boolean isVideoFile(String path) {
        return path.contains("mkv") || path.contains(".avi") || path.contains(".mp4");
    }

    public static HashMap<String, String> findSrtnVid(File location) {
        HashMap<String, String> names = new HashMap<>();
        ArrayList<String> srtArr = new ArrayList<>();
        ArrayList<String> vidArr = new ArrayList<>();
        File[] arr = location.listFiles();
        for (File f : arr) {
            if (f.getName().contains("srt")) srtArr.add(f.getAbsolutePath());
            if (isVideoFile(f.getAbsolutePath())) vidArr.add(f.getAbsolutePath());
        }
        for (int i = 0; i < vidArr.size(); i++) {
            try {
                names.put(srtArr.get(i), vidArr.get(i));
            } catch (Exception e) {
                break;
            }
        }
        return names;
    }

    public static void renameSrFile(File location) throws IOException {
        HashMap<String, String> map = findSrtnVid(location);
        Set<String> arr = map.keySet();
        for (String s : arr) {
            File file = new File(map.get(s));
            File file2 = new File(s);
            String type = file2.getName().substring(file2.getName().lastIndexOf("."), file2.getName().toCharArray().length);

            String dir = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + type;

            if (file2.renameTo(new File(dir))) {
                System.out.println("renamed");
            }
        }

    }


    public static boolean zipContainsSrt(String path) throws IOException {

        String str = "";
        ZipFile zip = null;

        try {
            zip = new ZipFile(path);

            for (Enumeration<?> e = zip.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                if (!entry.isDirectory()) {
                    str += entry.getName();
                    System.out.println(entry.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert zip != null;
            zip.close();
        }
        return str.contains("srt");
    }


    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(() -> {
            final FileChooser wnd = new FileChooser();
            wnd.setVisible(true);
        });
    }

}