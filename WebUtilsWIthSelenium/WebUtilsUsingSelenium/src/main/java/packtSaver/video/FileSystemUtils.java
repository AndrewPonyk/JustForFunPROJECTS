package packtSaver.video;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated(since = "Do not need this - V2 save already indexes during download using InputStream")
public class FileSystemUtils {

    public static void main(String[] args) {
        // folderContainsUncompletedDownloads(PACKT_VIDEO_DOWNLOAD_PATH);
        sortFilesByDateAndAppendIndexes("C:\\tmp\\packt\\video\\Hands-On Continuous Integration and Automation with Jenkins [Video]");

    }

    public static void sortFilesByDateAndAppendIndexes(String path) {
        Collection<File> filesAndDirs =
                FileUtils.listFilesAndDirs(new File(path),
                        new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY);

        filesAndDirs.stream().skip(1).forEach(dir -> {
            Collection<File> files =
                    FileUtils.listFiles(new File(dir.getPath()),
                            TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            Collections.sort((List<File>) files, new Comparator<>() {
                public int compare(File f1, File f2) {
                    return Long.compare(f1.lastModified(), f2.lastModified());
                }
            });
            int counter = 1;

            for (File file : files) {
                file.renameTo(new File(FilenameUtils.getFullPath(file.getPath()) + counter + FilenameUtils.getName(file.getPath())));
                System.out.println(files.size());
                counter++;
            }
        });

    }

    public static Boolean folderContainsUncompletedDownloads(String path) {
        Collection<File> files =
                FileUtils.listFiles(new File(path),
                        TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

        List<File> crdownload = files
                .stream()
                .filter(file -> file.getName().contains("crdownload"))
                .collect(Collectors.toList());

        return crdownload.size() > 0;

    }
}
