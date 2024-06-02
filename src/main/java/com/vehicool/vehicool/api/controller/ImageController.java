package com.vehicool.vehicool.api.controller;

import com.vehicool.vehicool.business.service.StorageService;
import com.vehicool.vehicool.persistence.entity.FileData;
import com.vehicool.vehicool.persistence.entity.Lender;
import com.vehicool.vehicool.persistence.repository.SystemStorageRepository;
import com.vehicool.vehicool.util.mappers.ResponseMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.vehicool.vehicool.util.constants.Messages.FAIL;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/images")
public class ImageController {
    private final StorageService storageService;
    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<Object> getImage(@PathVariable Long id) {
        try {
            byte[] fileData = storageService.downloadImageFromFileSystem(id);
            if (fileData == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Image not found !");
            }
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/png")).body(fileData); } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }
}
