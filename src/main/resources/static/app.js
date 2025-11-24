const API = '/api';
const elements = {};

document.addEventListener('DOMContentLoaded', () => {
  elements.shelfForm = document.getElementById('shelfForm');
  elements.bookForm = document.getElementById('bookForm');
  elements.shelvesContainer = document.getElementById('shelvesContainer');
  elements.bookShelf = document.getElementById('bookShelf');
  elements.searchInput = document.getElementById('searchInput');
  elements.searchBtn = document.getElementById('searchBtn');
  elements.refreshBtn = document.getElementById('refreshBtn');
  elements.modal = document.getElementById('modal');
  elements.modalBody = document.getElementById('modalBody');
  elements.modalClose = document.getElementById('modalCloseBtn');

  elements.shelfForm.addEventListener('submit', onSaveShelf);
  document.getElementById('resetShelfBtn').addEventListener('click', resetShelfForm);
  elements.bookForm.addEventListener('submit', onSaveBook);
  document.getElementById('resetBookBtn').addEventListener('click', resetBookForm);
  elements.searchBtn.addEventListener('click', onSearch);
  elements.refreshBtn.addEventListener('click', loadAll);
  if (elements.modalClose) elements.modalClose.addEventListener('click', closeModal);
  // allow closing modal with Escape key for better UX
  document.addEventListener('keydown', (ev) => { if (ev.key === 'Escape') closeModal(); });

  loadAll();
});

async function loadAll(){
  try{
    const shelves = await fetchJson(`${API}/shelves`);
    refreshShelfSelect(shelves);
    renderShelves(shelves);
  } catch(err){
    alert('Failed to load data: ' + err.message);
    console.error(err);
  }
}

async function fetchJson(url, opts){
  const res = await fetch(url, opts);
  if (!res.ok){
    const text = await res.text();
    throw new Error(text || `${res.status} ${res.statusText}`);
  }
  return await res.json();
}

function refreshShelfSelect(shelves){
  elements.bookShelf.innerHTML = '<option value="">-- No shelf --</option>';
  shelves.forEach(s => {
    const opt = document.createElement('option');
    opt.value = s.id; opt.text = `${s.name} (${s.location || 'no loc'})`;
    elements.bookShelf.appendChild(opt);
  });
}

function renderShelves(shelves){
  elements.shelvesContainer.innerHTML = '';
  shelves.forEach(async shelf => {
    const card = document.createElement('div');
    card.className = 'card';
    card.innerHTML = `
      <h3>${escapeHtml(shelf.name)}</h3>
      <div class="meta">Location: ${escapeHtml(shelf.location || '-')} • Capacity: ${shelf.capacity}</div>
      <div id="shelf-${shelf.id}" class="book-list small">
        <div class="occupancy">Loading occupancy...</div>
        <div class="books"></div>
      </div>
      <div style="margin-top:8px; display:flex; gap:8px">
        <button onclick="editShelf(${shelf.id})">Edit</button>
        <button class="muted" onclick="deleteShelf(${shelf.id})">Delete</button>
        <button class="muted" onclick="openShelfBooks(${shelf.id})">Open</button>
      </div>
    `;
    elements.shelvesContainer.appendChild(card);
    try{
      // If the shelf object already contains books (loaded via /api/shelves with EntityGraph), use it.
      let books = Array.isArray(shelf.books) ? shelf.books : await fetchJson(`${API}/books?shelfId=${shelf.id}`);
      const container = document.getElementById(`shelf-${shelf.id}`);
      const occ = container.querySelector('.occupancy');
      const booksContainer = container.querySelector('.books');
      const count = Array.isArray(books) ? books.length : 0;
      occ.innerHTML = `<strong>Occupancy:</strong> ${count}/${shelf.capacity || 0}`;
      // Only show occupancy summary on the shelf card (e.g. "5/50").
      // Do NOT render the book list inline to keep the UI compact.
      booksContainer.innerHTML = '';
      booksContainer.style.display = 'none';
    } catch(err){
      const container = document.getElementById(`shelf-${shelf.id}`);
      const occ = container.querySelector('.occupancy');
      const booksContainer = container.querySelector('.books');
      occ.innerHTML = `<strong>Occupancy:</strong> —/ ${shelf.capacity || 0}`;
      booksContainer.innerHTML = `<div style="color:red">Failed to load books</div>`;
      console.error(err);
    }
  });
}

