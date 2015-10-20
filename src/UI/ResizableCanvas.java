package UI;


import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
 
/**
 * Tip 1: A canvas resizing itself to the size of
 *        the parent pane.
 */

 public class ResizableCanvas extends Canvas {
     
     
     Group group = new Group();
 
        public ResizableCanvas() {
            // Redraw canvas when size changes.
           // widthProperty().addListener(evt -> draw());
           // heightProperty().addListener(evt -> draw());
        }
 
        private void draw() {
            double width = getWidth();
            double height = getHeight();
 
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, width, height);
 
            gc.setStroke(Color.RED);
            gc.strokeLine(0, 0, width, height);
            gc.strokeLine(0, height, width, 0);
        }
 
        @Override
        public boolean isResizable() {
            return true;
        }
        
       
 
        @Override
        public double prefWidth(double height) {
            return getWidth();
        }
 
        @Override
        public double prefHeight(double width) {
            return getHeight();
        }
    
 
    public void start(Stage stage) throws Exception {

        ResizableCanvas canvas = new ResizableCanvas();
 
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(canvas);
 
        // Bind canvas size to stack pane size.
        canvas.widthProperty().bind(
                       stackPane.widthProperty());
        canvas.heightProperty().bind(
                       stackPane.heightProperty());
 
        stage.setScene(new Scene(stackPane));
        stage.setTitle("Tip 1: Resizable Canvas");
        stage.show();
    }
}


 
   