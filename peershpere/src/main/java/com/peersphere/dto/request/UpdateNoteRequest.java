package com.peersphere.dto.request;

import lombok.Data;

@Data
public class UpdateNoteRequest {
    private String title;
    private String description;
    private String subject;
    private String tags;
}