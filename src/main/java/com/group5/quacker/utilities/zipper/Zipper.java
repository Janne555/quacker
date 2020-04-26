package com.group5.quacker.utilities.zipper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A helper class for zipping multiple files.
 * Follows the state design pattern.
 */
public abstract class Zipper {
    /**
     * Add a file to the zip archive. If a file by that name has already been included, a UUID string will be appended.
     * If the zipper has been closed this method does nothing
     * @param file
     * @param fileName
     * @throws IOException
     */
    public abstract void addFile(File file, String fileName) throws IOException;

    /**
     * Return the archive and close the zipper
     * If the zipper has been closed this method returns null
     * @return
     * @throws IOException
     */
    public abstract File getArchive() throws IOException;

    /**
     * Closes the zipper
     * If the zipper has been closed this method does nothing
     * @return
     * @throws IOException
     */
    protected abstract Zipper closeZipper() throws IOException;
}
