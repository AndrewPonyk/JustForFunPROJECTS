import java.io.File;

public class FindBigFolders {
    public static void main(String[] args) {
        System.out.println("Find folders with big files");

        //read the path from the command line
        String path = "C:\\";
        System.out.println("Path: " + path);

        //
        File root = new File(path);
        findLargeFolders(root);
    }

    private static void findLargeFolders(File directory) {
        long size = calculateFolderSize(directory);

        if (size > 10L * 1024 * 1024 * 1024) { // More than 10GB
            System.out.println("=====Large folder: " + directory.getPath() + " Size: " + size + " bytes");
        }
        // Optionally, list directories directly under this one
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findLargeFolders(file);
                }
            }
        }
    }

    private static long calculateFolderSize(File folder) {
        long size = 0;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    //System.out.println(file.getName() + ": " + file.length() + " bytes");
                    size += file.length();
                } else if (file.isDirectory()) {
                    //size += calculateFolderSize(file);
                }
            }
        }
        if(size > 400*1024*1024){
            System.out.println("Folder:: " + folder.getPath() + " Size: " + size + " bytes");
        }
        return size;
    }
}
