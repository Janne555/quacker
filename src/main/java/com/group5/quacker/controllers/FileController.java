package com.group5.quacker.controllers;

import com.group5.quacker.entities.FileMap;
import com.group5.quacker.repositories.FileMapRepository;
import com.group5.quacker.services.FileService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class FileController {
    @Autowired
    FileService fileService;

    @Autowired
    FileMapRepository fileMapRepository;

    private Long CHUNK_SIZE = 10000L;

    @GetMapping("/files/{id}")
    public ResponseEntity getFile(@PathVariable("id") String id, @RequestParam(name = "with-original-name", required = false) String withOriginalName) throws IOException {
        FileMap fileMap = fileMapRepository.findByPublicId(id);

        if (fileMap == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        InputStream inputStream = fileService.getInputStream(fileMap.getFileName());

        if (inputStream == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", fileMap.getContentType());
        if (withOriginalName != null) {
            headers.add("Content-disposition", "attachment; filename=" + fileMap.getOriginalFileName());
        }

        return new ResponseEntity(IOUtils.toByteArray(inputStream), headers, HttpStatus.OK);
    }

    @GetMapping("/video/{id}")
    public ResponseEntity<ResourceRegion> getVideo(
            @PathVariable("id") String id,
            @RequestHeader HttpHeaders headers
    ) throws IOException {
        FileMap fileMap = fileMapRepository.findByPublicId(id);

        if (fileMap == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        long startRange = headers.getRange().get(0).getRangeStart(fileMap.getSize());
        long endRange = startRange + CHUNK_SIZE > fileMap.getSize() ? fileMap.getSize() : startRange + CHUNK_SIZE;
        UrlResource video = new UrlResource(fileService.getFile(fileMap.getFileName()).toURI());
        ResourceRegion region = new ResourceRegion(video, startRange, endRange);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header("Content-type", fileMap.getContentType())
                .contentLength(endRange - startRange)
                .body(region);
    }


    @PostMapping("/files")
    public String postFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "redirect", required = false) String redirect) throws IOException {
        FileMap fileMap = fileService.storeFile(file);

        if (redirect != null) {
            return redirect;
        }

        // TODO Tää redirect on enimmäkseen testausta varten. Me kuitenkin halutaan et se redirect riippuu kontekstista.
        return "redirect:/files/" + fileMap.getPublicId();
    }
}
