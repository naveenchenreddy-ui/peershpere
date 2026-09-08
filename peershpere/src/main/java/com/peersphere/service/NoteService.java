package com.peersphere.service;

import com.peersphere.dto.request.CreateNoteRequest;
import com.peersphere.dto.request.UpdateNoteRequest;
import com.peersphere.dto.response.NoteResponse;

import java.util.List;

public interface NoteService {
    NoteResponse uploadNote(String email, CreateNoteRequest request);
    NoteResponse getNoteById(String email, Long noteId);
    List<NoteResponse> getNotesByGroup(String email, Long groupId);
    List<NoteResponse> getMyNotes(String email);
    List<NoteResponse> getPublicNotes(String email);
    List<NoteResponse> searchNotes(String email, String keyword);
    NoteResponse updateNote(String email, Long noteId, UpdateNoteRequest request);
    void deleteNote(String email, Long noteId);
    NoteResponse toggleLike(String email, Long noteId);
    NoteResponse toggleBookmark(String email, Long noteId);
    List<NoteResponse> getMyBookmarks(String email);
    NoteResponse trackDownload(String email, Long noteId);
}