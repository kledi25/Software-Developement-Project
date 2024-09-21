package com.example.sew_projekt_varianteb;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIController {
    @FXML
    private Button btnGenerate;
    @FXML
    private TableView<Song> tblSongs;
    @FXML
    private TableColumn<Song, String> colName;
    @FXML
    private TableColumn<Song, String> colArtist;
    @FXML
    private Label lblGeneratedWord;

    //Die Daten in die Tabelle initialisieren
    @FXML
    private void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
    }

    //Wenn der Button geclicked wird wird ein zufälliges Wort generiert werden
    @FXML
    private void btnGenerateOnAction() {
        String randomWord = getRandomWord();

        if (randomWord != null) {
            lblGeneratedWord.setText("Generiertes Wort: " + randomWord);
            ObservableList<Song> songs = searchAlbums(randomWord);

            if (songs != null) {
                tblSongs.setItems(songs);
            } else {
                showAlert("Error", "Kann keine Album Daten finden.");
            }
        } else {
            showAlert("Error", "Kann kein Wort generieren.");
        }
    }

    //Es ruft die API um ein zufälliges Wort abzurufen.
    private String getRandomWord() {
        try {
            URL url = new URL("https://random-word-api.vercel.app/api?words=1");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = reader.readLine();
            reader.close();

            JSONArray jsonArray = new JSONArray(line);
            return jsonArray.getString(0);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Kann kein zufalliges Wort abrufen!");
            return null;
        }
    }

    //Es bekommt nur die Alben die Ein Teil der Wort wie der generiertes Wirt haben, und gibt sie in eine Tabelle ein.
    private ObservableList<Song> searchAlbums(String keyword) {
        try {
            String apiKey = "10b24f1f8354018b75195065441a4753";
            URL url = new URL("http://ws.audioscrobbler.com/2.0/?method=album.search&album=" + keyword + "&api_key=" + apiKey + "&format=json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();

            ObservableList<Song> songs = FXCollections.observableArrayList();
            JSONObject json = new JSONObject(response.toString());

            if (json.has("results")) {
                JSONArray albumArray = json.getJSONObject("results").getJSONObject("albummatches").getJSONArray("album");

                for (int i = 0; i < albumArray.length(); i++) {
                    JSONObject albumObject = albumArray.getJSONObject(i);
                    String name = albumObject.getString("name");
                    String artist = albumObject.getString("artist");
                    songs.add(new Song(name, artist));
                }
                return songs;
            } else {
                showAlert("Error", "Keine Albumen fur dieses Wort gefunden!");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Kann die Album Daten nicht abrufen!.");
            return null;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