async function onSaveShelf(e){
  e.preventDefault();
  const id = document.getElementById('shelfId').value;
  const payload = {
    name: document.getElementById('shelfName').value.trim(),
    location: document.getElementById('shelfLocation').value.trim(),
    capacity: parseInt(document.getElementById('shelfCapacity').value, 10)
  };
  try{
    if (!payload.name) throw new Error('Shelf name required');
    if (id) {
      await fetchJson(`/api/shelves/${id}`, { method:'PUT', headers:{'Content-Type':'application/json'}, body: JSON.stringify(payload)});
      alert('Shelf updated');
    } else {
      await fetchJson(`/api/shelves`, { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(payload)});
      alert('Shelf created');
    }
    resetShelfForm();
    loadAll();
  } catch(err){ alert('Error saving shelf: ' + err.message); console.error(err); }
}

function resetShelfForm(){
  document.getElementById('shelfId').value = '';
  document.getElementById('shelfName').value = '';
  document.getElementById('shelfLocation').value = '';
  document.getElementById('shelfCapacity').value = 50;
}

async function editShelf(id){
  try{
    const shelf = await fetchJson(`/api/shelves/${id}`);
    document.getElementById('shelfId').value = shelf.id;
    document.getElementById('shelfName').value = shelf.name || '';
    document.getElementById('shelfLocation').value = shelf.location || '';
    document.getElementById('shelfCapacity').value = shelf.capacity || 50;
    window.scrollTo({top:0, behavior:'smooth'});
  } catch(err){ alert('Failed to load shelf: ' + err.message); console.error(err); }
}

async function deleteShelf(id){
  if (!confirm('Delete shelf? Books on this shelf will become unassigned (if backend supports it).')) return;
  try{
    await fetchJson(`/api/shelves/${id}`, { method:'DELETE' });
    alert('Shelf deleted');
    loadAll();
  } catch(err){ alert('Error deleting shelf: ' + err.message); console.error(err); }
}

async function onSaveBook(e){
  e.preventDefault();
  const id = document.getElementById('bookId').value;
  const payload = {
    title: document.getElementById('bookTitle').value.trim(),
    author: document.getElementById('bookAuthor').value.trim(),
    isbn: document.getElementById('bookIsbn').value.trim(),
    shelfId: (() => {
      const v = document.getElementById('bookShelf').value;
      return v ? parseInt(v) : null;
    })()
  };
  try{
    if (!payload.title) throw new Error('Book title required');
    if (id){
      await fetchJson(`/api/books/${id}`, { method:'PUT', headers:{'Content-Type':'application/json'}, body: JSON.stringify(payload) });
      alert('Book updated');
    } else {
      await fetchJson(`/api/books`, { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(payload) });
      alert('Book created');
    }
    resetBookForm();
    loadAll();
  } catch(err){ alert('Error saving book: ' + err.message); console.error(err); }
}

function resetBookForm(){
  document.getElementById('bookId').value = '';
  document.getElementById('bookTitle').value = '';
  document.getElementById('bookAuthor').value = '';
  document.getElementById('bookIsbn').value = '';
  document.getElementById('bookShelf').value = '';
}

async function editBook(id){
  try{
    const book = await fetchJson(`/api/books/${id}`);
    document.getElementById('bookId').value = book.id;
    document.getElementById('bookTitle').value = book.title || '';
    document.getElementById('bookAuthor').value = book.author || '';
    document.getElementById('bookIsbn').value = book.isbn || '';
    document.getElementById('bookShelf').value = book.shelf ? book.shelf.id : '';
    window.scrollTo({top:0, behavior:'smooth'});
  } catch(err){ alert('Failed to load book: ' + err.message); console.error(err); }
}

async function deleteBook(id){
  if (!confirm('Delete this book?')) return;
  try{
    await fetchJson(`/api/books/${id}`, { method:'DELETE' });
    alert('Book deleted');
    loadAll();
  } catch(err){ alert('Error deleting book: ' + err.message); console.error(err); }
}

