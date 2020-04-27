package com.group5.quacker.utilities.zipper;

import java.io.IOException;

/**
 * A factory class for creating Zippers
 */
public class ZipperFactory {
    /**
     * Creates a new Zipper using the archive name as the name of the resulting zip file. File extension is
     * automatically appended
     * @param archiveName
     * @return
     * @throws IOException
     */
    public static Zipper createZipper(String archiveName) throws IOException {
        return new ZipperContext(archiveName);
    }
}
