package com.example.application.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.nio.file.Path;

public class PackagesForm extends FormLayout {
    TextField textField = new TextField();
    Button addPackageButton = new Button("Add");
    Button deletePackageButton = new Button("Delete");
    Path path;

    Binder<Path> binder = new BeanValidationBinder<>(Path.class);

    public void setPath(Path path) {
        this.path = path;
        binder.readBean(path);
    }

    public PackagesForm() {
        add(createEditingLayout());
    }

    private Component createEditingLayout() {
        addPackageButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deletePackageButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        addPackageButton.addClickListener(event -> fireEvent(new AddEvent(this, path)));
        deletePackageButton.addClickListener(event -> fireEvent(new DeleteEvent(this, path)));
        return new HorizontalLayout(textField, addPackageButton, deletePackageButton);
    }

    public static abstract class PackageFormEvent extends ComponentEvent<PackagesForm> {
        Path path;
        public PackageFormEvent(PackagesForm source, Path path) {
            super(source, false);
            this.path = path;
        }
        public Path getPath() {
            return this.path;
        }
    }

    public static class AddEvent extends PackageFormEvent {
        public AddEvent(PackagesForm source, Path path) {
            super(source, path);
        }
    }
    public static class DeleteEvent extends PackageFormEvent {
        public DeleteEvent(PackagesForm source, Path path) {
            super(source, path);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
