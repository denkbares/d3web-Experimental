/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knowwe.cockpit2;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author Dani
 */
public class KnowWECockpit2 extends Application {
    private boolean isRunning = false;

    @Override
    public void start(Stage primaryStage) {
        Button startstop = new Button();
        startstop.setText("Start/Stop");
        startstop.setDefaultButton(true);

        startstop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String os = System.getProperty("os.name");
                if (!os.contains("Windows")) {
                    String path = "/Volumes/Macintosh HD/Downloads/KnowWE Mystique OS 2/bin/";
                    executeMacLinux(path);


                } else {
                    executeWindows("startup.bat");
                }
            }

            private void executeMacLinux(String script) {
                try {
                    String path= "";
                    if(isRunning == false){
                         path = script + "startup.sh";
                    }
                    else{
                         path = script + "shutdown.sh";
                    }
                    System.out.println(path);
                    ProcessBuilder pb = new ProcessBuilder(path);
                    pb.directory(new File(script));
                    Process process = pb.start();
                } catch (Exception e) {
                    System.out.println(e);
                }

            }

            private void executeWindows(String script) {
                try {
                    Runtime.getRuntime().exec("cmd /c start  " + script);

                } catch (IOException ioe) {
                }
            }
        });

        Button open = new Button("Open in Browser");
        open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    openUrl("http://localhost:8080/KnowWE");
                } catch (IOException ioe) {
                } catch (URISyntaxException use) {
                }
            }

            public void openUrl(String url) throws IOException, URISyntaxException {
                getHostServices().showDocument(url);
            }
        });

        startstop.setTranslateX(-75);
        open.setTranslateX(75);
        startstop.setTranslateY(75);
        open.setTranslateY(75);

        StackPane root = new StackPane();
        root.getChildren().add(startstop);
        root.getChildren().add(open);

        Scene scene = new Scene(root, 350, 250);
        primaryStage.setMinWidth(350);


        // scene.setFill(Paint.valueOf("#ddd"));
        primaryStage.setTitle("KnowWE Cockpit");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
