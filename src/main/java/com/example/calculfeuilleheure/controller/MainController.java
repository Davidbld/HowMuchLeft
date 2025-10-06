package com.example.calculfeuilleheure.controller;

import com.example.calculfeuilleheure.model.TimesheetEntry;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.layout.Canvas;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.HashMap;

/**
 * Contrôleur principal de l'application.
 * Gère les interactions utilisateur et la logique de la feuille d'heure.
 */
public class MainController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField hoursField;

    @FXML
    private TextField endHoursField;

    @FXML
    private TableView<TimesheetEntry> tableView;

    @FXML
    private TableColumn<TimesheetEntry, String> dateColumn;

    @FXML
    private TableColumn<TimesheetEntry, String> hoursColumn;

    @FXML
    private TableColumn<TimesheetEntry, String> endHoursColumn;

    @FXML
    private TableColumn<TimesheetEntry, String> workedHoursColumn;

    @FXML
    private TableColumn<TimesheetEntry, String> totalDailyColumn;

    @FXML
    private TableColumn<TimesheetEntry, String> totalWeeklyColumn;

    private final ObservableList<TimesheetEntry> entries = FXCollections.observableArrayList();

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Caches pour optimiser les calculs
    private final Map<java.time.LocalDate, Double> dailyTotals = new HashMap<>();
    private final Map<String, Double> weeklyTotals = new HashMap<>();
    private final Map<java.time.LocalDate, Integer> dateCounts = new HashMap<>();
    private final Map<String, java.time.LocalDate> weekMaxDates = new HashMap<>();

    @FXML
    public void initialize() {
        // Initialisation des colonnes du tableau
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDate().format(dateFormatter)));
        hoursColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getHoursFormatted()));
        endHoursColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEndHoursFormatted()));
        workedHoursColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getWorkedHoursFormatted()));

        // Nouvelle colonne pour le total journalier
        totalDailyColumn.setCellValueFactory(cellData -> {
            TimesheetEntry entry = cellData.getValue();
            Integer count = dateCounts.get(entry.getDate());
            if (count != null && count > 1) {
                Double total = dailyTotals.get(entry.getDate());
                return new javafx.beans.property.SimpleStringProperty(
                        TimesheetEntry.formatTotalHours(total != null ? total : 0.0));
            } else {
                return new javafx.beans.property.SimpleStringProperty("-");
            }
        });

        // Nouvelle colonne pour le total hebdomadaire
        totalWeeklyColumn.setCellValueFactory(cellData -> {
            TimesheetEntry entry = cellData.getValue();
            String weekKey = entry.getWeekYear() + "-" + entry.getWeekNumber();
            java.time.LocalDate maxDate = weekMaxDates.get(weekKey);
            if (maxDate != null && entry.getDate().equals(maxDate)) {
                Double total = weeklyTotals.get(weekKey);
                String formatted = TimesheetEntry.formatTotalHours(total != null ? total : 0.0);
                return new javafx.beans.property.SimpleStringProperty("Semaine " + entry.getWeekNumber() + ": " + formatted);
            } else {
                return new javafx.beans.property.SimpleStringProperty("");
            }
        });

        tableView.setItems(entries);
    }

    /**
     * Met à jour les caches de totaux pour optimiser les calculs.
     */
    private void updateTotals() {
        dailyTotals.clear();
        weeklyTotals.clear();
        dateCounts.clear();
        weekMaxDates.clear();

        for (TimesheetEntry entry : entries) {
            // Comptage par date
            dateCounts.put(entry.getDate(), dateCounts.getOrDefault(entry.getDate(), 0) + 1);

            // Total quotidien
            dailyTotals.put(entry.getDate(), dailyTotals.getOrDefault(entry.getDate(), 0.0) + entry.getWorkedHours());

            // Total hebdomadaire
            String weekKey = entry.getWeekYear() + "-" + entry.getWeekNumber();
            weeklyTotals.put(weekKey, weeklyTotals.getOrDefault(weekKey, 0.0) + entry.getWorkedHours());

            // Date max par semaine
            java.time.LocalDate currentMax = weekMaxDates.get(weekKey);
            if (currentMax == null || entry.getDate().isAfter(currentMax)) {
                weekMaxDates.put(weekKey, entry.getDate());
            }
        }
    }

    /**
     * Ajoute une entrée à la feuille d'heure.
     */
    @FXML
    public void handleAddEntry() {
        if (datePicker.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner une date.");
            return;
        }
        String hoursText = hoursField.getText();
        String endHoursText = endHoursField.getText();
        double hours;
        double endHours;

        // Regex pour valider le format xxhyy (ex: 08h30)
        if (!hoursText.matches("^\\d{1,2}h\\d{2}$")) {
            showAlert("Erreur", "Veuillez entrer les heures au format 00h00 (ex: 08h30).");
            return;
        }
        if (!endHoursText.matches("^\\d{1,2}h\\d{2}$")) {
            showAlert("Erreur", "Veuillez entrer l'heure de fin au format 00h00 (ex: 17h30).");
            return;
        }

        try {
            hours = TimesheetEntry.parseHours(hoursText);
            if (hours < 0) {
                showAlert("Erreur", "Le nombre d'heures doit être positif.");
                return;
            }
            endHours = TimesheetEntry.parseHours(endHoursText);
            if (endHours < 0) {
                showAlert("Erreur", "L'heure de fin doit être positive.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Format des heures invalide.");
            return;
        }

        TimesheetEntry entry = new TimesheetEntry(datePicker.getValue(), hours, endHours);
        entries.add(entry);

        // Mettre à jour les totaux
        updateTotals();

        // Réinitialiser les champs (conserver la date)
        hoursField.clear();
        endHoursField.clear();
    }



    /**
     * Génère un fichier PDF avec les entrées de la feuille d'heure.
     */
    @FXML
    public void handleGeneratePdf() {
        if (entries.isEmpty()) {
            showAlert("Erreur", "Aucune entrée à exporter.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer la feuille d'heure en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(tableView.getScene().getWindow());

        if (file != null) {
            try {
                createPdf(file.getAbsolutePath());
                showAlert("Succès", "PDF généré avec succès.");
            } catch (FileNotFoundException e) {
                showAlert("Erreur", "Impossible de créer le fichier PDF.");
            }
        }
    }

    /**
     * Gestionnaire d'événement pour ajouter un pied de page avec numéro de page.
     */
    private static class FooterEventHandler implements IEventHandler {
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDoc = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            Rectangle pageSize = page.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
            Canvas canvas = new Canvas(pdfCanvas, pageSize);
            canvas.setFontSize(10);
            canvas.showTextAligned("Page " + pdfDoc.getPageNumber(page) + " / " + pdfDoc.getNumberOfPages(),
                    pageSize.getWidth() / 2, 20, TextAlignment.CENTER);
        }
    }

    /**
     * Crée le PDF avec les données de la feuille d'heure.
     * @param dest chemin du fichier PDF à créer
     * @throws FileNotFoundException si le fichier ne peut pas être créé
     */
    private void createPdf(String dest) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.setMargins(36, 36, 36, 36); // Marges de 36 points (0.5 pouce)

        // Ajouter le gestionnaire de pied de page
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new FooterEventHandler());

        // En-tête
        Paragraph header = new Paragraph("Feuille d'heure")
                .setBold()
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(header);

        String generatedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Paragraph subHeader = new Paragraph("Généré le " + generatedDate)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subHeader);

        // Tableau stylisé
        float[] columnWidths = {200F, 100F, 100F, 100F, 150F, 200F};
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();
        table.setMarginBottom(20);

        // En-têtes avec style
        String[] headers = {"Date", "Heures", "Heure de fin", "Heures travaillées", "Total journalier", "Total hebdomadaire"};
        for (String headerText : headers) {
            Cell headerCell = new Cell().add(new Paragraph(headerText).setBold())
                    .setBackgroundColor(new DeviceRgb(200, 200, 200))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(5);
            table.addHeaderCell(headerCell);
        }

        // Données
        for (TimesheetEntry entry : entries) {
            table.addCell(createCell(entry.getDate().format(dateFormatter)));
            table.addCell(createCell(entry.getHoursFormatted()));
            table.addCell(createCell(entry.getEndHoursFormatted()));
            table.addCell(createCell(entry.getWorkedHoursFormatted()));

            // Ajouter le total journalier dans le PDF
            Integer count = dateCounts.get(entry.getDate());
            if (count != null && count > 1) {
                Double total = dailyTotals.get(entry.getDate());
                table.addCell(createCell(TimesheetEntry.formatTotalHours(total != null ? total : 0.0)));
            } else {
                table.addCell(createCell("-"));
            }

            // Ajouter le total hebdomadaire dans le PDF
            String weekKey = entry.getWeekYear() + "-" + entry.getWeekNumber();
            java.time.LocalDate maxDate = weekMaxDates.get(weekKey);
            if (maxDate != null && entry.getDate().equals(maxDate)) {
                Double total = weeklyTotals.get(weekKey);
                String formatted = TimesheetEntry.formatTotalHours(total != null ? total : 0.0);
                table.addCell(createCell("Semaine " + entry.getWeekNumber() + ": " + formatted));
            } else {
                table.addCell(createCell(""));
            }
        }

        document.add(table);

        // Résumé des heures totales
        double totalHours = entries.stream().mapToDouble(TimesheetEntry::getWorkedHours).sum();
        Paragraph summary = new Paragraph("Total des heures travaillées : " + TimesheetEntry.formatTotalHours(totalHours))
                .setBold()
                .setFontSize(12)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(20);
        document.add(summary);

        document.close();
    }

    /**
     * Crée une cellule stylisée pour le tableau.
     * @param text texte de la cellule
     * @return cellule stylisée
     */
    private Cell createCell(String text) {
        return new Cell().add(new Paragraph(text))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(3);
    }

    /**
     * Supprime l'entrée sélectionnée après confirmation.
     */
    @FXML
    public void handleDeleteEntry() {
        TimesheetEntry selectedEntry = tableView.getSelectionModel().getSelectedItem();
        if (selectedEntry == null) {
            showAlert("Erreur", "Veuillez sélectionner une entrée à supprimer.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText("Supprimer l'entrée ?");
        confirmDialog.setContentText("Voulez-vous vraiment supprimer l'entrée du " + 
            selectedEntry.getDate().format(dateFormatter) + " ?\nCette action est irréversible.");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                entries.remove(selectedEntry);
                updateTotals();
            }
        });
    }

    /**
     * Affiche une boîte de dialogue d'alerte.
     * @param title titre de la fenêtre
     * @param message message à afficher
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
