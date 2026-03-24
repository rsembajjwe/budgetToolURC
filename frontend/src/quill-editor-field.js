import Quill from 'quill';
import 'quill/dist/quill.snow.css';

class QuillEditorField extends HTMLElement {
  static get observedAttributes() {
    return ['value', 'placeholder', 'readonly'];
  }

  constructor() {
    super();
    this._quill = null;
    this._syncingFromServer = false;
    this._value = '';
  }

  connectedCallback() {
    if (this._quill) return;

    this.style.display = 'block';
    this.style.width = '100%';
    if (!this.style.height) {
      this.style.height = '320px';
    }

    this.innerHTML = `
      <div style="
        display:flex;
        flex-direction:column;
        width:100%;
        height:100%;
        border:1px solid var(--lumo-contrast-20pct, #d1d5db);
        border-radius:8px;
        overflow:hidden;
        background:white;
      ">
        <div id="toolbar">
          <span class="ql-formats">
            <select class="ql-header">
              <option selected></option>
              <option value="1"></option>
              <option value="2"></option>
              <option value="3"></option>
            </select>
          </span>

          <span class="ql-formats">
            <select class="ql-font"></select>
            <select class="ql-size"></select>
          </span>

          <span class="ql-formats">
            <button class="ql-bold"></button>
            <button class="ql-italic"></button>
            <button class="ql-underline"></button>
            <button class="ql-strike"></button>
          </span>

          <span class="ql-formats">
            <select class="ql-color"></select>
            <select class="ql-background"></select>
          </span>

          <span class="ql-formats">
            <button class="ql-list" value="ordered"></button>
            <button class="ql-list" value="bullet"></button>
            <select class="ql-align"></select>
          </span>

          <span class="ql-formats">
            <button class="ql-blockquote"></button>
            <button class="ql-code-block"></button>
            <button class="ql-link"></button>
          </span>

          <span class="ql-formats">
            <button class="ql-clean"></button>
          </span>
        </div>
        <div id="editor" style="flex:1; min-height:220px;"></div>
      </div>
    `;

    const toolbar = this.querySelector('#toolbar');
    const editor = this.querySelector('#editor');

    this._quill = new Quill(editor, {
      theme: 'snow',
      placeholder: this.getAttribute('placeholder') || 'Enter text...',
      readOnly: this.hasAttribute('readonly') && this.getAttribute('readonly') !== 'false',
      modules: {
        toolbar: toolbar
      }
    });

    const initialValue = this.getAttribute('value') || '';
    this._value = initialValue;
    if (initialValue) {
      this._quill.root.innerHTML = initialValue;
    }

    this._quill.on('text-change', () => {
      if (this._syncingFromServer) return;

      const html = this._normalizeHtml(this._quill.root.innerHTML);
      this._value = html;

      // update attribute for Vaadin property listener visibility
      if (this.getAttribute('value') !== html) {
        this.setAttribute('value', html);
      }

      this.dispatchEvent(new CustomEvent('value-changed', {
        detail: { value: html },
        bubbles: true,
        composed: true
      }));
    });
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if (oldValue === newValue) return;

    if (name === 'value') {
      this._value = newValue || '';

      if (this._quill) {
        const current = this._normalizeHtml(this._quill.root.innerHTML);
        const incoming = this._value;

        if (current !== incoming) {
          this._syncingFromServer = true;
          this._quill.root.innerHTML = incoming;
          this._syncingFromServer = false;
        }
      }
    }

    if (name === 'readonly' && this._quill) {
      const readOnly = newValue !== null && newValue !== 'false';
      this._quill.enable(!readOnly);
    }
  }

  get value() {
    return this._value || '';
  }

  set value(val) {
    const normalized = val || '';
    this._value = normalized;

    if (this.getAttribute('value') !== normalized) {
      this.setAttribute('value', normalized);
    }
  }

  _normalizeHtml(html) {
    if (!html) return '';
    const trimmed = html.trim();
    return trimmed === '<p><br></p>' ? '' : trimmed;
  }
}

customElements.define('quill-editor-field', QuillEditorField);
