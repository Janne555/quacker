package com.group5.quacker.utilities.zipper;

import java.io.File;
/**
 * Represents the functionality of a closed Zipper
 * Method documentation can be found within the Zipper abstract class
 */
public class ClosedZipper extends Zipper {
    protected ClosedZipper() {
    }

    @Override
    public void addFile(File file, String fileName) {
    }

    @Override
    public File getArchive() {
        return null;
    }

    @Override
    protected Zipper closeZipper() {
        return null;
    }
}
