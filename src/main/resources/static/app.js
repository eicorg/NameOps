(() => {
  const API_BASE = '/api/v1/nameops';

  // ---------- Tabs ----------
  const tabButtons = document.querySelectorAll('.tab-btn');
  const tabPanels = document.querySelectorAll('.tab-panel');

  tabButtons.forEach((btn) => {
    btn.addEventListener('click', () => {
      tabButtons.forEach((b) => {
        b.classList.remove('active');
        b.setAttribute('aria-selected', 'false');
      });
      tabPanels.forEach((p) => p.classList.remove('active'));

      btn.classList.add('active');
      btn.setAttribute('aria-selected', 'true');
      document.getElementById(`${btn.dataset.tab}-panel`).classList.add('active');
    });
  });

  // ---------- Helpers ----------
  function populateSelect(selectEl, items, { keepBlank = false } = {}) {
    const blankOption = keepBlank ? selectEl.querySelector('option[value=""]') : null;
    selectEl.innerHTML = '';
    if (blankOption) {
      selectEl.appendChild(blankOption);
    }
    items
      .slice()
      .sort((a, b) => a.abbreviation.localeCompare(b.abbreviation))
      .forEach((item) => {
        const option = document.createElement('option');
        option.value = item.abbreviation;
        option.textContent = `${item.abbreviation} — ${item.fullName}`;
        option.dataset.description = item.description || '';
        selectEl.appendChild(option);
      });
  }

  function wireHelperText(selectEl, helperEl) {
    const updateHelper = () => {
      const selected = selectEl.options[selectEl.selectedIndex];
      helperEl.textContent = selected ? (selected.dataset.description || '') : '';
    };
    selectEl.addEventListener('change', updateHelper);
    updateHelper();
  }

  function showResult(panelEl, { success, title, name, message }) {
    panelEl.hidden = false;
    panelEl.classList.remove('success', 'error');
    panelEl.classList.add(success ? 'success' : 'error');

    if (success) {
      panelEl.innerHTML = `
        <div>${title}</div>
        <div class="result-row">
          <div class="result-name">${escapeHtml(name)}</div>
          <button type="button" class="copy-btn outline">Copy</button>
        </div>
        ${message ? `<div>${escapeHtml(message)}</div>` : ''}
      `;
      const copyBtn = panelEl.querySelector('.copy-btn');
      copyBtn.addEventListener('click', () => {
        navigator.clipboard.writeText(name).then(() => {
          copyBtn.textContent = 'Copied!';
          setTimeout(() => { copyBtn.textContent = 'Copy'; }, 1500);
        });
      });
    } else {
      panelEl.innerHTML = `<div><strong>${title}</strong></div><div>${escapeHtml(message || '')}</div>`;
    }
  }

  function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
  }

  // ---------- Load repository data ----------
  async function loadRepository() {
    try {
      const res = await fetch(`${API_BASE}/repository`);
      const data = await res.json();

      populateSelect(document.getElementById('gen-area'), data.areas || []);
      populateSelect(document.getElementById('gen-device'), data.devices || []);
      populateSelect(document.getElementById('gen-specificArea'), data.specificAreas || [], { keepBlank: true });
      populateSelect(document.getElementById('gen-controller'), data.controllers || [], { keepBlank: true });
      populateSelect(document.getElementById('gen-signal'), data.signals || [], { keepBlank: true });

      wireHelperText(document.getElementById('gen-area'), document.getElementById('gen-area-help'));
      wireHelperText(document.getElementById('gen-device'), document.getElementById('gen-device-help'));
      wireHelperText(document.getElementById('gen-specificArea'), document.getElementById('gen-specificArea-help'));
      wireHelperText(document.getElementById('gen-controller'), document.getElementById('gen-controller-help'));
      wireHelperText(document.getElementById('gen-signal'), document.getElementById('gen-signal-help'));
    } catch (err) {
      console.error('Failed to load naming repository', err);
    }
  }

  // ---------- Generate form ----------
  const generateForm = document.getElementById('generate-form');
  const generateResult = document.getElementById('generate-result');

  generateForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const formData = new FormData(generateForm);
    const params = {};
    for (const [key, value] of formData.entries()) {
      if (key === 'lattice') {
        params.lattice = 'true';
      } else if (value) {
        params[key] = value;
      }
    }

    try {
      const res = await fetch(`${API_BASE}/generate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(params),
      });
      const data = await res.json();

      if (res.ok && data.status === 'success') {
        showResult(generateResult, {
          success: true,
          title: 'Generated device name:',
          name: data.generatedName,
          message: data.message,
        });
      } else {
        showResult(generateResult, {
          success: false,
          title: 'Could not generate name',
          message: data.message || 'Unknown error',
        });
      }
    } catch (err) {
      showResult(generateResult, {
        success: false,
        title: 'Request failed',
        message: err.message,
      });
    }
  });

  // ---------- Validate form ----------
  const validateForm = document.getElementById('validate-form');
  const validateResult = document.getElementById('validate-result');

  validateForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const name = document.getElementById('val-name').value.trim();

    try {
      const res = await fetch(`${API_BASE}/validate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name }),
      });
      const data = await res.json();

      showResult(validateResult, {
        success: !!data.valid,
        title: data.valid ? 'Valid device name:' : 'Invalid device name',
        name: data.name,
        message: data.message,
      });
    } catch (err) {
      showResult(validateResult, {
        success: false,
        title: 'Request failed',
        message: err.message,
      });
    }
  });

  loadRepository();
})();
