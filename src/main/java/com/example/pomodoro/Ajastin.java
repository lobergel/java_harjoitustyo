package com.example.pomodoro;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

class Ajastin {
    TextField pomodoroDefault = new TextField("00:30"); // default arvo yhdelle kierrokselle
    TextField tauko = new TextField("00:10"); // default arvo tauolle
    TextField tyoaika = new TextField("1min"); // default arvo tyoajalle

    Button aloitaButton = new Button("Aloita");
    Button paussiButton = new Button("Paussi");
    Button resetoiButton = new Button("Resetoi");

    public int aika = 0; // Ajastimen aika sekuntteina
    public int toistot = 0; // toistojen määrä
    public int kokoAika; // käyttäjä antaa kokoajan, jonka haluaa käyttää opiskeluun
    public int kokoTyoaika;
    public int kertyvaAika = 0; // aika, joka kasvaa yhdellä joka sekunti. Käytetään ajan kirjaamisessa tiedostoon

    public boolean kaynnissa = false; // Onko ajastin käynnissä
    public boolean paussilla = false; // Onko ajastin paussilla
    public boolean onTauko = false; // Onko tauko


    public Timeline timeline; // Ajastimen aikajana
    public Label statusLabel = new Label(); // Näyttää ajastimen tilan käyttäjälle
    public Label statusPrompt = new Label("Opiskelua: ");

    /**
     * Palauttaa ajan, joka toimii ajastimessa indeksinä.
     * @return - aika, joka alkaa käyttäjän antamasta ajasta ja pienenee yhdellä joka sekunti.
     */
    public int getAika(){
        return aika;
    }

    /**
     * Asetetaan aika.
     * @param aika
     */
    public void setAika(int aika){
        this.aika = aika;
    }

    /**
     * Palauttaa kokoAika arvon. Sisältää käyttäjän antaman työajan keston sekunteina.
     * @return kokoAika sekunteina.
     */
    public int getKokoAika(){
        return kokoAika;
    }

    /**
     * Asetetaan kokoAika sekunteina.
     * @param kokoAika
     */
    public void setKokoAika(int kokoAika){
        this.kokoAika = kokoAika;
    }

    /**
     * Palauttaa ajastimen toiminnan aikana kertyneen ajan.
     * @return kertyväAika sekunteina.
     */
    public int getKertyvaAika(){
        return kertyvaAika;
    }

    /**
     * Asetetaan kertyvaAika sekunteina.
     * @param kertyvaAika
     */
    public void setKertyvaAika(int kertyvaAika){
        this.kertyvaAika = kertyvaAika;
    }

