import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.media.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class BibliotecaMultimedia extends Application {

    private MediaPlayer mediaPlayer;
    private Label tituloArchivo;
    private Slider barraProgreso;
    private Label tiempoReproduccion;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Biblioteca Multimedia");

        // Barra de menú
        MenuBar menuBar = new MenuBar();
        Menu menuArchivo = new Menu("Archivo");
        Menu menuBiblioteca = new Menu("Biblioteca");
        Menu menuVer = new Menu("Ver");
        Menu menuAcerca = new Menu("Acerca");
        menuBar.getMenus().addAll(menuArchivo, menuBiblioteca, menuVer, menuAcerca);

        // Título del archivo
        tituloArchivo = new Label("Seleccione un archivo para reproducir");
        tituloArchivo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Panel lateral izquierdo (Editor de video)
        VBox editorVideo = new VBox();
        editorVideo.getChildren().add(new Label("Editor de Video"));
        editorVideo.setStyle("-fx-background-color: #ececec;");
        editorVideo.setPrefWidth(150);

        // Panel lateral derecho (Biblioteca)
        VBox biblioteca = new VBox();
        biblioteca.getChildren().add(new Label("Biblioteca"));
        biblioteca.setStyle("-fx-background-color: #ececec;");
        biblioteca.setPrefWidth(150);
        
        // Reproductor central
        StackPane reproductor = new StackPane();
        reproductor.setStyle("-fx-background-color: #000;");
        reproductor.setAlignment(Pos.CENTER);

        // Controles inferiores
        Button btnPlay = new Button("Play");
        Button btnPause = new Button("Pause");
        Button btnStop = new Button("Stop");

        barraProgreso = new Slider();
        barraProgreso.setMin(0);
        barraProgreso.setMax(1);
        barraProgreso.setValue(0);

        tiempoReproduccion = new Label("0:00 / 0:00");

        HBox controles = new HBox(10, btnPlay, btnPause, btnStop, barraProgreso, tiempoReproduccion);
        controles.setAlignment(Pos.CENTER);
        controles.setStyle("-fx-padding: 10px; -fx-background-color: #dcdcdc;");

        btnPlay.setOnAction(e -> {
            if (mediaPlayer != null) mediaPlayer.play();
        });

        btnPause.setOnAction(e -> {
            if (mediaPlayer != null) mediaPlayer.pause();
        });

        btnStop.setOnAction(e -> {
            if (mediaPlayer != null) mediaPlayer.stop();
        });

        // Cargar archivo desde la biblioteca
        biblioteca.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos Multimedia", "*.mp3", "*.mp4", "*.wav")
            );
            File archivo = fileChooser.showOpenDialog(primaryStage);
            if (archivo != null) {
                cargarArchivo(archivo, reproductor);
            }
        });

        // Diseño principal
        BorderPane root = new BorderPane();
        root.setTop(new VBox(menuBar, tituloArchivo));
        root.setLeft(editorVideo);
        root.setRight(biblioteca);
        root.setCenter(reproductor);
        root.setBottom(controles);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void cargarArchivo(File archivo, StackPane reproductor) {
        // Detener la reproducción actual si existe
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        // Configurar Media y MediaPlayer
        Media media = new Media(archivo.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        barraProgreso.setValue(0);

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            barraProgreso.setValue(newTime.toSeconds() / mediaPlayer.getTotalDuration().toSeconds());
            tiempoReproduccion.setText(
                formatTime(newTime) + " / " + formatTime(mediaPlayer.getTotalDuration())
            );
        });

        barraProgreso.setOnMouseClicked(e -> {
            if (mediaPlayer != null) {
                double newTime = barraProgreso.getValue() * mediaPlayer.getTotalDuration().toSeconds();
                mediaPlayer.seek(javafx.util.Duration.seconds(newTime));
            }
        });
        
        // Mostrar título del archivo
        tituloArchivo.setText("Reproduciendo: " + archivo.getName());

        // Mostrar contenido multimedia
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setPreserveRatio(true);
        mediaView.setFitWidth(600);
        mediaView.setFitHeight(400);

        reproductor.getChildren().clear();
        reproductor.getChildren().add(mediaView);
    }

    private String formatTime(javafx.util.Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) duration.toSeconds() % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
