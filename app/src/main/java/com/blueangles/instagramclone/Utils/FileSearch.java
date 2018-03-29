package com.blueangles.instagramclone.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Ashith VL on 10/19/2017.
 */

public class FileSearch {
    /**
     * Search a directory and return a list of all **directories** contained inside
     *
     * @param directory
     * @return
     */
    public static ArrayList<String> getDirectoryPaths(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();
        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isDirectory()) {
                pathArray.add(listFiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }

    /**
     * Search a directory and return a list of all **files** contained inside
     *
     * @param directory
     * @return
     */
    public static ArrayList<String> getFilePaths(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();
        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isFile()) {
                if (listFiles[i].getAbsolutePath().contains(".jpeg") || listFiles[i].getAbsolutePath().contains(".jpg")
                        || listFiles[i].getAbsolutePath().contains(".png")) {
                    pathArray.add(listFiles[i].getAbsolutePath());
                }
            }
        }
        return pathArray;
    }
}
