package com.example.pomodoro;

// lähteet:

// https://www.w3schools.com/java/java_files_read.asp
// https://www.w3schools.com/java/java_files_create.asp
// https://www.geeksforgeeks.org/list-interface-java-examples/
// https://www.geeksforgeeks.org/map-interface-java-examples/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;


class KurssiTarkastelu {

    int aika = 0; // Ajastimen antama aika, jonka indeksi lisätään tiedostoon
    String kurssiInput = "Kurssin nimi";
    public static String TIEDOSTON_NIMI = "kurssit.txt"; // jos haluaa vaihtaa tiedoston nimeä, se on tehtävä täällä
    public static String OPISKELUTUNNIT = "opiskelutunnit.txt";
    static File tiedosto = new File(TIEDOSTON_NIMI);
    List<String> kurssit = new ArrayList<>(); // lista lisätyistä kursseista

    public KurssiTarkastelu() {
        tiedosto = new File(TIEDOSTON_NIMI);
    }

    /**
     * Gettermetodi, jolla haetaan KurssiInput-merkkijono.
     * @return kurssiInput, joka sisältää kurssin tunnisteen.
     */
    public String getKurssiInput() {
        return kurssiInput;
    }

    /**
     * Gettermetodi, jolla haetaan tiedoston nimi. Tiedostossa on tallennettuna kurssien tunnisteet.
     * @return TIEDOSTON_NIMI merkkijono, joka sisältää tiedoston nimen merkkijonona.
     */
    public String getTiedostonNimi() {
        return TIEDOSTON_NIMI;
    }

    /**
     * Gettermetodi, jolla haetaan kurssilistaa.
     * @return kurssit -lista, joka sisältää tiedoston sisältämät kurssit.
     */
    public List<String> getKurssit() {
        return kurssit;
    }

    /**
     * Settermetodi. Asettaa KurssiInputin.
     * @param kurssiInput Kurssin tunniste merkkijonona.
     */
    public void setKurssiInput(String kurssiInput) {
        this.kurssiInput = kurssiInput;
    }

    /**
     * Settermetodi, jolla asetetaan tiedostolle nimi. Samalla luodaan tiedosto tällä nimellä.
     * @param tiedostonNimi tiedoston nimi merkkijonona.
     */
    public void setTiedostonNimi(String tiedostonNimi) {
        this.TIEDOSTON_NIMI = tiedostonNimi;
        this.tiedosto = new File(tiedostonNimi);
    }

    /**
     * Settermetodi, jolla asetetaan lista kursseista.
     * @param kurssit lista, joka sisältää käyttäjän antamat kurssit.
     */
    public void setKurssit(List<String> kurssit) {
        this.kurssit = new ArrayList<>(kurssit);
    }

    /**
     * Metodi, jolla haetaan kurssit tiedostosta. Metodille annetaan merkkijonona tiedoston nimi. Tiedostossa on käyttäjän tallentamat kurssit.
     * @param TIEDOSTON_NIMI Tiedoston nimi merkkijonona.
     * @return Palauttaa listan tiedoston sisältämistä kursseista.
     * @throws IOException Poikkeus tiedoston lukemisessa.
     */
    public List<String> kurssitTiedostossa(String TIEDOSTON_NIMI) throws IOException {
        kurssit.clear(); // tyhjennetään lista ja lisätään tiedoston tiedot siihen
        tiedosto = new File(TIEDOSTON_NIMI);

        try (Scanner reader = new Scanner(tiedosto)) {
            while (reader.hasNextLine()) {
                String data = reader.nextLine().trim();
                if (!data.isEmpty()) {
                    kurssit.add(data); // lisätään data listaan
                }
            }
            System.out.println("Ladatut kurssit: " + kurssit);
        } catch (FileNotFoundException e) {
            System.out.println("Tiedostoa ei löytynyt. Luodaan tiedosto '" + TIEDOSTON_NIMI + "'");
            tiedosto.createNewFile();
        }
        return kurssit;
    }

    /**
     * Metodi, jolla voidaan lisätä uusi kurssi tiedostoon
     * @param kurssiInput Kurssin nimi, lyhenne tai muu tunniste, jota käyttäjä käyttää kurssin tunnistamiseen
     */
    public void lisataanKurssi(String kurssiInput) {
        if (!kurssit.contains(kurssiInput)) { // ei sallita samaa nimeä useampaan kertaan
            kurssit.add(kurssiInput);

            try (FileWriter writer = new FileWriter(TIEDOSTON_NIMI, true)) {
                writer.write(kurssiInput + "\n");
            } catch (IOException e) {
                System.out.println("Virhe tiedoston kirjoittamisessa: " + e.getMessage());
            }
        }
    }

    /**
     * Metodi, jolla voidaan poistaa vanhentunut kurssi listasta.
     * @param kurssiInput Kurssin nimi
     */
    public void poistetaanKurssi(String kurssiInput) {
        if (kurssiInput == null || kurssiInput.isBlank()) {
            System.out.println("Anna poistettava kurssi.");
            return;
        }

        boolean poistettu = kurssit.removeIf(kurssi -> kurssi.equalsIgnoreCase(kurssiInput));

        if (poistettu) {
            System.out.println("Kurssi '" + kurssiInput + "' poistettu listasta.");
            try (FileWriter writer = new FileWriter(TIEDOSTON_NIMI)) {
                for (String kurssi : kurssit) {
                    writer.write(kurssi + "\n");
                }
            } catch (IOException e) {
                System.out.println("Virhe tiedostoa päivittäessä: " + e.getMessage());
            }
        } else {
            System.out.println("Kurssia '" + kurssiInput + "' ei löydetty.");
        }

        System.out.println("Päivitetty kurssilista sisältää: " + kurssit);
    }


    /**
     * Metodi, jolla tiedostoa päivitetään kurssilistan mukaan
     * @param TIEDOSTON_NIMI Tiedostolle annettu nimi
     * @throws IOException Poikkeus, jos tiedostoa ei löydy
     */
    public void lisataanKurssiTiedostoon(String TIEDOSTON_NIMI) throws IOException {
        try (FileWriter writer = new FileWriter(TIEDOSTON_NIMI)) {
            for (String kurssi : kurssit) {
                writer.write(kurssi + "\n");
            }
        }
    }

    /**
     * Metodi, jolla päivitetään tiedosto, joka sisältää kurssin nimen, opiskeluun käytetyn ajan ja opiskelun päivämäärän
     * @param kurssiInput Kurssin tunniste, esim. kurssin lyhenne.
     * @param aikaString Merkkijono, joka esittää opiskeluun käytetyn ajan.
     * @throws IOException Poikkeus tiedostoon kirjoittamisen epäonnistumisessa.
     */
    public void paivitaOpiskeluTiedosto(String kurssiInput, String aikaString) throws IOException {
        LocalDate pvmr = LocalDate.now();

        try (FileWriter writer = new FileWriter(OPISKELUTUNNIT, true)) {
            writer.write(kurssiInput + "\t" + aikaString + "\t" + pvmr + "\n");
        } catch (IOException e) {
            System.out.println("Virhe tiedoston kirjoittamisessa: " + e.getMessage());
        }
        System.out.println("Opiskelutunnit päivitetty.");
    }
}
