package App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class UnzipAndRename {

    public static File findZip(File dir) throws NoZipException, IOException {
        if (!dir.isDirectory()) return dir;
        File[] arr = dir.listFiles();
        for (File f : arr) {
            if (f.getName().contains("zip") || f.getName().contains("rar"))
                return f;
        }
        throw new NoZipException("No zip/rar file found in path");
    }

    public static void unzipFile(File zipped, String pathname) throws IOException {
        byte[] buffer = new byte[2048];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipped));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            if (zipEntry.isDirectory()) continue;
            else {
                File newFile = new File(pathname + "/" + zipEntry.getName());

                FileOutputStream stream = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    stream.write(buffer, 0, len);
                }
                zipEntry = zis.getNextEntry();
                stream.close();
            }
        }
        zis.closeEntry();
        zis.close();

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

    public static boolean isVideoFile(String path) {
        return path.contains("mkv") || path.contains("mp4") || path.contains("avi") || path.contains("wav");
    }

    public static void apply(String dir) throws IOException, NotDirectoryException, NoZipException, URISyntaxException {
        File file = new File(dir);
        File toUnzip = findZip(file);
        unzipFile(toUnzip, dir);
        renameSrFile(file);
    }

    public static void main(String[] args) throws IOException {
        unzipFile(new File("C:/filmi/Heavenly.Creatures.1994.UNCUT.BRRip.x264.AC3-HUD/" +
                        "heavenly.creatures.1994.uncut.720p.bluray.x264-avs720(subsunacs.net).rar"),
                "C:/filmi/Heavenly.Creatures.1994.UNCUT.BRRip.x264.AC3-HUD");
    }


}

