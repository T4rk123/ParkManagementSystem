package com.amusementpark.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class AboutController {
    @FXML private Label fioLabel;
    @FXML private Label groupLabel;
    @FXML private Label contactsLabel;
    @FXML private TextArea experienceText;
    @FXML private Label datesLabel;

    @FXML
    private void initialize() {
        fioLabel.setText("Буяджи Тимофей Павлович");
        groupLabel.setText("Группа: ПИ23-1");
        contactsLabel.setText("Email: 9034603@gmail.com | Тел: +7 (965) 280-52-62");
        experienceText.setText("Опыт работы с технологиями: Java (OOP, коллекции), JavaFX (GUI), Hibernate/JPA (ORM), PostgreSQL (SQL-запросы). Проект разработан для изучения full-stack Java.");
        datesLabel.setText("Начало: 01.10.2025 | Завершение: 18.12.2025");
    }
}