    /**
     * Alustaa Ajastin-olion ja luo ajastimen aikajanan.
     * Aikajana määrittää ajastimen toiminnan, joka vähentää aikaa sekunnin välein.
     *
     * Asetetaan nappeihin toiminnallisuus:
     * Aloita-nappi käynnistää laskennan.
     * Paussi-nappi keskeyttää tai jatkaa ajastinta.
     * Resetoi-nappi palauttaa ajastimen alkuarvoihin.
     */
    public Ajastin() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            vahennaAikaa();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        aloitaButton.setOnAction(e -> aloitaLaskenta());
        paussiButton.setOnAction(e -> {
                    lopetaAjastin();
                    if (paussilla) {
                        paussilla = false;
                    } else {
                        paussilla = true;
                    }
                });
        resetoiButton.setOnAction(e -> ajastimenResetointi());
    }

    /**
     * kuinkaMontaToistoa() metodi laskee, kuinka moneen osaan työaika jaetaan. pomodoroDefault on työskentelyaika, jota seuraa tauko.
     * @param tyoaika käyttäjä antaa, kuinka kauan haluaa opiskella
     * @param tauko käyttäjä antaa tauon keston. Tauko seuraa pomodoroDefault:in määräämästä työajasta ja seuraa sitä (riippuen, kuinka pitkä aika opiskellaan)
     * @return kokonaisluku, jonka avulla määritetään kierrosten lukumäärä
     */
    public int kuinkaMontaToistoa(TextField tyoaika, TextField tauko) {
        int kokoTyoaika = parseAika(tyoaika.getText());
        int pomodoroDefault = parseAika(this.pomodoroDefault.getText());
        int taukoAika = parseAika(tauko.getText());
        int kierroksenKesto = pomodoroDefault + taukoAika;
        kokoAika = kokoTyoaika;

        toistot = (int) Math.ceil((double) kokoTyoaika / kierroksenKesto);

        return toistot;
    }


    /**
     * Käynnistää ajastimen toiminnan ja laskee toistojen määrän käyttäjän antamien aikojen perusteella.
     * Asettaa ajastimen aikainvälin pomodoroDefaultin mukaan ja aloittaa ajastimen laskennan.
     *
     * Jos käyttäjän syöte on virheellinen, näytetään virheilmoitus. Virheellinen syöte saadaan aikamuodosta, jota ohjelma ei tunnista.
     */
    public void aloitaLaskenta() {
        kokoTyoaika = parseAika(tyoaika.getText());
        if (!kaynnissa) {
            try {
                toistot = kuinkaMontaToistoa(tyoaika, tauko); // määritetään toistojen määrä
                aika = parseAika(pomodoroDefault.getText()); // asetetaan aikaindeksi
                statusLabel.setText(formatAika(aika)); // asetetaan aika, joka näkyy käyttäjälle

                timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> { // ajastimen aikajana, joka kutsuu vahennaAikaa() sekunnin välein
                    vahennaAikaa();
                }));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();

                kaynnissa = true;
            } catch (NumberFormatException e) {
                statusLabel.setText("Virhe: Syötä aika muodossa '1h', '5min' tai 'mm:ss'!"); // ilmoitetaan virheellisestä aikamuodosta
            }
        }
    }




    /**
     * Lopettaa ajastimen laskennan eli keskeyttää kellon.
     * Kun ajastin ei ole käynnissä, se voidaan laittaan käyntiin. Käyttäjälle annetaan kehote vaihtamalla nappiin teksti "Jatka".
     * Vastaavasti, kun ajastin on käynnissä, se voidaan paussittaa paussiButtonilla, jossa lukee kehote "Paussi".
     * */
    public void lopetaAjastin() {
        if (kaynnissa) {
            timeline.pause();
            kaynnissa = false;
            paussiButton.setText("Jatka");
            statusPrompt.setText("Ajastin paussilla");
        } else {
            timeline.play();
            kaynnissa = true;
            paussiButton.setText("Paussi");
        }
    }


    /**
     * Resetoi ajastimen. Ohjelma palauttaa default-arvot.
     */
    public void ajastimenResetointi() {
        aika = 0;
        toistot = 0;
        kertyvaAika = 0;
        onTauko = false;
        kaynnissa = false;
        paussilla = false;
        timeline.stop();

        tyoaika.setText("1min");
        pomodoroDefault.setText("00:30");
        tauko.setText("00:10");

        paussiButton.setText("Paussi");
        statusPrompt.setText("Opiskelua: ");
    }


    /**
     * Metodi, jolla vähennetään ajastimen aikaa ja kerrytetään kertyvää aikaa.
     * Kun aika menee nollaan, tarkistetaan, onko jäljellä toistoja ja vaihdetaan työ- ja taukojaksojen välillä.
     * Jos kaikki toistot on suoritettu, ajastin pysäytetään.
     */
    public void vahennaAikaa() {
        if (aika > 0) {
            kertyvaAika++;
            aika--;
            statusLabel.setText(formatAika(aika));
            kokoTyoaika--;
        } else { // jos ajastimen näyttämä indeksi menee nollaa, tarkistetaan onko jäljellä toistoja
            if (toistot > 0) { // vaihdetaan tauon ja työajan välillä
                if (onTauko) { // poistutaan edellisen toiston tauolta ja muutetaan aika ja statusLabel työajan indekseihin
                    aika = Math.min(parseAika(pomodoroDefault.getText()), kokoTyoaika);
                    statusPrompt.setText("Opiskelua: ");
                } else { // poistutaan edellisen toiston työajalta ja asetetaan aika ja statusLabel tauon indekseihin
                    aika = Math.min(parseAika(tauko.getText()), kokoTyoaika);
                    statusPrompt.setText("Tauko: ");
                }
                statusLabel.setText(formatAika(aika));
                onTauko = !onTauko; // vaihdetaan onTauko totuusarvo vastakkaiseksi, mitä se oli edellisessä silmukassa
                toistot--; // vähennetään toistojen määrää yhdellä
            } else { // ajastin menee nollaan
                timeline.stop();
                kaynnissa = false;
                statusPrompt.setText("Valmis!");
            }/*
            if (toistot == 1){
                if (onTauko){ // viimeisellä toistolla valitaan pienempi indeksi pomodoroDefaultin ja kokoAjan väliltä
                    aika = Math.min(parseAika(pomodoroDefault.getText()), kokoTyoaika);
                    statusPrompt.setText("Viimeinen kierros: ");
                } else {
                    aika = Math.min(parseAika(tauko.getText()), kokoTyoaika);
                    statusPrompt.setText("Tauko: ");
                }
            }*/
        }
    }

    /**
     * Muuttaa annetun ajan muotoon min min : sec sec.
     * Ohjelmalle voidaan antaa mm. 1h, 5min, 30:00 (30min).
     * Aika palautetaan sekunneiksi.
     * @param aikaStr metodille annetaan merkkijono numeroita.
     * @return aika sekunteina.
     * @throws NumberFormatException käyttäjä on antanut ajan muodossa, jota ohjelma ei tunnista.
     */
    public int parseAika(String aikaStr) throws NumberFormatException {
        aikaStr = aikaStr.trim().toLowerCase();

        if (aikaStr.endsWith("h")) {
            return Integer.parseInt(aikaStr.replace("h", "").trim()) * 3600;
        }

        if (aikaStr.endsWith("min")) {
            return Integer.parseInt(aikaStr.replace("min", "").trim()) * 60;
        }

        if (aikaStr.contains(":")) {
            String[] osat = aikaStr.split(":");
            if (osat.length != 2) throw new NumberFormatException("Aika annettu väärässä muodossa: " + aikaStr);
            int min = Integer.parseInt(osat[0]);
            int sec = Integer.parseInt(osat[1]);
            return min * 60 + sec;
        }
        throw new NumberFormatException("Aika annettu väärässä muodossa: " + aikaStr);
    }


    /**
     * Muutetaan aika sekunneista muotoon min min : sec sec.
     * @param sekuntti aika sekuntteina.
     * @return palauttaa ajan minuutteina ja sekuntteina ":" erottamana.
     */
    public String formatAika(int sekuntti) {
        int min = sekuntti / 60;
        int sec = sekuntti % 60;
        return String.format("%02d:%02d", min, sec);
    }
}