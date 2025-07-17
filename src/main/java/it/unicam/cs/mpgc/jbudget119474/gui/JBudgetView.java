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
import java.util.*;

public class JBudgetView extends VBox {

    private final TableView<Movement> tabella = new TableView<>();
    private final MovementRepository archivioMovimenti = new MovementRepositoryImpl();
    private final PersistenceManager salvataggio = new JsonPersistence();
    private final TagTree alberoTag = new TagTreeImpl("root");

    public JBudgetView() {
        setSpacing(10);
        setPadding(new Insets(10));

        tabella.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<Movement, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getData().toString()));

        TableColumn<Movement, String> colDescrizione = new TableColumn<>("Descrizione");
        colDescrizione.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescrizione()));

        TableColumn<Movement, String> colImporto = new TableColumn<>("Importo");
        colImporto.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.2f €", cell.getValue().getImporto())));

        TableColumn<Movement, String> colTag = new TableColumn<>("Tag");
        colTag.setCellValueFactory(cellData -> {
            List<Tag> tags = cellData.getValue().getTag();
            String nomeTag = tags != null && !tags.isEmpty() ? tags.get(0).getName() : "";
            return new SimpleStringProperty(nomeTag);
        });
        tabella.getColumns().add(colTag);



        tabella.getColumns().addAll(colData, colDescrizione, colImporto);
        getChildren().add(tabella);

        // Bottoni
        Button btnAggiungi = new Button("Aggiungi");
        Button btnSalva = new Button("Salva");
        Button btnCarica = new Button("Carica");
        Button btnTotale = new Button("Totale");
        Button btnPerTag = new Button("Per Tag");
        Button btnScadenzario = new Button("Scadenzario");
        Button btnStatistiche = new Button("Statistiche");

        TextField campoTag = new TextField();
        campoTag.setPromptText("Inserisci tag");

        HBox barraBottoni = new HBox(10);
        barraBottoni.setPadding(new Insets(10));
        List<Button> pulsanti = List.of(btnAggiungi, btnSalva, btnCarica, btnTotale, btnPerTag, btnScadenzario, btnStatistiche);
        pulsanti.forEach(b -> b.setPrefWidth(110));
        campoTag.setPrefWidth(140);
        barraBottoni.getChildren().addAll(btnAggiungi, btnSalva, btnCarica, btnTotale, campoTag, btnPerTag, btnScadenzario, btnStatistiche);
        getChildren().add(barraBottoni);

        // Eventi
        btnAggiungi.setOnAction(e -> mostraDialogoAggiunta());
        btnSalva.setOnAction(e -> {
            try {
                salvataggio.salva(archivioMovimenti, alberoTag, "dati.json");
                mostraInfo("Dati salvati correttamente.");
            } catch (Exception ex) {
                mostraErrore("Errore nel salvataggio: " + ex.getMessage());
            }
        });
        btnCarica.setOnAction(e -> {
            try {
                salvataggio.carica("dati.json", archivioMovimenti, alberoTag);
                aggiornaTabella();
                mostraInfo("Dati caricati correttamente.");
            } catch (Exception ex) {
                mostraErrore("Errore nel caricamento: " + ex.getMessage());
            }
        });
        btnTotale.setOnAction(e -> {
            double totale = archivioMovimenti.getTotalBalance();
            mostraInfo("Bilancio totale: " + String.format("%.2f €", totale));
        });
        btnPerTag.setOnAction(e -> {
            String nomeTag = campoTag.getText().trim();
            if (nomeTag.isEmpty()) {
                mostraErrore("Inserisci un nome di tag.");
                return;
            }
            Tag t = alberoTag.getOrCreateTag(nomeTag);
            double parziale = archivioMovimenti.getBalanceForTag(t, alberoTag);
            mostraInfo("Bilancio per il tag '" + nomeTag + "': " + String.format("%.2f €", parziale));
        });
        btnScadenzario.setOnAction(e -> mostraScadenzario());
        btnStatistiche.setOnAction(e -> mostraStatistiche());

        aggiornaTabella();
    }

    private void mostraDialogoAggiunta() {
        Stage finestra = new Stage();
        finestra.initModality(Modality.APPLICATION_MODAL);
        finestra.setTitle("Aggiungi Movimento");

        VBox contenuto = new VBox(10);
        contenuto.setPadding(new Insets(15));

        DatePicker selettoreData = new DatePicker(LocalDate.now());
        TextField campoDesc = new TextField();
        TextField campoImporto = new TextField();
        TextField campoTags = new TextField();
        CheckBox chkProgrammato = new CheckBox("Movimento programmato");

        contenuto.getChildren().addAll(
                new Label("Data:"), selettoreData,
                new Label("Descrizione:"), campoDesc,
                new Label("Importo (+ entrata, - uscita):"), campoImporto,
                new Label("Tag (separati da virgola):"), campoTags,
                chkProgrammato
        );

        Button conferma = new Button("Conferma");
        conferma.setOnAction(e -> {
            try {
                if (campoDesc.getText().isBlank() || campoImporto.getText().isBlank()) {
                    mostraErrore("Inserisci descrizione e importo.");
                    return;
                }

                LocalDate data = selettoreData.getValue();
                String descrizione = campoDesc.getText();
                double importo = Double.parseDouble(campoImporto.getText());
                String[] tagInseriti = campoTags.getText().split(",");

                Movement movimento = new Movement(data, descrizione, importo);
                for (String tag : tagInseriti) {
                    if (!tag.isBlank()) {
                        movimento.aggiungiTag(alberoTag.getOrCreateTag(tag.trim()));
                    }
                }

                if (chkProgrammato.isSelected()) {
                    RatePlan piano = new RatePlan(data, 1, -importo, descrizione);
                    archivioMovimenti.addScheduled(piano.getListaRate().get(0));
                } else {
                    archivioMovimenti.add(movimento);
                }

                aggiornaTabella();
                finestra.close();
            } catch (NumberFormatException ex) {
                mostraErrore("Importo non valido.");
            } catch (Exception ex) {
                mostraErrore("Errore: " + ex.getMessage());
            }
        });

        contenuto.getChildren().add(conferma);
        finestra.setScene(new Scene(contenuto, 420, 420));
        finestra.showAndWait();
    }

    private void mostraScadenzario() {
        Stage finestra = new Stage();
        finestra.initModality(Modality.APPLICATION_MODAL);
        finestra.setTitle("Scadenzario");

        VBox contenuto = new VBox(10);
        contenuto.setPadding(new Insets(10));

        TableView<ScheduledMovement> tabellaSchedulati = new TableView<>();

        TableColumn<ScheduledMovement, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate().toString()));

        TableColumn<ScheduledMovement, String> colDescrizione = new TableColumn<>("Descrizione");
        colDescrizione.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOriginal().getDescrizione()));

        TableColumn<ScheduledMovement, String> colImporto = new TableColumn<>("Importo");
        colImporto.setCellValueFactory(cell -> new SimpleStringProperty(
                String.format("%.2f €", cell.getValue().getOriginal().getImporto()))
        );

        tabellaSchedulati.getColumns().addAll(colData, colDescrizione, colImporto);

        List<ScheduledMovement> prossimi = archivioMovimenti.getScheduled().stream()
                .sorted(Comparator.comparing(ScheduledMovement::getDate))
                .toList();

        tabellaSchedulati.setItems(FXCollections.observableArrayList(prossimi));
        contenuto.getChildren().add(tabellaSchedulati);
        finestra.setScene(new Scene(contenuto, 500, 300));
        finestra.showAndWait();
    }

    private void mostraStatistiche() {
        Stage finestra = new Stage();
        finestra.initModality(Modality.APPLICATION_MODAL);
        finestra.setTitle("Statistiche");

        VBox contenuto = new VBox(10);
        contenuto.setPadding(new Insets(10));

        DatePicker da1 = new DatePicker();
        DatePicker a1 = new DatePicker();
        DatePicker da2 = new DatePicker();
        DatePicker a2 = new DatePicker();

        contenuto.getChildren().addAll(
                new Label("Periodo 1 - Da:"), da1, new Label("A:"), a1,
                new Label("Periodo 2 - Da:"), da2, new Label("A:"), a2
        );

        Button confronta = new Button("Confronta");
        confronta.setOnAction(e -> {
            try {
                StatisticsService servizio = new StatisticsService(archivioMovimenti.getAll(), alberoTag);
                Map<String, Double> confronto = servizio.confrontaPeriodi(
                        da1.getValue(), a1.getValue(),
                        da2.getValue(), a2.getValue(),
                        alberoTag
                );

                StringBuilder sb = new StringBuilder();
                confronto.forEach((nome, valore) -> sb.append(nome).append(": ").append(String.format("%.2f €", valore)).append("\n"));

                mostraInfo(sb.toString());
                finestra.close();
            } catch (Exception ex) {
                mostraErrore("Errore nel confronto: " + ex.getMessage());
            }
        });

        contenuto.getChildren().add(confronta);
        finestra.setScene(new Scene(contenuto, 400, 400));
        finestra.showAndWait();
    }

    private void mostraErrore(String messaggio) {
        new Alert(Alert.AlertType.ERROR, messaggio).show();
    }

    private void mostraInfo(String messaggio) {
        new Alert(Alert.AlertType.INFORMATION, messaggio).show();
    }

    private void aggiornaTabella() {
        tabella.setItems(FXCollections.observableArrayList(archivioMovimenti.getAll()));
    }
}
