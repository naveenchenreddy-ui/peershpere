package com.peersphere.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateNoteRequest {

    @NotBlank(message = "Note title is required")
    private String title;

    private String description;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "File URL is required")
    private String fileUrl;

    private String fileType;

    private Long fileSizeKb;

    private String tags;  // comma-separated: "java,oop,spring"

    private Long groupId; // optional — if null, note is public across platform
}