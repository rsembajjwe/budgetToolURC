package com.methaltech.application.views.budget.Component;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

@Tag("quill-editor-field")
@NpmPackage(value = "quill", version = "2.0.3")
@JsModule("./src/quill-editor-field.js")
public class QuillEditorField extends AbstractField<QuillEditorField, String> implements HasSize {

    public QuillEditorField() {
        super("");

        getElement().addPropertyChangeListener("value", "value-changed", event -> {
            String value = getElement().getProperty("value", "");
            setModelValue(value, true);
        });

        setWidth("100%");
        setHeight("320px");
    }

    @Override
    protected void setPresentationValue(String value) {
        getElement().setProperty("value", value == null ? "" : value);
    }

    public void setPlaceholder(String placeholder) {
        getElement().setProperty("placeholder", placeholder == null ? "" : placeholder);
    }

    public void setEditorReadOnly(boolean readOnly) {
        getElement().setProperty("readonly", readOnly);
    }
}
