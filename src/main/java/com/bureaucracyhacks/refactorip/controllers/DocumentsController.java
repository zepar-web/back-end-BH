package com.bureaucracyhacks.refactorip.controllers;

import com.bureaucracyhacks.refactorip.exceptions.TaskNotFoundException;
import com.bureaucracyhacks.refactorip.models.DocumentJPA;
import com.bureaucracyhacks.refactorip.models.TaskJPA;
import com.bureaucracyhacks.refactorip.repositories.DocumentRepository;
import com.bureaucracyhacks.refactorip.repositories.TaskRepository;
import com.bureaucracyhacks.refactorip.utils.ImageUtil;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tasks")
public class DocumentsController {

    private final DocumentRepository documentRepository;
    private final TaskRepository taskRepository;


    @GetMapping("/{taskId}/documents")
    public ResponseEntity<List<DocumentJPA>> getDocumentsForTask(@PathVariable Long taskId) throws IOException {
        TaskJPA task = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);

        List<DocumentJPA> documents = task.getDocuments();

        List<DocumentJPA> processedDocuments = new ArrayList<>();

        for (DocumentJPA document : documents) {
            if (document.getFile() != null) {
                ResponseEntity<ByteArrayResource> response = downloadFile(document.getName());
                ByteArrayResource fileResource = response.getBody();
                assert fileResource != null;
                document.setFile(fileResource.getByteArray());
            }
            processedDocuments.add(document);
        }

        if (processedDocuments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(processedDocuments, HttpStatus.OK);
        }
    }


    @GetMapping("/get-doc-by-name/{taskName}")
    public ResponseEntity<List<DocumentJPA>> getDocumentsForTaskByName(@PathVariable String taskName) {
        TaskJPA task = taskRepository.findByName(taskName)
                .orElseThrow(TaskNotFoundException::new);

        List<DocumentJPA> documents = task.getDocuments();

        if (documents.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(documents, HttpStatus.OK);
        }
    }
    @PostMapping("/upload/{name}")
    public String handleFileUpload(@PathVariable String name, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            DocumentJPA document = documentRepository.findByName(name).orElseThrow(() -> new RuntimeException("Document not found"));
            document.setFile(ImageUtil.compressImage(file.getBytes()));
            documentRepository.save(document);
            redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload " + file.getOriginalFilename() + " => " + e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/download/{name}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String name) {
        DocumentJPA document = documentRepository.findByName(name).orElseThrow(() -> new RuntimeException("Document not found"));

        byte[] decompressedFile = ImageUtil.decompressImage(document.getFile());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + ".png\"")
                .body(new ByteArrayResource(decompressedFile));
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<TaskJPA> getTaskById(@PathVariable(value = "id") Long taskId){
        TaskJPA task = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);
        return ResponseEntity.ok().body(task);
    }

    @GetMapping("/find-by-name/{name}")
    public List<DocumentJPA> GetTaskByName(@PathVariable String name){
        TaskJPA task = taskRepository.findByName(name)
                .orElseThrow(TaskNotFoundException::new);

        return task.getDocuments();
    }

    @GetMapping("/names")
    public List<String> getAllTaskNames(){
        return taskRepository.findAll().stream().map(TaskJPA::getName).toList();
    }
}