import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.media.*;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
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
        editorVideo.setStyle("-fx-background-color: hsl(0, 0.00%, 100.00%);");
        editorVideo.setPrefWidth(150);

        // Panel lateral derecho (Biblioteca)
        VBox biblioteca = new VBox();
        biblioteca.getChildren().add(new Label("Biblioteca"));
        biblioteca.setStyle("-fx-background-color:hsl(0, 0.00%, 100.00%);");
        biblioteca.setPrefWidth(150);

        // ListView para mostrar los archivos multimedia
        ListView<String> listaArchivos = new ListView<>();
        listaArchivos.setMaxHeight(200);

        // Cargar los archivos de la carpeta "Reproduccion"
        cargarArchivosDeBiblioteca(listaArchivos);

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

        HBox controles = new HBox(50, btnPlay, btnPause, btnStop, barraProgreso, tiempoReproduccion);
        controles.setAlignment(Pos.CENTER);
        controles.setStyle("-fx-padding: 10px; -fx-background-color:rgb(189, 58, 58);");

        btnPlay.setOnAction(e -> {
            if (mediaPlayer != null)
                mediaPlayer.play();
        });

        btnPause.setOnAction(e -> {
            if (mediaPlayer != null)
                mediaPlayer.pause();
        });

        btnStop.setOnAction(e -> {
            if (mediaPlayer != null)
                mediaPlayer.stop();
        });

        // Acción al seleccionar un archivo de la biblioteca
        listaArchivos.setOnMouseClicked(e -> {
            String archivoSeleccionado = listaArchivos.getSelectionModel().getSelectedItem();
            if (archivoSeleccionado != null) {
                File archivo = new File("Reproduccion", archivoSeleccionado);
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

        // Crear la escena
        Scene scene = new Scene(root, 800, 600);

        // Agregar el CSS a la escena
        URL cssURL = getClass().getResource("style.css");
        if (cssURL == null) {
            System.out.println("No se encontró el archivo style.css.");
        } else {
            scene.getStylesheets().add(cssURL.toExternalForm());
        }


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
    
        // Verificar si el MediaPlayer se ha inicializado correctamente
        if (mediaPlayer == null) {
            System.out.println("Error al cargar el video.");
            return;
        }
    
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
    
        // Mostrar contenido multimedia en MediaView
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setPreserveRatio(true);
        mediaView.setFitWidth(600);
        mediaView.setFitHeight(400);
    
        reproductor.getChildren().clear();
        reproductor.getChildren().add(mediaView);
    
        // Reproducir el video
        mediaPlayer.play();
    }

    private void cargarArchivosDeBiblioteca(ListView<String> listaArchivos) {
        // Directorio de la carpeta 'Reproduccion'
        File carpetaReproduccion = new File("reproduccion");
        
        // Verificar si la carpeta existe
        if (!carpetaReproduccion.exists()) {
            System.out.println("La carpeta 'Reproduccion' no se encuentra.");
            return;
        }

        // Listar los archivos multimedia en la carpeta 'Reproduccion'
        File[] archivos = carpetaReproduccion.listFiles((dir, name) -> name.endsWith(".mp4") || name.endsWith(".mp3") || name.endsWith(".wav"));
        if (archivos != null) {
            for (File archivo : archivos) {
                listaArchivos.getItems().add(archivo.getName());
            }
        }
    }

    private String formatTime(javafx.util.Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) duration.toSeconds() % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
