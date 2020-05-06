package com.group5.quacker.utilities.zipper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ZipperTest {
    private Zipper zipper;

    @BeforeEach
    public void beforeEach() throws IOException {
        this.zipper = ZipperFactory.createZipper("archive");
    }

    @AfterEach
    public void afterEach() throws IOException {
        this.zipper.closeZipper();
        this.zipper = null;
    }


    @Test
    @DisplayName("Factory method creates an object")
    public void testFactoryMethodWorks() throws IOException {
        Zipper zipper = ZipperFactory.createZipper("hello");
        assertThat(zipper, not(nullValue()));
        zipper.closeZipper();
    }

    @Test
    @DisplayName("Zipper adds files and creates an archive")
    public void testActiveZipperAddsFile() throws IOException {
        this.zipper.addFile(File.createTempFile("first", ".txt"), "first");
        this.zipper.addFile(File.createTempFile("second", ".txt"), "second");
        this.zipper.addFile(File.createTempFile("third", ".txt"), "third");
        File archive = this.zipper.getArchive();
        assertThat(archive, not(nullValue()));
    }

    @Test
    @DisplayName("Closed zipper returns null archive")
    public void testClosedZipperReturnsNullArchive() throws IOException {
        this.zipper.closeZipper();
        File archive = this.zipper.getArchive();
        assertThat(archive, is(nullValue()));
    }

    @Test
    @DisplayName("Zipper only returns archive once")
    public void testZipperReturnsArchiveOnce() throws IOException {
        File archive1 = this.zipper.getArchive();
        File archive2 = this.zipper.getArchive();

        assertThat(archive1, not(nullValue()));
        assertThat(archive2, is(nullValue()));
    }

    @Test
    @DisplayName("Zipper can add files with same name")
    public void testZipperAddsFilesWithSameName() throws IOException {
        this.zipper.addFile(File.createTempFile("first", ".txt"), "first");
        this.zipper.addFile(File.createTempFile("first", ".txt"), "first");
        File archive = this.zipper.getArchive();
        assertThat(archive, not(nullValue()));
    }
}
