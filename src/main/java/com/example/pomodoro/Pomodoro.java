package com.example.pomodoro;

// https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableList.html

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Pomodoro extends Application {
    Ajastin ajastin = new Ajastin();
    KurssiTarkastelu kurssi = new KurssiTarkastelu();

    Stage primaryStage;

    Button kurssinMuokkausButton = new Button("Muokkaa kursseja");
    ObservableList<String> kurssilista = FXCollections.observableList(kurssi.kurssit);
    ComboBox<String> kurssiCombobox = new ComboBox<>(kurssilista);
    String valittuKurssi = new String();

    Button takaisinButton = new Button("Lopeta");

    Label tyoaikaPrompt = new Label("Anna työaika: ");
    Label pomodoroDefaultPrompt = new Label("Anna yhden\nkierroksen kesto: ");
    Label taukoPrompt = new Label("Anna tauon kesto: ");
    Label statusPrompt = new Label();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        showPrimaryScene();
    }

    /**
     * Metodi, jolla ladataan tiedostoon lisätyt kurssit ja laitetaan ne näkyville ComboBoxiin
     * @param TIEDOSTON_NIMI KurssiTarkastelu-luokassa määritetty tiedoston nimi. Tiedostossa on käyttäjän lisäämät kurssit.
     * @param comboBox ComboBox, johon lisätään tiedoston sisältämät kurssit.
     */
    public void ladataanCombobox(String TIEDOSTON_NIMI, ComboBox<String> comboBox){
        comboBox.getItems().clear(); // tyhjennetään lista ja lisätään siihen tiedoston tiedot

        try (BufferedReader reader = new BufferedReader(new FileReader(TIEDOSTON_NIMI))) { // luetaan tiedoston rivit
            String rivi;
            while ((rivi = reader.readLine()) != null) {
                comboBox.getItems().add(rivi); // lisätään rivi ComboBoxiin
            }
        } catch (IOException e) {
            System.out.println("Virhe tiedoston lukemisessa: " + e.getMessage());
        }
    }

    /**
     * Metodi, jolla näytetään alkunäyttö, joka toimii päänäkymänä. Siis tähän näyttöön palataan useasti ohjelmassa.
     * @throws IOException - jos tapahtuu virhe metodissa ladataanCombobox()
     */
    private void showPrimaryScene() throws IOException {
        ajastin.ajastimenResetointi(); // ajastin resetoidaan, jos palataan takaisin primaryStageen
        kurssi.kurssitTiedostossa(kurssi.TIEDOSTON_NIMI); // ladataan tiedosto, jossa kurssit ovat
        kurssilista.setAll(kurssi.kurssit); // päivitetään kurssit sisältävä tiedosto

       ladataanCombobox(kurssi.TIEDOSTON_NIMI, kurssiCombobox); // lisätään ComboBoxiin aiemmin lisätyt kurssit

        // visuaaliset ominaisuudet
        HBox tyoaikaBox = new HBox(10, tyoaikaPrompt, ajastin.tyoaika);
        tyoaikaBox.setAlignment(Pos.CENTER);
        HBox pomodoroDefaultBox = new HBox(10, pomodoroDefaultPrompt, ajastin.pomodoroDefault);
        pomodoroDefaultBox.setAlignment(Pos.CENTER);
        HBox taukoBox = new HBox(10, taukoPrompt, ajastin.tauko);
        taukoBox.setAlignment(Pos.CENTER);

        ajastin.aloitaButton.setAlignment(Pos.CENTER);

        kurssiCombobox.setPromptText("Valitse kurssi"); // käyttäjälle annetaan kehote kurssin valinnasta
        HBox kurssitarkastelu = new HBox(10, kurssiCombobox, kurssinMuokkausButton);
        kurssitarkastelu.setAlignment(Pos.CENTER);
        kurssinMuokkausButton.setOnAction(e -> kurssinLisaysScene());
        VBox layout = new VBox(15, tyoaikaBox, pomodoroDefaultBox, taukoBox, ajastin.aloitaButton, kurssitarkastelu);
        layout.setAlignment(Pos.CENTER);

        Scene primaryScene = new Scene(layout, 300, 200);

        ajastin.aloitaButton.setOnAction(e -> {
            if(kurssiCombobox.getValue() == null){
                System.out.println("Valitse kurssi!");
                kurssiCombobox.setStyle("-fx-border-color: red;");
            } else {
                kurssiCombobox.setStyle("-fx-border-color: grey;");
                try {
                    valittuKurssi = kurssiCombobox.getValue();
                    countdownScene();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        primaryStage.setScene(primaryScene);
        primaryStage.setTitle("Pomodoro");
        primaryStage.show();
    }

    /**
     * Käyttäjän painaessa kurssinLisaysButtonia siirrytään näkymään, jossa käyttäjä voi kirjoittaa haluamansa kurssin nimen ja se lisätään tiedostoon.
     * Vaihtoehtoisesti käyttäjä voi poistaa kurssin.
     * Painamalla takaisin-nappia voidaan palata takaisin tekemättä muutoksia.
     * Tallenna kurssi painikkeella voidaan tallentaa uusi kurssi.
     * Poista kurssi painikkeella voidaan poistaa valittu kurssi.
     */
    private void kurssinLisaysScene() {
        // käyttäjälle annettavat kehotteet
        Label kurssinLisaysLabel = new Label("Anna kurssin nimi:");
        Label kurssinPoistoLabel = new Label("Poista kurssi: ");

        TextField kurssinLisaysField = new TextField("");
        Button tallennaButton = new Button("Tallenna kurssi");
        Button poistaButton = new Button("Poista kurssi");

        VBox tallennaLayout = new VBox(10, kurssinLisaysLabel, kurssinLisaysField, tallennaButton, takaisinButton);
        VBox poistoLayout = new VBox(10, kurssinPoistoLabel, kurssiCombobox, poistaButton);
        HBox layout = new HBox(10, tallennaLayout, poistoLayout);

        tallennaLayout.setAlignment(Pos.CENTER);
        poistoLayout.setAlignment(Pos.CENTER);
        Scene kurssinlisaysScene = new Scene(layout, 300, 200);

        tallennaButton.setOnAction(e -> { // tallennaButtonia painamalla kurssi tallennetaan
            String uusiKurssi = kurssinLisaysField.getText().trim();
            if (!uusiKurssi.isEmpty()) {
                kurssi.lisataanKurssi(uusiKurssi);
                kurssilista.setAll(kurssi.kurssit); // päivitetään näkyvä lista
                ladataanCombobox(kurssi.TIEDOSTON_NIMI, kurssiCombobox); // päivitetään ComboBox
                kurssiCombobox.setValue(uusiKurssi); // asetetaan uusi arvo ComboBoxin arvoksi
            }
            try {
                showPrimaryScene(); // tallennaButtonin painamisen jälkeen käyttäjä palaa aloitusnäkymään
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        poistaButton.setOnAction(e -> { // poistaButtonia painamalla kurssit listasta voidaan poistaa kursseja
            String poistettavaKurssi = kurssiCombobox.getValue();

            if (poistettavaKurssi == null || poistettavaKurssi.isEmpty()) {
                System.out.println("Virhe: Valitse poistettava kurssi.");
                return;
            }

            kurssi.poistetaanKurssi(poistettavaKurssi);

            // päivitetään ComboBoxin kurssit
            kurssilista.setAll(kurssi.kurssit);
            ladataanCombobox(kurssi.TIEDOSTON_NIMI, kurssiCombobox);

            System.out.println("Kurssi '" + poistettavaKurssi + "' poistettu onnistuneesti.");
        });

        takaisinButton.setOnAction(e -> { // jos käyttäjä muuttaa mielensä, eikä halua lisätä uutta kurssia, siirrytään alkunäkymään
            try {
                showPrimaryScene();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        primaryStage.setScene(kurssinlisaysScene); // muutetaan kurssin lisäämisen ajaksi primaryStage kurssinLisaysSceneksi
    }


    /**
     * Metodi, joka näyttää ajastimen.
     * @throws IOException Poikkeus ajastimen päivittämisessä.
     */
    public void countdownScene() throws IOException {
        Label pomodoroLabel = new Label();
        ajastin.statusLabel = pomodoroLabel;
        statusPrompt = ajastin.statusPrompt;

        takaisinButton.setOnAction(e -> {
            try {
                int kertynytAika = ajastin.getKertyvaAika();
                String aikaStr = ajastin.formatAika(kertynytAika);
                kurssi.paivitaOpiskeluTiedosto(valittuKurssi, aikaStr); // kirjoitetaan aika ja valittu kurssi tiedostoon

                showPrimaryScene(); // palataan takaisin alkunäkymään
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            ajastin.ajastimenResetointi(); // resetoidaan ajastin defaultarvoille
        });

        VBox layout = new VBox(15, statusPrompt, pomodoroLabel, ajastin.paussiButton, takaisinButton);
        layout.setAlignment(Pos.CENTER);
        Scene countdownScene = new Scene(layout, 300, 200);

        primaryStage.setScene(countdownScene); // asetetaan countdownScene näkymäksi
        ajastin.aloitaLaskenta(); // aloitetaan ajastimen laskenta
    }
}
