package com.group5.quacker.entities;

import javax.persistence.*;

@Entity
public class FileMap {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String fileName;

    @Column(unique = true)
    private String publicId;

    private String contentType;

    private String originalFileName;

    private Long size;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getUrl() {
        String endpoint;
        if (contentType.startsWith("video") || contentType.startsWith("audio")) {
            endpoint = "stream";
        } else {
            endpoint = "files";
        }

        return String.format("/%s/%s", endpoint, getPublicId());
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
