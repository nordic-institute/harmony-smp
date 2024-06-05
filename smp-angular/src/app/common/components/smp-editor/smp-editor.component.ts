import {
  AfterViewInit,
  Component,
  ElementRef,
  forwardRef,
  Input,
  OnDestroy,
  ViewChild,
} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {EditorView} from "@codemirror/view";
import {basicSetup} from "codemirror";
import {Compartment, EditorState, Extension} from "@codemirror/state"
import {LanguageSupport} from "@codemirror/language"
import {javascript} from "@codemirror/lang-javascript"
import {xml} from "@codemirror/lang-xml"
import {html} from "@codemirror/lang-html"
import {json} from "@codemirror/lang-json"
import {ThemeService} from "../../theme-service/theme.service";
import {oneDark} from "@codemirror/theme-one-dark";

function normalizeLineEndings(str: string): string {
  if (!str) {
    return str;
  }
  return str.replace(/\r\n|\r/g, '\n');
}

@Component({
  selector: 'smp-editor',
  templateUrl: './smp-editor.component.html',
  styleUrls: ['./smp-editor.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SmpEditorComponent),
      multi: true
    }
  ]
})
export class SmpEditorComponent
  implements AfterViewInit, OnDestroy, ControlValueAccessor {
  /* class applied to the created textarea */
  @Input() className = '';
  /* name applied to the created textarea */
  @Input() name = 'smpEditor';
  @ViewChild('editorRoot') ref!: ElementRef<HTMLDivElement>;
  value = '';
  _readOnly:boolean = false;
  _mimeType: string = "text/xml";

  codeMirror: EditorView;
  readOnlyDocument = new Compartment;
  documentLanguage = new Compartment;
  themeConfig = new Compartment;

  constructor( private  themeService: ThemeService) {
  }

  ngAfterViewInit() {

    let updateListenerExtension = EditorView.updateListener.of((update) => {
      if (update.docChanged) {
        this.onChange(update.view.state.doc.toString());
      }
    });
    // configure the default extensions
    let initExtensions: Extension[] =  [
        basicSetup,
        EditorView.lineWrapping,
        updateListenerExtension,
        this.documentLanguage.of(xml()),
        this.readOnlyDocument.of(EditorState.readOnly.of(false))
      ];
    // add the dark theme extension
    if (this.themeService.currentTheme === "pink_blue-grey_theme"
      || this.themeService.currentTheme === "purple_green_theme" ) {
      initExtensions.push(this.themeConfig.of(oneDark));
    }
    this.codeMirror = new EditorView({
      doc: '',
      extensions: initExtensions,
      parent: this.ref.nativeElement
    })
  }

  @Input() set mimeType(mime: string) {
    this._mimeType = mime;
    this.codeMirror?.dispatch({
      effects: this.documentLanguage.reconfigure(this.getLanguagePack)
    })
  }

  get mimeType(): string {
    return this._mimeType;
  }

  @Input() set readOnly(readOnly: boolean) {
    this._readOnly = readOnly;
    this.codeMirror?.dispatch({
      effects: this.readOnlyDocument.reconfigure(EditorState.readOnly.of(readOnly))
    })
  }

  get readOnly(): boolean {
    return this._readOnly;
  }

  get hasFocus(): boolean {
    return this.codeMirror.hasFocus;
  }

  /**
   *  puts focus on editor instance
   */
  focus(): void {
    this.codeMirror.focus();
  }

  /**
   *  puts focus on editor instance
   */
  focusAndCursorToEnd(): void {
    this.focus();
    let length = this.codeMirror.state.doc.length
    this.codeMirror.dispatch({selection: {anchor: length, head: length}})
  }


  ngOnDestroy() {
    // is there a lighter-weight way to remove the cm instance?
    if (this.codeMirror) {
      this.codeMirror.destroy();
    }
  }

  get getLanguagePack(): LanguageSupport {
    console.log("getLanguagePack")
    //mime to codemirror language
    let langSupport = javascript();
    if (this.mimeType == "text/xml"
      || this.mimeType == "application/xml") {
      langSupport = xml();
    } else if (this.mimeType == "text/javascript") {
      langSupport = javascript();
    } else if (this.mimeType == "text/html"
      || this.mimeType == "application/xhtml+xml") {
      langSupport = html();
    } else if (this.mimeType == "application/json") {
      langSupport = json();
    } else if (this.mimeType == "text/x-properties") {
      langSupport = javascript();
    }
    return langSupport;
  }


  /** Implemented as part of ControlValueAccessor. */
  writeValue(value: string) {
    if (value === null || value === undefined) {
      return;
    }
    if (!this.codeMirror) {
      this.value = value;
      return;
    }
    const cur = this.codeMirror.state.doc.toString();

    if (value !== cur && normalizeLineEndings(cur) !== normalizeLineEndings(value)) {
      this.value = value;
      let transaction = this.codeMirror.state.update({
        changes: {
          from: 0,
          to: this.codeMirror.state.doc.length, insert: value
        }
      })
      this.codeMirror.dispatch(transaction)
    }
  }

  /** Implemented as part of ControlValueAccessor. */
  registerOnChange(fn: (value: string) => void) {
    this.onChange = fn;
  }

  /** Implemented as part of ControlValueAccessor. */
  registerOnTouched(fn: () => void) {
    this.onTouched = fn;
  }

  /** Implemented as part of ControlValueAccessor. */
  setDisabledState(isDisabled: boolean) {
    this.readOnly = isDisabled;
  }

  /** Implemented as part of ControlValueAccessor. */
  private onChange = (_: any) => {
  };
  /** Implemented as part of ControlValueAccessor. */
  private onTouched = () => {
  };
}