async function moveBookPrompt(bookId){
  try{
    const shelves = await fetchJson(`/api/shelves`);
    let options = 'Select target shelf by id (or blank to unassign):\n\n';
    shelves.forEach(s => options += `${s.id}: ${s.name} (${s.location || '-'}) capacity ${s.capacity}\n`);
    const entry = prompt(options);
    if (entry === null) return;
    const shelfId = entry.trim() === '' ? '' : entry.trim();
    const url = `/api/books/${bookId}/move${shelfId ? '?shelfId=' + encodeURIComponent(shelfId) : '?shelfId='}`;
    const res = await fetch(url, { method:'POST' });
    if (!res.ok){
      const text = await res.text();
      throw new Error(text || `${res.status} ${res.statusText}`);
    }
    alert('Move successful');
    loadAll();
  } catch(err){ alert('Error moving book: ' + err.message); console.error(err); }
}

async function openShelfBooks(shelfId){
  try{
    const shelf = await fetchJson(`/api/shelves/${shelfId}`);
    let books = shelf.books;
    if (!books) books = await fetchJson(`/api/books?shelfId=${shelfId}`);
    let html = `<h3>${escapeHtml(shelf.name)}</h3><div class="small">Location: ${escapeHtml(shelf.location || '-')} • Capacity: ${shelf.capacity}</div><hr/>`;
    if (!books.length) html += '<div class="small">No books on this shelf.</div>';
    else {
      books.forEach(b => {
        html += `<div style="display:flex;justify-content:space-between;gap:8px;padding:6px 0">
          <div><b>${escapeHtml(b.title)}</b><div class="small">${escapeHtml(b.author || '')} • ISBN:${escapeHtml(b.isbn || '')}</div></div>
          <div style="display:flex;flex-direction:row;gap:6px;align-items:center">
            <button onclick="editBook(${b.id})">Edit</button>
            <button class="muted" onclick="moveBookPrompt(${b.id})">Move</button>
            <button class="muted" onclick="deleteBook(${b.id})">Delete</button>
          </div>
        </div>`;
      });
    }
    elements.modalBody.innerHTML = html;
    openModal();
  } catch(err){ alert('Failed to open shelf: ' + err.message); console.error(err); }
}

function openModal(){ elements.modal.classList.remove('hidden'); elements.modal.setAttribute('aria-hidden','false'); }
function closeModal(){ elements.modal.classList.add('hidden'); elements.modal.setAttribute('aria-hidden','true'); }

async function onSearch(){
  const q = elements.searchInput.value.trim();
  if (!q){ loadAll(); return; }
  try{
    const byTitle = await fetchJson(`/api/books?title=${encodeURIComponent(q)}`).catch(()=>[]);
    const byAuthor = await fetchJson(`/api/books?author=${encodeURIComponent(q)}`).catch(()=>[]);
    const byIsbn = await fetchJson(`/api/books?isbn=${encodeURIComponent(q)}`).catch(()=>[]);
    const combined = [...byTitle, ...byAuthor, ...byIsbn];
    const map = new Map();
    combined.forEach(b => map.set(b.id, b));
    const results = Array.from(map.values());
    let html = `<h3>Search results: ${results.length}</h3>`;
    if (!results.length) html += '<div class="small">No matches</div>';
    else results.forEach(b => {
      const shelfName = b.shelf && b.shelf.name ? escapeHtml(b.shelf.name) : 'Unassigned';
      const shelfLoc = b.shelf && b.shelf.location ? ` (${escapeHtml(b.shelf.location)})` : '';
      html += `<div style="display:flex;justify-content:space-between;gap:8px;padding:6px 0">
        <div>
          <b>${escapeHtml(b.title)}</b>
          <div class="small">${escapeHtml(b.author || '')} • ISBN:${escapeHtml(b.isbn || '')}</div>
          <div class="small" style="margin-top:4px;color:#666">Shelf: ${shelfName}${shelfLoc}</div>
        </div>
        <div style="display:flex;flex-direction:row;gap:6px;align-items:center">
          <button onclick="editBook(${b.id})">Edit</button>
          <button class="muted" onclick="moveBookPrompt(${b.id})">Move</button>
          <button class="muted" onclick="deleteBook(${b.id})">Delete</button>
        </div>
      </div>`;
    });
    elements.modalBody.innerHTML = html;
    openModal();
  } catch(err){ alert('Search failed: ' + err.message); console.error(err); }
}

function escapeHtml(str){
  if (!str) return '';
  return String(str).replace(/[&<>\"']/g, s => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[s]));
}
