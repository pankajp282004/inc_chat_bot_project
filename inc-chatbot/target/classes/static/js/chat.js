/* =============================================
   INC Chatbot – Chat/Search JavaScript
   Handles: API search, message rendering,
            result cards, keyboard events
   ============================================= */

const SEARCH_URL = '/api/search';

/* ── Utilities ── */
function now() {
    return new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

function escHtml(str) {
    if (!str) return '';
    return str
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

/* ── Scroll chat to bottom ── */
function scrollBottom() {
    const box = document.getElementById('chatMessages');
    if (box) box.scrollTop = box.scrollHeight;
}

/* ── Append a message bubble ── */
function appendMessage(html, type = 'bot') {
    const box = document.getElementById('chatMessages');
    if (!box) return;
    const wrapper = document.createElement('div');
    wrapper.className = 'message ' + (type === 'user' ? 'user-message' : 'bot-message');
    wrapper.innerHTML = html;
    box.appendChild(wrapper);
    scrollBottom();
}

/* ── User bubble ── */
function addUserMessage(text) {
    appendMessage(`
        <div class="message-avatar"><i class="fas fa-user"></i></div>
        <div class="message-content">
            <div class="message-bubble">${escHtml(text)}</div>
            <span class="message-time">${now()}</span>
        </div>
    `, 'user');
}

/* ── Typing indicator ── */
function addTypingIndicator() {
    const box = document.getElementById('chatMessages');
    if (!box) return null;
    const div = document.createElement('div');
    div.className = 'message bot-message';
    div.id = 'typingIndicator';
    div.innerHTML = `
        <div class="message-avatar"><i class="fas fa-robot"></i></div>
        <div class="message-content">
            <div class="message-bubble">
                <div class="typing-indicator">
                    <div class="typing-dot"></div>
                    <div class="typing-dot"></div>
                    <div class="typing-dot"></div>
                </div>
            </div>
        </div>`;
    box.appendChild(div);
    scrollBottom();
    return div;
}

function removeTypingIndicator() {
    const el = document.getElementById('typingIndicator');
    if (el) el.remove();
}

/* ── Status badge HTML ── */
function statusBadge(status) {
    if (!status) return '';
    const cls = 'status-' + status.toLowerCase();
    return `<span class="status-badge ${cls}">${escHtml(status)}</span>`;
}

/* ── Build a result card HTML ── */
function buildResultCard(r) {
    const matchCls = r.matchReason === 'EXACT' ? 'match-exact' : 'match-keyword';
    const approved = r.isApproved
        ? '<span style="color:#4ade80;font-size:12px"><i class="fas fa-check-circle"></i> Approved</span>'
        : '<span style="color:#fbbf24;font-size:12px"><i class="fas fa-clock"></i> Pending</span>';

    let rcaSection = '';
    if (r.rca) {
        rcaSection = `
            <div class="result-section">
                <div class="result-section-title"><i class="fas fa-search"></i> Root Cause</div>
                <div class="result-section-body">${escHtml(r.rca)}</div>
            </div>`;
    }

    let solutionSection = '';
    const solutionText = r.solution || r.planSolution;
    if (solutionText) {
        solutionSection = `
            <div class="result-section">
                <div class="result-section-title"><i class="fas fa-wrench"></i> Solution / Steps</div>
                <div class="result-section-body">${escHtml(solutionText)}</div>
            </div>`;
    }

    const appBadge = r.app ? `<span class="app-badge">${escHtml(r.app)}</span>` : '';
    const taskNo   = r.taskNo ? `<span style="color:#94a3b8;font-size:12px">Task: ${escHtml(r.taskNo)}</span>` : '';

    return `
    <div class="result-card">
        <div class="result-card-header">
            <span class="result-inc-no"><i class="fas fa-ticket-alt"></i> ${escHtml(r.incidentNo)}</span>
            <div class="result-badges">
                ${appBadge}
                ${statusBadge(r.status)}
                <span class="${matchCls}">${escHtml(r.matchReason)}</span>
                ${approved}
            </div>
        </div>
        ${taskNo}
        <p class="result-desc">${escHtml(r.description || '')}</p>
        ${rcaSection}
        ${solutionSection}
        <a href="/incidents/${r.id}" class="result-link">
            <i class="fas fa-external-link-alt"></i> View full details
        </a>
    </div>`;
}

/* ── No results bubble ── */
function addNoResultsMessage(query) {
    const html = `
        <div class="message-avatar"><i class="fas fa-robot"></i></div>
        <div class="message-content">
            <div class="message-bubble">
                <p>😕 No incidents found matching <strong>"${escHtml(query)}"</strong>.</p>
                <p style="margin-top:8px;color:#94a3b8;font-size:13px">
                    Try different keywords, an INC number, or remove the app filter.
                </p>
                <button onclick="document.getElementById('newIncidentModal') && (document.getElementById('newIncidentModal').style.display='flex')"
                        style="margin-top:12px;background:#3b82f6;border:none;color:white;padding:8px 16px;border-radius:8px;cursor:pointer;font-size:13px">
                    <i class="fas fa-plus"></i> Document new incident
                </button>
            </div>
            <span class="message-time">${now()}</span>
        </div>`;
    appendMessage(html, 'bot');
}

/* ── Main send function ── */
async function sendChatMessage() {
    const input   = document.getElementById('chatInput');
    const appSel  = document.getElementById('appFilter');
    const sendBtn = document.getElementById('sendBtn');

    if (!input) return;
    const query = input.value.trim();
    if (!query) return;

    // Disable input
    input.disabled = true;
    if (sendBtn) sendBtn.disabled = true;

    addUserMessage(query);
    input.value = '';

    const typing = addTypingIndicator();

    try {
        const body = {
            query: query,
            app: appSel ? appSel.value : '',
            page: 0,
            size: 5
        };

        const resp = await fetch(SEARCH_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: JSON.stringify(body)
        });

        removeTypingIndicator();

        if (!resp.ok) {
            throw new Error('Search failed: ' + resp.status);
        }

        const data = await resp.json();

        if (!data.results || data.results.length === 0) {
            addNoResultsMessage(query);
        } else {
            const count = data.totalResults || data.results.length;
            const appNote = appSel && appSel.value ? ` in <strong>${escHtml(appSel.value)}</strong>` : '';
            let cardsHtml = data.results.map(buildResultCard).join('');

            const hasMore = data.hasMore
                ? `<p style="margin-top:12px;font-size:13px;color:#64748b">
                       Showing top ${data.results.length} of ${count} results.
                       <a href="/search?q=${encodeURIComponent(query)}&app=${encodeURIComponent(appSel ? appSel.value : '')}"
                          style="color:#3b82f6">View all →</a>
                   </p>` : '';

            const botHtml = `
                <div class="message-avatar"><i class="fas fa-robot"></i></div>
                <div class="message-content" style="max-width:95%">
                    <div class="message-bubble">
                        <p>Found <strong>${count}</strong> incident(s)${appNote} for <em>"${escHtml(query)}"</em>:</p>
                        ${cardsHtml}
                        ${hasMore}
                    </div>
                    <span class="message-time">${now()}</span>
                </div>`;
            appendMessage(botHtml, 'bot');
        }
    } catch (err) {
        removeTypingIndicator();
        console.error('Search error:', err);
        const errHtml = `
            <div class="message-avatar"><i class="fas fa-robot"></i></div>
            <div class="message-content">
                <div class="message-bubble" style="border-color:#ef4444">
                    <p style="color:#fca5a5"><i class="fas fa-exclamation-triangle"></i>
                    Search error. Please check your connection and try again.</p>
                </div>
                <span class="message-time">${now()}</span>
            </div>`;
        appendMessage(errHtml, 'bot');
    } finally {
        input.disabled = false;
        if (sendBtn) sendBtn.disabled = false;
        input.focus();
    }
}

/* ── Set query from quick-action buttons ── */
function setQuery(text) {
    const input = document.getElementById('chatInput');
    if (input) {
        input.value = text;
        sendChatMessage();
    }
}

/* ── Keyboard: Enter to send ── */
document.addEventListener('DOMContentLoaded', function () {
    const input = document.getElementById('chatInput');
    if (input) {
        input.addEventListener('keypress', function (e) {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendChatMessage();
            }
        });
        input.focus();
    }
});
