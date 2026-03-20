import { LitElement, html, css } from 'lit';
import Quill from 'quill';

class QuillEditorField extends LitElement {
  static properties = {
    value: { type: String },
    placeholder: { type: String },
    readonly: { type: Boolean }
  };

  static styles = css`
    :host {
      display: block;
      width: 100%;
      height: 100%;
    }

    .wrapper {
      display: flex;
      flex-direction: column;
      width: 100%;
      height: 100%;
      border: 1px solid var(--lumo-contrast-20pct, #d1d5db);
      border-radius: 8px;
      overflow: hidden;
      background: white;
    }

    #editor {
      flex: 1;
      min-height: 220px;
    }
  `;

  constructor() {
    super();
    this.value = '';
    this.placeholder = 'Enter text...';
    this.readonly = false;
    this._quill = null;
    this._syncing = false;
  }

  render() {
    return html`
      <div class="wrapper">
        <div id="toolbar"></div>
        <div id="editor"></div>
      </div>
    `;
  }

  firstUpdated() {
    const toolbar = this.renderRoot.querySelector('#toolbar');
    const editor = this.renderRoot.querySelector('#editor');

    this._quill = new Quill(editor, {
      theme: 'snow',
      placeholder: this.placeholder || 'Enter text...',
      readOnly: this.readonly,
      modules: {
        toolbar: {
          container: [
            [{ header: [1, 2, 3, false] }],
            [{ font: [] }],
            [{ size: ['small', false, 'large', 'huge'] }],
            ['bold', 'italic', 'underline', 'strike'],
            [{ color: [] }, { background: [] }],
            [{ list: 'ordered' }, { list: 'bullet' }],
            [{ align: [] }],
            ['blockquote', 'code-block'],
            ['link'],
            ['clean']
          ]
        }
      }
    });

    if (this.value) {
      this._quill.root.innerHTML = this.value;
    }

    this._quill.on('text-change', () => {
      if (this._syncing) return;
      this.value = this._quill.root.innerHTML;
      this.dispatchEvent(new CustomEvent('value-changed', {
        detail: { value: this.value },
        bubbles: true,
        composed: true
      }));
    });
  }

  updated(changedProps) {
    if (!this._quill) return;

    if (changedProps.has('value')) {
      const incoming = this.value || '';
      const current = this._quill.root.innerHTML;

      if (incoming !== current) {
        this._syncing = true;
        this._quill.root.innerHTML = incoming;
        this._syncing = false;
      }
    }

    if (changedProps.has('readonly')) {
      this._quill.enable(!this.readonly);
    }
  }
}

customElements.define('quill-editor-field', QuillEditorField);
