package com.peersphere.service.impl;

import com.peersphere.dto.request.CreateNoteRequest;
import com.peersphere.dto.request.UpdateNoteRequest;
import com.peersphere.dto.response.NoteResponse;
import com.peersphere.entity.*;
import com.peersphere.exception.ResourceNotFoundException;
import com.peersphere.exception.UnauthorizedException;
import com.peersphere.repository.*;
import com.peersphere.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final NoteLikeRepository noteLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final StudyGroupRepository studyGroupRepository;

    @Override
    @Transactional
    public NoteResponse uploadNote(String email, CreateNoteRequest request) {
        User uploader = getUserByEmail(email);

        StudyGroup group = null;
        if (request.getGroupId() != null) {
            group = studyGroupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        }

        Note note = Note.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .subject(request.getSubject())
                .fileUrl(request.getFileUrl())
                .fileType(request.getFileType())
                .fileSizeKb(request.getFileSizeKb())
                .tags(request.getTags())
                .uploadedBy(uploader)
                .studyGroup(group)
                .build();

        return mapToResponse(noteRepository.save(note), uploader.getId());
    }

    @Override
    public NoteResponse getNoteById(String email, Long noteId) {
        User user = getUserByEmail(email);
        Note note = getNoteOrThrow(noteId);
        return mapToResponse(note, user.getId());
    }

    @Override
    public List<NoteResponse> getNotesByGroup(String email, Long groupId) {
        User user = getUserByEmail(email);
        return noteRepository.findByStudyGroupIdOrderByCreatedAtDesc(groupId)
                .stream()
                .map(n -> mapToResponse(n, user.getId()))
                .toList();
    }

    @Override
    public List<NoteResponse> getMyNotes(String email) {
        User user = getUserByEmail(email);
        return noteRepository.findByUploadedByIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(n -> mapToResponse(n, user.getId()))
                .toList();
    }

    @Override
    public List<NoteResponse> getPublicNotes(String email) {
        User user = getUserByEmail(email);
        return noteRepository.findByStudyGroupIsNullOrderByCreatedAtDesc()
                .stream()
                .map(n -> mapToResponse(n, user.getId()))
                .toList();
    }

    @Override
    public List<NoteResponse> searchNotes(String email, String keyword) {
        User user = getUserByEmail(email);
        return noteRepository.searchNotes(keyword)
                .stream()
                .map(n -> mapToResponse(n, user.getId()))
                .toList();
    }

    @Override
    @Transactional
    public NoteResponse updateNote(String email, Long noteId, UpdateNoteRequest request) {
        User user = getUserByEmail(email);
        Note note = getNoteOrThrow(noteId);

        if (!note.getUploadedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only the note uploader can edit this note");
        }

        if (request.getTitle() != null)       note.setTitle(request.getTitle());
        if (request.getDescription() != null) note.setDescription(request.getDescription());
        if (request.getSubject() != null)     note.setSubject(request.getSubject());
        if (request.getTags() != null)        note.setTags(request.getTags());

        return mapToResponse(noteRepository.save(note), user.getId());
    }

    @Override
    @Transactional
    public void deleteNote(String email, Long noteId) {
        User user = getUserByEmail(email);
        Note note = getNoteOrThrow(noteId);

        if (!note.getUploadedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only the note uploader can delete this note");
        }

        noteRepository.delete(note);
    }

    @Override
    @Transactional
    public NoteResponse toggleLike(String email, Long noteId) {
        User user = getUserByEmail(email);
        Note note = getNoteOrThrow(noteId);

        /**
         * Toggle pattern — if already liked, unlike. If not liked, like.
         * This is the standard pattern for social features.
         * One endpoint handles both directions, making the frontend simpler.
         */
        if (noteLikeRepository.existsByNoteIdAndUserId(noteId, user.getId())) {
            // Unlike — remove the like record
            NoteLike existingLike = noteLikeRepository
                    .findByNoteIdAndUserId(noteId, user.getId()).get();
            noteLikeRepository.delete(existingLike);
            note.setLikeCount(Math.max(0, note.getLikeCount() - 1));
        } else {
            // Like — add a new like record
            NoteLike like = NoteLike.builder()
                    .note(note)
                    .user(user)
                    .build();
            noteLikeRepository.save(like);
            note.setLikeCount(note.getLikeCount() + 1);
        }

        return mapToResponse(noteRepository.save(note), user.getId());
    }

    @Override
    @Transactional
    public NoteResponse toggleBookmark(String email, Long noteId) {
        User user = getUserByEmail(email);
        Note note = getNoteOrThrow(noteId);

        if (bookmarkRepository.existsByNoteIdAndUserId(noteId, user.getId())) {
            Bookmark existing = bookmarkRepository
                    .findByNoteIdAndUserId(noteId, user.getId()).get();
            bookmarkRepository.delete(existing);
        } else {
            Bookmark bookmark = Bookmark.builder()
                    .note(note)
                    .user(user)
                    .build();
            bookmarkRepository.save(bookmark);
        }

        return mapToResponse(note, user.getId());
    }

    @Override
    public List<NoteResponse> getMyBookmarks(String email) {
        User user = getUserByEmail(email);
        return bookmarkRepository.findByUserIdOrderBySavedAtDesc(user.getId())
                .stream()
                .map(bookmark -> mapToResponse(bookmark.getNote(), user.getId()))
                .toList();
    }

    @Override
    @Transactional
    public NoteResponse trackDownload(String email, Long noteId) {
        User user = getUserByEmail(email);
        Note note = getNoteOrThrow(noteId);

        // Increment download counter
        note.setDownloadCount(note.getDownloadCount() + 1);
        return mapToResponse(noteRepository.save(note), user.getId());
    }

    // ── Private helpers ────────────────────────────────────────────

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Note getNoteOrThrow(Long noteId) {
        return noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Note not found with id: " + noteId));
    }

    private NoteResponse mapToResponse(Note note, Long currentUserId) {
        return NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .description(note.getDescription())
                .subject(note.getSubject())
                .fileUrl(note.getFileUrl())
                .fileType(note.getFileType())
                .fileSizeKb(note.getFileSizeKb())
                .downloadCount(note.getDownloadCount())
                .likeCount(note.getLikeCount())
                .tags(note.getTags())
                .uploadedById(note.getUploadedBy().getId())
                .uploadedByName(note.getUploadedBy().getFullName())
                .groupId(note.getStudyGroup() != null ? note.getStudyGroup().getId() : null)
                .groupName(note.getStudyGroup() != null ? note.getStudyGroup().getName() : null)
                // These two check the logged-in user's relationship with this note
                .likedByCurrentUser(
                        noteLikeRepository.existsByNoteIdAndUserId(note.getId(), currentUserId))
                .bookmarkedByCurrentUser(
                        bookmarkRepository.existsByNoteIdAndUserId(note.getId(), currentUserId))
                .createdAt(note.getCreatedAt())
                .build();
    }
}