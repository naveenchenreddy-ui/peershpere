package com.peersphere.controller;

import com.peersphere.dto.request.CreateNoteRequest;
import com.peersphere.dto.request.UpdateNoteRequest;
import com.peersphere.dto.response.NoteResponse;
import com.peersphere.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<NoteResponse> uploadNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateNoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noteService.uploadNote(userDetails.getUsername(), request));
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponse> getNoteById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId) {
        return ResponseEntity.ok(
                noteService.getNoteById(userDetails.getUsername(), noteId));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<NoteResponse>> getNotesByGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId) {
        return ResponseEntity.ok(
                noteService.getNotesByGroup(userDetails.getUsername(), groupId));
    }

    @GetMapping("/my-notes")
    public ResponseEntity<List<NoteResponse>> getMyNotes(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(noteService.getMyNotes(userDetails.getUsername()));
    }

    @GetMapping("/public")
    public ResponseEntity<List<NoteResponse>> getPublicNotes(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(noteService.getPublicNotes(userDetails.getUsername()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<NoteResponse>> searchNotes(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String keyword) {
        return ResponseEntity.ok(
                noteService.searchNotes(userDetails.getUsername(), keyword));
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<NoteResponse> updateNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId,
            @Valid @RequestBody UpdateNoteRequest request) {
        return ResponseEntity.ok(
                noteService.updateNote(userDetails.getUsername(), noteId, request));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId) {
        noteService.deleteNote(userDetails.getUsername(), noteId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/notes/{noteId}/like
     * Toggles like/unlike on a note.
     * POST because this creates/destroys a like record in the DB.
     */
    @PostMapping("/{noteId}/like")
    public ResponseEntity<NoteResponse> toggleLike(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId) {
        return ResponseEntity.ok(
                noteService.toggleLike(userDetails.getUsername(), noteId));
    }

    @PostMapping("/{noteId}/bookmark")
    public ResponseEntity<NoteResponse> toggleBookmark(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId) {
        return ResponseEntity.ok(
                noteService.toggleBookmark(userDetails.getUsername(), noteId));
    }

    @GetMapping("/bookmarks")
    public ResponseEntity<List<NoteResponse>> getMyBookmarks(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(noteService.getMyBookmarks(userDetails.getUsername()));
    }

    /**
     * POST /api/notes/{noteId}/download
     * Call this BEFORE redirecting to the file URL.
     * It increments the download counter, then returns the note
     * with the updated count and the fileUrl to redirect to.
     */
    @PostMapping("/{noteId}/download")
    public ResponseEntity<NoteResponse> trackDownload(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId) {
        return ResponseEntity.ok(
                noteService.trackDownload(userDetails.getUsername(), noteId));
    }
}