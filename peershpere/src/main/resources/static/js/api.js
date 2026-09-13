// js/api.js
// Central place for all backend API calls
// Every function returns the parsed JSON response or throws an error

const BASE_URL = '/api';

/**
 * Core fetch wrapper.
 * Automatically adds JWT token to every request.
 * Redirects to login on 401.
 */
async function apiFetch(endpoint, options = {}) {
    const token = localStorage.getItem('token');

    const config = {
        headers: {
            'Content-Type': 'application/json',
            ...(token && { 'Authorization': `Bearer ${token}` }),
            ...options.headers
        },
        ...options
    };

    const response = await fetch(`${BASE_URL}${endpoint}`, config);

    if (response.status === 401) {
        localStorage.clear();
        window.location.href = 'login.html';
        return;
    }

    // 204 No Content — no body to parse
    if (response.status === 204) return null;

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || 'Something went wrong');
    }

    return data;
}

// ── Auth ───────────────────────────────────────────────────
const AuthAPI = {
    register: (data) => apiFetch('/auth/register', {
        method: 'POST', body: JSON.stringify(data)
    }),
    login: (data) => apiFetch('/auth/login', {
        method: 'POST', body: JSON.stringify(data)
    })
};

// ── User ───────────────────────────────────────────────────
const UserAPI = {
    getMyProfile:    () => apiFetch('/users/me'),
    getUserById:     (id) => apiFetch(`/users/${id}`),
    updateProfile:   (data) => apiFetch('/users/me', {
        method: 'PUT', body: JSON.stringify(data)
    }),
    addSkill:        (value) => apiFetch('/users/me/skills', {
        method: 'POST', body: JSON.stringify({ value })
    }),
    removeSkill:     (skill) => apiFetch(`/users/me/skills/${encodeURIComponent(skill)}`, {
        method: 'DELETE'
    }),
    addInterest:     (value) => apiFetch('/users/me/interests', {
        method: 'POST', body: JSON.stringify({ value })
    }),
    removeInterest:  (interest) => apiFetch(`/users/me/interests/${encodeURIComponent(interest)}`, {
        method: 'DELETE'
    }),
    searchUsers:     (keyword) => apiFetch(`/users/search?keyword=${encodeURIComponent(keyword)}`),
    deleteAccount:   () => apiFetch('/users/me', { method: 'DELETE' })
};

// ── Study Groups ────────────────────────────────────────────
const GroupAPI = {
    createGroup:   (data) => apiFetch('/groups', {
        method: 'POST', body: JSON.stringify(data)
    }),
    getAllGroups:   () => apiFetch('/groups'),
    getGroupById:  (id) => apiFetch(`/groups/${id}`),
    getMyGroups:   () => apiFetch('/groups/my-groups'),
    searchGroups:  (keyword) => apiFetch(`/groups/search?keyword=${encodeURIComponent(keyword)}`),
    joinGroup:     (id) => apiFetch(`/groups/${id}/join`, { method: 'POST' }),
    leaveGroup:    (id) => apiFetch(`/groups/${id}/leave`, { method: 'DELETE' }),
    updateGroup:   (id, data) => apiFetch(`/groups/${id}`, {
        method: 'PUT', body: JSON.stringify(data)
    }),
    deleteGroup:   (id) => apiFetch(`/groups/${id}`, { method: 'DELETE' }),
    removeMember:  (groupId, userId) => apiFetch(`/groups/${groupId}/members/${userId}`, {
        method: 'DELETE'
    })
};

// ── Study Sessions ──────────────────────────────────────────
const SessionAPI = {
    createSession:      (data) => apiFetch('/sessions', {
        method: 'POST', body: JSON.stringify(data)
    }),
    getSessionById:     (id) => apiFetch(`/sessions/${id}`),
    getGroupSessions:   (groupId) => apiFetch(`/sessions/group/${groupId}`),
    getUpcomingSessions:(groupId) => apiFetch(`/sessions/group/${groupId}/upcoming`),
    getMyUpcoming:      () => apiFetch('/sessions/my-upcoming'),
    updateSession:      (id, data) => apiFetch(`/sessions/${id}`, {
        method: 'PUT', body: JSON.stringify(data)
    }),
    cancelSession:      (id) => apiFetch(`/sessions/${id}/cancel`, { method: 'PATCH' }),
    completeSession:    (id) => apiFetch(`/sessions/${id}/complete`, { method: 'PATCH' })
};

// ── Notes ───────────────────────────────────────────────────
const NoteAPI = {
    uploadNote:     (data) => apiFetch('/notes', {
        method: 'POST', body: JSON.stringify(data)
    }),
    getNoteById:    (id) => apiFetch(`/notes/${id}`),
    getGroupNotes:  (groupId) => apiFetch(`/notes/group/${groupId}`),
    getMyNotes:     () => apiFetch('/notes/my-notes'),
    getPublicNotes: () => apiFetch('/notes/public'),
    searchNotes:    (keyword) => apiFetch(`/notes/search?keyword=${encodeURIComponent(keyword)}`),
    updateNote:     (id, data) => apiFetch(`/notes/${id}`, {
        method: 'PUT', body: JSON.stringify(data)
    }),
    deleteNote:     (id) => apiFetch(`/notes/${id}`, { method: 'DELETE' }),
    toggleLike:     (id) => apiFetch(`/notes/${id}/like`, { method: 'POST' }),
    toggleBookmark: (id) => apiFetch(`/notes/${id}/bookmark`, { method: 'POST' }),
    getBookmarks:   () => apiFetch('/notes/bookmarks'),
    trackDownload:  (id) => apiFetch(`/notes/${id}/download`, { method: 'POST' })
};

