package com.example.First_Java_CRUD;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("")
public class CustomerView extends VerticalLayout {

    private final CustomerRepository repository;
    private final Grid<Customer> grid = new Grid<>(Customer.class, false);

    private final TextField nameField = new TextField("Customer Name");
    private final TextField emailField = new TextField("Customer Email");

    private Customer editingCustomer;

    public CustomerView(CustomerRepository repository) {
        this.repository = repository;

        Button saveButton = new Button("Save Customer", event -> saveCustomer());

        grid.addColumn(Customer::getId).setHeader("ID");
        grid.addColumn(Customer::getName).setHeader("Name");
        grid.addColumn(Customer::getEmail).setHeader("Email");

        grid.addComponentColumn(customer ->
            new Button("Edit", event -> editCustomer(customer))
        ).setHeader("Edit");

        grid.addComponentColumn(customer ->
            new Button("Delete", event -> deleteCustomer(customer))
        ).setHeader("Delete");

        add(nameField, emailField, saveButton, grid);

        refreshGrid();
    }

    private void saveCustomer() {
        String name = nameField.getValue().trim();
        String email = emailField.getValue().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Notification.show("Please enter both name and email.");
            return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            Notification.show("Please enter a valid email address.");
            return;
        }

        if (editingCustomer == null) {
            editingCustomer = new Customer();
        }

        editingCustomer.setName(name);
        editingCustomer.setEmail(email);

        repository.save(editingCustomer);

        editingCustomer = null;
        nameField.clear();
        emailField.clear();

        refreshGrid();
        Notification.show("Customer saved successfully!");
    }

    private void editCustomer(Customer customer) {
        editingCustomer = customer;
        nameField.setValue(customer.getName());
        emailField.setValue(customer.getEmail());
        Notification.show("Edit the details, then click Save Customer.");
    }

    private void deleteCustomer(Customer customer) {
        repository.delete(customer);
        refreshGrid();
        Notification.show("Customer deleted successfully!");
    }

    private void refreshGrid() {
        grid.setItems(repository.findAll());
    }
}