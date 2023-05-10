package com.bureaucracyhacks.refactorip.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "documents", uniqueConstraints = {
        @UniqueConstraint(columnNames = "document_id")
})
public class DocumentJPA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int document_id;
    private String name;
    private String description;
    private String price;
    private int institution_id;
    private String path;
    @Lob
    @Column(name="file")
    private byte[] file;
}
