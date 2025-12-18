package com.amusementpark.controllers;

import com.amusementpark.models.Attraction;
import com.amusementpark.models.User;
import com.amusementpark.repositories.AttractionRepository;
import com.amusementpark.services.StatisticsService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainController {
    @FXML private TableView<Attraction> tableView;
    @FXML private TableColumn<Attraction, Long> idColumn;
    @FXML private TableColumn<Attraction, String> nameColumn;
    @FXML private TableColumn<Attraction, String> descColumn;
    @FXML private TableColumn<Attraction, Double> priceColumn;
    @FXML private TableColumn<Attraction, Integer> waitColumn;
    @FXML private TableColumn<Attraction, Integer> capColumn;

    @FXML private TextField searchField;

    @FXML private Label userCountLabel;
    @FXML private Label avgWaitLabel;
    @FXML private BarChart<String, Number> statsChart;


    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;


    @FXML private MenuButton userMenuButton;


    @FXML private ComboBox<String> sortStatsCombo;

    private AttractionRepository repo = new AttractionRepository();
    private StatisticsService stats = new StatisticsService();
    private User currentUser;
    private ObservableList<Attraction> attractionsData = FXCollections.observableArrayList();
    private String currentSortOrder = "ASC";

    @FXML
    private void initialize() {
        initTable();
        initSortStats();
        loadData();
        loadStats();
    }

    public void setUser(User user) {
        this.currentUser = user;
        userMenuButton.setText(user.getUsername());

        userMenuButton.getItems().clear();
        MenuItem logoutItem = new MenuItem("Выход");
        logoutItem.setOnAction(e -> handleLogout());
        userMenuButton.getItems().add(logoutItem);


        if ("USER".equals(user.getRole())) {
            if (addButton != null) addButton.setVisible(false);
            if (editButton != null) editButton.setVisible(false);
            if (deleteButton != null) deleteButton.setVisible(false);
        }
    }

    private void initTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        waitColumn.setCellValueFactory(new PropertyValueFactory<>("waitingTime"));
        capColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        tableView.setItems(attractionsData);

    }

    private void initSortStats() {
        sortStatsCombo.getItems().addAll("По возрастанию", "По убыванию");
        sortStatsCombo.setValue("По возрастанию");
    }

    @FXML
    private void handleSortStats() {
        String selected = sortStatsCombo.getValue();
        currentSortOrder = "По возрастанию".equals(selected) ? "ASC" : "DESC";
        loadStats();
    }

    @FXML
    private void loadData() {
        try {
            List<Attraction> list = repo.findAll();
            attractionsData.setAll(list);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка загрузки", "Не удалось загрузить данные: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        try {
            List<Attraction> list = repo.searchByName(query);
            attractionsData.setAll(list);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка поиска", "Не удалось выполнить поиск: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        if (!"ADMIN".equals(currentUser.getRole())) return;

        Optional<Attraction> result = showAttractionDialog(null, "Добавить аттракцион");
        if (result.isPresent()) {
            try {
                repo.save(result.get());
                loadData();
                loadStats();
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Аттракцион добавлен!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Ошибка сохранения", "Не удалось сохранить аттракцион: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleEdit() {
        if (!"ADMIN".equals(currentUser.getRole())) return;
        Attraction selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Выберите аттракцион для редактирования!");
            return;
        }

        Optional<Attraction> result = showAttractionDialog(selected, "Редактировать аттракцион");
        if (result.isPresent()) {
            try {
                repo.save(result.get());
                loadData();
                loadStats();
                showAlert(Alert.AlertType.INFORMATION, "Успех", "Аттракцион обновлен!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Ошибка обновления", "Не удалось обновить аттракцион: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (!"ADMIN".equals(currentUser.getRole())) return;
        Attraction selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Удаление");
            confirm.setHeaderText("Удалить аттракцион '" + selected.getName() + "'?");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    repo.delete(selected.getId());
                    loadData();
                    loadStats();
                    showAlert(Alert.AlertType.INFORMATION, "Успех", "Аттракцион удален!");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Ошибка удаления", "Не удалось удалить аттракцион: " + e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Выберите аттракцион!");
        }
    }

    private Optional<Attraction> showAttractionDialog(Attraction existing, String title) {
        Dialog<Attraction> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Заполните данные аттракциона");

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));


        TextField nameField = new TextField();
        nameField.setPromptText("Название (обязательно)");
        TextArea descField = new TextArea();
        descField.setPromptText("Описание");
        descField.setPrefRowCount(3);
        TextField priceField = new TextField();
        priceField.setPromptText("Цена (руб., >=0, пример: 500.00)");
        TextField waitField = new TextField();
        waitField.setPromptText("Время ожидания (мин., >=0, пример: 0)");
        TextField capField = new TextField();
        capField.setPromptText("Вместимость (>=1, пример: 20)");

        grid.add(new Label("Название:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Описание:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Цена:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Время ожидания:"), 0, 3);
        grid.add(waitField, 1, 3);
        grid.add(new Label("Вместимость:"), 0, 4);
        grid.add(capField, 1, 4);

        dialog.getDialogPane().setContent(grid);


        if (existing != null) {
            nameField.setText(existing.getName() != null ? existing.getName() : "");
            descField.setText(existing.getDescription() != null ? existing.getDescription() : "");
            priceField.setText(existing.getPrice() != null ? existing.getPrice().toString() : "");
            waitField.setText(existing.getWaitingTime() != null ? existing.getWaitingTime().toString() : "");
            capField.setText(existing.getCapacity() != null ? existing.getCapacity().toString() : "");
        }


        dialog.setResultConverter(new Callback<ButtonType, Attraction>() {
            @Override
            public Attraction call(ButtonType buttonType) {
                if (buttonType == okButton) {
                    String name = nameField.getText().trim();
                    String desc = descField.getText();
                    String priceStr = priceField.getText().trim();
                    String waitStr = waitField.getText().trim();
                    String capStr = capField.getText().trim();


                    if (!validateInputs(name, priceStr, waitStr, capStr)) {
                        return null;
                    }

                    try {
                        Double price = Double.parseDouble(priceStr);
                        Integer wait = Integer.parseInt(waitStr);
                        Integer cap = Integer.parseInt(capStr);

                        if (price < 0) {
                            showAlert(Alert.AlertType.ERROR, "Ошибка валидации", "Цена не может быть отрицательной!");
                            return null;
                        }
                        if (wait < 0) {
                            showAlert(Alert.AlertType.ERROR, "Ошибка валидации", "Время ожидания не может быть отрицательным!");
                            return null;
                        }
                        if (cap < 1) {
                            showAlert(Alert.AlertType.ERROR, "Ошибка валидации", "Вместимость минимум 1 человек!");
                            return null;
                        }

                        Attraction attr;
                        if (existing == null) {
                            attr = new Attraction(name, desc, price, wait, cap);
                        } else {
                            existing.setName(name);
                            existing.setDescription(desc);
                            existing.setPrice(price);
                            existing.setWaitingTime(wait);
                            existing.setCapacity(cap);
                            attr = existing;
                        }
                        return attr;
                    } catch (NumberFormatException e) {
                        showAlert(Alert.AlertType.ERROR, "Ошибка ввода", "Введите целое число для времени ожидания или вместимости! Для цены — число (пример: 500.00 или 0).");
                        return null;
                    }
                }
                return null;
            }
        });

        return dialog.showAndWait();
    }

    private boolean validateInputs(String name, String priceStr, String waitStr, String capStr) {
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Ошибка валидации", "Название обязательно!");
            return false;
        }
        if (priceStr.isEmpty() || !priceStr.matches("\\d+(\\.\\d+)?")) {
            showAlert(Alert.AlertType.ERROR, "Ошибка валидации", "Цена: число >=0 (пример: 500.00 или 0 для бесплатного)!");
            return false;
        }
        if (waitStr.isEmpty() || !waitStr.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Ошибка валидации", "Время ожидания: неотрицательное целое >=0 (пример: 0)!");
            return false;
        }
        if (capStr.isEmpty() || !capStr.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Ошибка валидации", "Вместимость: неотрицательное целое >=1 (пример: 20)!");
            return false;
        }
        return true;
    }

    private void loadStats() {
        try {
            userCountLabel.setText("Количество пользователей: " + stats.getUserCount());




            Double avgWait = stats.getAverageWaitingTime();
            double roundedAvg = avgWait != null ? Math.round(avgWait * 10.0) / 10.0 : 0.0;
            avgWaitLabel.setText("Среднее время ожидания: ~" + roundedAvg + " мин");


            statsChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Время ожидания");
            boolean ascending = "ASC".equals(currentSortOrder);
            List<Attraction> allAttractions = repo.findSorted("waitingTime", ascending);
            List<String> categoryNames = allAttractions.stream().map(Attraction::getName).collect(Collectors.toList());
            CategoryAxis xAxis = (CategoryAxis) statsChart.getXAxis();
            xAxis.getCategories().clear();
            xAxis.setCategories(FXCollections.observableArrayList(categoryNames));
            for (Attraction a : allAttractions) {
                Integer waitTime = a.getWaitingTime() != null ? a.getWaitingTime() : 0;
                series.getData().add(new XYChart.Data<>(a.getName(), waitTime));
            }
            statsChart.getData().add(series);
            xAxis.requestLayout();
            statsChart.layout();
            Platform.runLater(() -> statsChart.requestLayout());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка статистики", "Не удалось загрузить статистику: " + e.getMessage());
        }
    }

    @FXML
    private void handleAbout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/about.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Об авторе");
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть 'Об авторе': " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Выход");
        confirmAlert.setHeaderText("Вы уверены, что хотите выйти из аккаунта?");
        confirmAlert.setContentText("Вы вернетесь на экран входа.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            currentUser = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) tableView.getScene().getWindow();
                stage.setScene(new Scene(root, 800, 600));
                stage.setTitle("Информационно-справочная система парка развлечений");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Ошибка при выходе", "Не удалось выйти: " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}