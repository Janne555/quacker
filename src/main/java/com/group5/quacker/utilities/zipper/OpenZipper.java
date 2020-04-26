package com.group5.quacker.utilities.zipper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Handles operations related to zipping files
 * Method documentation can be found within the Zipper abstract class
 */
public class OpenZipper extends Zipper {
    private final List<String> usedFileNames;
    private final ZipOutputStream zos;
    private final File archive;
    private final FileOutputStream fos;

    protected OpenZipper(String archive) throws IOException {
        this.archive = File.createTempFile(archive, ".zip");
        this.usedFileNames = new ArrayList<>();
        this.fos = new FileOutputStream(this.archive);
        this.zos = new ZipOutputStream(fos);
    }

    @Override
    public void addFile(File file, String fileName) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry;
        if (this.usedFileNames.stream().anyMatch(usedFileName -> fileName.equals(usedFileName))) {
            String randomFileNameForZipEntry = new StringBuilder().append(fileName).append(UUID.randomUUID().toString()).toString();
            zipEntry = new ZipEntry(randomFileNameForZipEntry);
            this.usedFileNames.add(randomFileNameForZipEntry);
        } else {
            zipEntry = new ZipEntry(fileName);
            this.usedFileNames.add(fileName);
        }
        this.zos.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            this.zos.write(bytes, 0, length);
        }
        fis.close();
    }

    @Override
    public File getArchive() {
        return this.archive;
    }

    @Override
    protected Zipper closeZipper() throws IOException {
        this.zos.close();
        this.fos.close();
        return new ClosedZipper();
    }
}