// ── Resources ───────────────────────────────────────────────
const ResourceAPI = {
    shareResource:   (data) => apiFetch('/resources', {
        method: 'POST', body: JSON.stringify(data)
    }),
    getResourceById: (id) => apiFetch(`/resources/${id}`),
    getGroupResources:(groupId) => apiFetch(`/resources/group/${groupId}`),
    getPublicResources:() => apiFetch('/resources/public'),
    getMyResources:  () => apiFetch('/resources/my-resources'),
    getByType:       (type) => apiFetch(`/resources/type/${type}`),
    searchResources: (keyword) => apiFetch(`/resources/search?keyword=${encodeURIComponent(keyword)}`),
    updateResource:  (id, data) => apiFetch(`/resources/${id}`, {
        method: 'PUT', body: JSON.stringify(data)
    }),
    deleteResource:  (id) => apiFetch(`/resources/${id}`, { method: 'DELETE' }),
    toggleLike:      (id) => apiFetch(`/resources/${id}/like`, { method: 'POST' }),
    toggleBookmark:  (id) => apiFetch(`/resources/${id}/bookmark`, { method: 'POST' }),
    getBookmarks:    () => apiFetch('/resources/bookmarks'),
    trackView:       (id) => apiFetch(`/resources/${id}/view`, { method: 'POST' })
};

// ── Forum ───────────────────────────────────────────────────
const ForumAPI = {
    askQuestion:        (data) => apiFetch('/forum/questions', {
        method: 'POST', body: JSON.stringify(data)
    }),
    getQuestion:        (id) => apiFetch(`/forum/questions/${id}`),
    getAllQuestions:     () => apiFetch('/forum/questions'),
    getGroupQuestions:  (groupId) => apiFetch(`/forum/questions/group/${groupId}`),
    getUnanswered:      () => apiFetch('/forum/questions/unanswered'),
    searchQuestions:    (keyword) => apiFetch(`/forum/questions/search?keyword=${encodeURIComponent(keyword)}`),
    getMyQuestions:     () => apiFetch('/forum/questions/my-questions'),
    updateQuestion:     (id, data) => apiFetch(`/forum/questions/${id}`, {
        method: 'PUT', body: JSON.stringify(data)
    }),
    deleteQuestion:     (id) => apiFetch(`/forum/questions/${id}`, { method: 'DELETE' }),
    upvoteQuestion:     (id) => apiFetch(`/forum/questions/${id}/upvote`, { method: 'POST' }),
    postAnswer:         (questionId, data) => apiFetch(`/forum/questions/${questionId}/answers`, {
        method: 'POST', body: JSON.stringify(data)
    }),
    updateAnswer:       (id, data) => apiFetch(`/forum/answers/${id}`, {
        method: 'PUT', body: JSON.stringify(data)
    }),
    deleteAnswer:       (id) => apiFetch(`/forum/answers/${id}`, { method: 'DELETE' }),
    upvoteAnswer:       (id) => apiFetch(`/forum/answers/${id}/upvote`, { method: 'POST' }),
    acceptAnswer:       (questionId, answerId) =>
        apiFetch(`/forum/questions/${questionId}/answers/${answerId}/accept`, {
            method: 'PATCH'
        }),
    commentOnQuestion:  (id, data) => apiFetch(`/forum/questions/${id}/comments`, {
        method: 'POST', body: JSON.stringify(data)
    }),
    commentOnAnswer:    (id, data) => apiFetch(`/forum/answers/${id}/comments`, {
        method: 'POST', body: JSON.stringify(data)
    })
};

// ── Progress ────────────────────────────────────────────────
const ProgressAPI = {
    logProgress:    (data) => apiFetch('/progress', {
        method: 'POST', body: JSON.stringify(data)
    }),
    getMyProgress:  () => apiFetch('/progress'),
    getThisWeek:    () => apiFetch('/progress/this-week'),
    getSummary:     () => apiFetch('/progress/summary'),
    deleteEntry:    (id) => apiFetch(`/progress/${id}`, { method: 'DELETE' })
};

// ── Notifications ───────────────────────────────────────────
const NotifAPI = {
    getAll:         () => apiFetch('/notifications'),
    getUnread:      () => apiFetch('/notifications/unread'),
    getCount:       () => apiFetch('/notifications/count'),
    markAsRead:     (id) => apiFetch(`/notifications/${id}/read`, { method: 'PATCH' }),
    markAllAsRead:  () => apiFetch('/notifications/read-all', { method: 'PATCH' }),
    delete:         (id) => apiFetch(`/notifications/${id}`, { method: 'DELETE' })
};

// ── Recommendations ─────────────────────────────────────────
const RecommendAPI = {
    getPeers: () => apiFetch('/recommendations/peers')
};
