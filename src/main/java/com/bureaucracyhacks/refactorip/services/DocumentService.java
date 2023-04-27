package com.bureaucracyhacks.refactorip.services;

import com.bureaucracyhacks.refactorip.exceptions.DocumentNotFoundException;

import com.bureaucracyhacks.refactorip.models.DocumentJPA;


import com.bureaucracyhacks.refactorip.repositories.DocumentRepository;
import com.bureaucracyhacks.refactorip.repositories.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;


    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public void makeDocument(String name)
    {
        DocumentJPA document;
        try {
            document = documentRepository.findByName(name).orElseThrow();
        }
        catch(NoSuchElementException e)
        {
            throw new DocumentNotFoundException();
        }
        documentRepository.save(document);
    }

    public void removeDocument(String name)
    {
        DocumentJPA document;
        try {
            document = documentRepository.findByName(name).orElseThrow();
        }
        catch(NoSuchElementException e)
        {
            throw new DocumentNotFoundException();
        }
        documentRepository.delete(document);
    }

    public ResponseEntity<?> findByName(String name) {

            return ResponseEntity.ok(documentRepository.findByName(name));
    }


}
