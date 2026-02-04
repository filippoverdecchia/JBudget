package it.unicam.cs.mpgc.jbudget119474.gui;

import it.unicam.cs.mpgc.jbudget119474.model.*;
import it.unicam.cs.mpgc.jbudget119474.persistence.*;
import it.unicam.cs.mpgc.jbudget119474.repository.*;
import it.unicam.cs.mpgc.jbudget119474.stats.StatisticsService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JBudgetView extends VBox {

    private final TableView<Movement> table = new TableView<>();
    private final MovementRepository movementRepository = new MovementRepositoryImpl();
    private final PersistenceManager persistenceManager = new JsonPersistence();
    private final TagTree tagTree = new TagTreeImpl("root");

    private final javafx.collections.ObservableList<Movement> tableData =
            FXCollections.observableArrayList();

    public JBudgetView() {

        setSpacing(10);
        setPadding(new Insets(10));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<Movement, String> dateColumn = new TableColumn<>("Data");
        dateColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getDate().toString()));

        TableColumn<Movement, String> descriptionColumn = new TableColumn<>("Descrizione");
        descriptionColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getDescription()));

        TableColumn<Movement, String> amountColumn = new TableColumn<>("Importo");
        amountColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(String.format("%.2f €", cell.getValue().getAmount())));

        TableColumn<Movement, String> tagColumn = new TableColumn<>("Tag");
        tagColumn.setCellValueFactory(cell -> {
            String tagsText = cell.getValue().getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(tagsText);
        });

        table.getColumns().setAll(dateColumn, descriptionColumn, amountColumn, tagColumn);

        table.setItems(tableData);
        getChildren().add(table);

        Button addButton = new Button("Aggiungi");
        Button saveButton = new Button("Salva");
        Button loadButton = new Button("Carica");
        Button totalButton = new Button("Totale");
        Button tagButton = new Button("Per Tag");
        Button scheduleButton = new Button("Scadenzario");
        Button statsButton = new Button("Statistiche");

        TextField tagField = new TextField();
        tagField.setPromptText("Inserisci tag");

        HBox buttonBar = new HBox(10);
        buttonBar.setPadding(new Insets(10));

        List<Button> buttons = List.of(
                addButton,
                saveButton,
                loadButton,
                totalButton,
                tagButton,
                scheduleButton,
                statsButton
        );

        buttons.forEach(b -> b.setPrefWidth(110));
        tagField.setPrefWidth(140);

        buttonBar.getChildren().addAll(
                addButton,
                saveButton,
                loadButton,
                totalButton,
                tagField,
                tagButton,
                scheduleButton,
                statsButton
        );

        getChildren().add(buttonBar);

        addButton.setOnAction(e -> showAddDialog());

        saveButton.setOnAction(e -> {
            try {
                persistenceManager.save(movementRepository, tagTree, "dati.json");
                showInfo("Dati salvati correttamente.");
            } catch (Exception ex) {
                showError("Errore nel salvataggio: " + ex.getMessage());
            }
        });

        loadButton.setOnAction(e -> {
            try {
                persistenceManager.load("dati.json", movementRepository, tagTree);
                refreshTable();
                showInfo("Dati caricati correttamente.");
            } catch (Exception ex) {
                showError("Errore nel caricamento: " + ex.getMessage());
            }
        });

        totalButton.setOnAction(e -> {
            refreshTable();
            double total = movementRepository.getTotalBalance();
            showInfo("Bilancio totale: " + String.format("%.2f €", total));
        });

        tagButton.setOnAction(e -> {
            refreshTable();

            String tagName = tagField.getText().trim();
            if (tagName.isEmpty()) {
                showError("Inserisci un nome di tag.");
                return;
            }

            Tag tag = tagTree.getOrCreateTag(tagName);
            double partial = movementRepository.getBalanceForTag(tag, tagTree);

            showInfo("Bilancio per il tag '" + tagName + "': " + String.format("%.2f €", partial));
        });

        scheduleButton.setOnAction(e -> showSchedule());
        statsButton.setOnAction(e -> showStatistics());

        refreshTable();
    }

    private void showAddDialog() {

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Aggiungi Movimento");

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField descriptionField = new TextField();
        TextField amountField = new TextField();
        TextField tagsField = new TextField();

        CheckBox scheduledCheckBox = new CheckBox("Movimento programmato");

        TextField monthsField = new TextField();
        monthsField.setPromptText("Numero mesi (es. 12)");
        monthsField.setDisable(true);

        scheduledCheckBox.selectedProperty().addListener((obs, o, n) -> {
            monthsField.setDisable(!n);
            if (!n) {
                monthsField.clear();
            }
        });

        content.getChildren().addAll(
                new Label("Data:"), datePicker,
                new Label("Descrizione:"), descriptionField,
                new Label("Importo (+ entrata, - uscita):"), amountField,
                new Label("Tag (separati da virgola):"), tagsField,
                scheduledCheckBox,
                new Label("Numero mesi:"), monthsField
        );

        Button confirmButton = new Button("Conferma");

        confirmButton.setOnAction(e -> {
            try {

                if (descriptionField.getText().isBlank()
                        || amountField.getText().isBlank()) {
                    showError("Inserisci descrizione e importo.");
                    return;
                }

                LocalDate date = datePicker.getValue();
                String description = descriptionField.getText();
                double amount = Double.parseDouble(amountField.getText());

                String[] insertedTags = tagsField.getText().split(",");

                Movement movement = new Movement(date, description, amount);

                for (String t : insertedTags) {
                    if (!t.isBlank()) {
                        movement.addTag(tagTree.getOrCreateTag(t.trim()));
                    }
                }

                if (scheduledCheckBox.isSelected()) {

                    if (monthsField.getText().isBlank()) {
                        showError("Inserisci il numero di mesi.");
                        return;
                    }

                    int months;

                    try {
                        months = Integer.parseInt(monthsField.getText().trim());

                        if (months <= 0) {
                            showError("Il numero di mesi deve essere maggiore di 0.");
                            return;
                        }

                    } catch (NumberFormatException ex) {
                        showError("Numero mesi non valido.");
                        return;
                    }

                    RatePlan plan = new RatePlan(date, months, amount, description);

                    for (ScheduledMovement sm : plan.getInstallments()) {
                        Movement base = sm.getBaseMovement();
                        if (base != null) {
                            for (Tag tag : movement.getTags()) {
                                base.addTag(tag);
                            }
                        }
                        movementRepository.addScheduled(sm);
                    }

                } else {
                    movementRepository.add(movement);
                }

                refreshTable();
                stage.close();

            } catch (NumberFormatException ex) {
                showError("Importo non valido.");
            } catch (Exception ex) {
                showError("Errore: " + ex.getMessage());
            }
        });

        content.getChildren().add(confirmButton);

        stage.setScene(new Scene(content, 420, 420));
        stage.showAndWait();
    }

    private void showSchedule() {

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Scadenzario");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        TableView<ScheduledMovement> scheduleTable = new TableView<>();

        TableColumn<ScheduledMovement, String> dateColumn = new TableColumn<>("Data");
        dateColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getDate() == null ? "" : cell.getValue().getDate().toString()));

        TableColumn<ScheduledMovement, String> descriptionColumn = new TableColumn<>("Descrizione");
        descriptionColumn.setCellValueFactory(cell -> {
            Movement base = cell.getValue().getBaseMovement();
            return new SimpleStringProperty(base == null ? "" : base.getDescription());
        });

        TableColumn<ScheduledMovement, String> amountColumn = new TableColumn<>("Importo");
        amountColumn.setCellValueFactory(cell -> {
            Movement base = cell.getValue().getBaseMovement();
            double amount = base == null ? 0.0 : base.getAmount();
            return new SimpleStringProperty(String.format("%.2f €", amount));
        });

        TableColumn<ScheduledMovement, String> tagColumn = new TableColumn<>("Tag");
        tagColumn.setCellValueFactory(cell -> {
            Movement base = cell.getValue().getBaseMovement();
            String tagsText = base == null ? "" : base.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(tagsText);
        });

        scheduleTable.getColumns().setAll(dateColumn, descriptionColumn, amountColumn, tagColumn);

        List<ScheduledMovement> upcoming = movementRepository.getScheduled().stream()
                .sorted(Comparator.comparing(ScheduledMovement::getDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        scheduleTable.setItems(FXCollections.observableArrayList(upcoming));

        content.getChildren().add(scheduleTable);

        stage.setScene(new Scene(content, 650, 320));
        stage.showAndWait();
    }

    private void showStatistics() {

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Statistiche");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        DatePicker start1 = new DatePicker();
        DatePicker end1 = new DatePicker();
        DatePicker start2 = new DatePicker();
        DatePicker end2 = new DatePicker();

        content.getChildren().addAll(
                new Label("Periodo 1 - Da:"), start1,
                new Label("A:"), end1,
                new Label("Periodo 2 - Da:"), start2,
                new Label("A:"), end2
        );

        Button compareButton = new Button("Confronta");

        compareButton.setOnAction(e -> {
            try {

                StatisticsService service = new StatisticsService(movementRepository.getAll());

                Map<String, Double> comparison = service.comparePeriods(
                        start1.getValue(), end1.getValue(),
                        start2.getValue(), end2.getValue()
                );

                StringBuilder sb = new StringBuilder();

                comparison.forEach((k, v) ->
                        sb.append(k)
                                .append(": ")
                                .append(String.format("%.2f €", v))
                                .append("\n")
                );

                showInfo(sb.toString());
                stage.close();

            } catch (Exception ex) {
                showError("Errore nel confronto: " + ex.getMessage());
            }
        });

        content.getChildren().add(compareButton);

        stage.setScene(new Scene(content, 400, 400));
        stage.showAndWait();
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).show();
    }

    private void showInfo(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).show();
    }

    private void refreshTable() {
        movementRepository.applyScheduledMovements(LocalDate.now());
        tableData.setAll(movementRepository.getAll());
        table.refresh();
    }
}
