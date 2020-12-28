package main.java.sample.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import main.java.hr.java.covidportal.model.BazaPodataka;
import main.java.hr.java.covidportal.model.Zupanija;
import main.java.sample.Main;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Kontroler unosa županija
 */
public class UnosZupanijeController extends UnosController implements Initializable {

    private static List<Zupanija> listaZupanija;

    @FXML
    private TextField nazivZupanije;
    @FXML
    private TextField brStanovnikaZupanije;
    @FXML
    private TextField brZarazenihZupanije;
    @FXML
    private Label status;


    /**
     * Inicijalizira kontroler
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            listaZupanija = BazaPodataka.dohvatiSveZupanije();
        } catch (IOException | SQLException e) {
            Main.logger.error("Greška kod dohvaćanja županija iz baze podataka");
            e.printStackTrace();
        }

        prikaziStatus();
        inicijalizirajListenere();
    }


    /**
     * Dodaje novu županiju
     */
    public void dodaj() {
        String naziv = toTitleCase(nazivZupanije.getText(), "-");
        String brStanovnikaUnos = brStanovnikaZupanije.getText();
        String brZarazenihUnos = brZarazenihZupanije.getText();

        Boolean valIme = validateField(nazivZupanije, naziv);
        Boolean valBrStan = validateTextFieldNumber(brStanovnikaZupanije, brStanovnikaUnos);
        Boolean valBrojZar = validateTextFieldNumber(brZarazenihZupanije, brZarazenihUnos);

        if (!(valIme && valBrStan && valBrojZar)) {
            prikaziErrorUnosAlert("Unos županije", "Unijeli ste županiju s nedozvoljenim vrijednostima.");
            return;
        }

        Integer brStanovnika = Integer.valueOf(brStanovnikaUnos);
        Integer brZarazenih = Integer.valueOf(brZarazenihUnos);
        Zupanija novaZupanija = new Zupanija(naziv, brStanovnika, brZarazenih);

        try {
            BazaPodataka.spremiNovuZupaniju(novaZupanija);
        } catch (IOException | SQLException e) {
            Main.logger.error("Greška kod spremanja nove županije");
            e.printStackTrace();
        }

        prikaziSuccessUnosAlert(
                "Unos županije", "Županija dodana", "Unijeli ste županiju: " + novaZupanija);

        ocistiUnos();
        prikaziStatus();
    }

    /**
     * Postavlja početnu scenu
     */
    public void natragNaPocetni() {
        Main.prikaziPocetniEkran();
    }

    /**
     * Prikazuje status
     */
    public void prikaziStatus() {
        status.setText("U sustavu je trenutno " + listaZupanija.size() + " županija");
    }

    /**
     * Resetira unose za upisivanje podataka
     */
    public void ocistiUnos() {
        Arrays.asList(nazivZupanije, brStanovnikaZupanije, brZarazenihZupanije).forEach(TextInputControl::clear);
        resetIndicators();
    }

    public void resetIndicators() {
        Arrays.asList(nazivZupanije, brStanovnikaZupanije, brZarazenihZupanije).forEach(UnosController::makniErrorIndicator);
    }

    private void inicijalizirajListenere() {
        nazivZupanije.textProperty()
                .addListener((obs, oldText, newText) -> validateField(nazivZupanije, newText));
        brStanovnikaZupanije.textProperty()
                .addListener((obs, oldText, newText) -> validateTextFieldNumber(brStanovnikaZupanije, newText));
        brZarazenihZupanije.textProperty()
                .addListener((obs, oldText, newText) -> validateTextFieldNumber(brZarazenihZupanije, newText));
    }

}
