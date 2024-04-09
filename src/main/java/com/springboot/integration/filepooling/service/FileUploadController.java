package com.springboot.integration.filepooling.service;

import com.springboot.integration.filepooling.configuration.FilePoolerProps;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/upload")
public class FileUploadController {

    private final FilePoolerProps filePoolerProps;

    @PostMapping
    public ResponseEntity<Boolean> uploadWatcherFiles(@RequestPart("file")MultipartFile file,
                                                      @RequestParam("flow") String flow){
        boolean uploadStatus = processUpload(file, flow);
        return Boolean.TRUE.equals(uploadStatus)?
                ResponseEntity.status(HttpStatus.ACCEPTED).body(uploadStatus):
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uploadStatus);
    }

    private boolean processUpload(MultipartFile file, String flow) {
        try{
            Optional<String> uploadDirectory = getUploadDirectoryByFlowName(flow);
            var fileName = file.getOriginalFilename();

            if(uploadDirectory.isPresent() && null != fileName){
                var targetDirectory = Path.of(uploadDirectory.get()).resolve(fileName);
                Files.copy(file.getInputStream(), targetDirectory, StandardCopyOption.REPLACE_EXISTING);
                return true;
            }else{
                return false;
            }
        }catch (IOException e){
            return false;
        }
    }

    private Optional<String> getUploadDirectoryByFlowName(String flow){
        return filePoolerProps.getDestinationdirectories().stream()
                .filter(map->map.containsKey("flow") && map.get("flow").equalsIgnoreCase(flow))
                .map(map -> map.get("directory"))
                .findFirst();
    }
}
