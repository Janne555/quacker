package com.group5.quacker.utilities.zipper;

import java.io.File;
import java.io.IOException;

/**
 * Context class for the zipper
 * Holds the current active zipper and delegates method calls to it.
 * Method documentation can be found within the Zipper abstract class
 */
public class ZipperContext extends Zipper {
    private Zipper activeZipper;

    protected ZipperContext(String archiveName) throws IOException {
        this.activeZipper = new OpenZipper(archiveName);
    }

    @Override
    public void addFile(File file, String fileName) throws IOException {
        this.activeZipper.addFile(file, fileName);
    }

    @Override
    public File getArchive() throws IOException {
        File archive = this.activeZipper.getArchive();
        this.activeZipper = this.activeZipper.closeZipper();
        return archive;
    }

    @Override
    protected Zipper closeZipper() throws IOException {
        this.activeZipper = this.activeZipper.closeZipper();
        return null;
    }
}
