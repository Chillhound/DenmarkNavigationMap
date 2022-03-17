package bfst20.presentation;


import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import bfst20.data.AddressData;
import bfst20.data.InterestPointData;
import bfst20.logic.AppController;
import bfst20.logic.FileHandler;
import bfst20.logic.entities.Address;
import bfst20.logic.entities.InterestPoint;
import bfst20.logic.misc.Vehicle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class ViewController {
    private SuggestionHandler suggestionHandlerDestination;
    private SuggestionHandler suggestionHandlerAddress;
    private SuggestionHandler suggestionHandlerSearch;
    private AppController appController;
    @FXML
    private FlowPane wayPointFlowPane;
    @FXML
    private Button searchAdressButton;
    @FXML
    private Button searchRouteButton;
    @FXML
    private TextField destinationBar;
    @FXML
    private Label mouseLocationLabel;
    @FXML
    private TextField searchAddress;
    @FXML
    public FlowPane displayPane;
    @FXML
    private TextField searchbar;
    @FXML
    private MenuItem openFile;
    @FXML
    private Slider zoomSlider;
    @FXML
    private ToggleGroup type;
    @FXML
    private Canvas canvas;
    @FXML
    private HBox hbox;

    private boolean scrollTrigger;
    private Point2D lastMouse;
    private View view;

    public ViewController() {
        appController = new AppController();
    }

    @FXML
    public void initialize() {
        view = new View(canvas);
        view.setMouseLocationView(mouseLocationLabel);
        suggestionHandlerSearch = new SuggestionHandler(appController, searchAddress, SuggestionHandler.SuggestionEvent.SEARCH);
        suggestionHandlerAddress = new SuggestionHandler(appController, searchbar, SuggestionHandler.SuggestionEvent.ADDRESS);
        suggestionHandlerDestination = new SuggestionHandler(appController, destinationBar, SuggestionHandler.SuggestionEvent.DESTINATION);

        appController.alertOK(Alert.AlertType.INFORMATION, "Starting program, press OK to continue!", true);

        loadDefault();

        setupHbox();
        setupFileHandling();
        setupZoomSlider();

        setupCanvas();
        setupSearchButton();
        setupRouteButton();
    }

    private void loadDefault(){
        File file = null;

        try {
            file = FileHandler.getResourceAsFile("samsoe.osm", appController);

        } catch (NullPointerException e) {
            appController.alertOK(Alert.AlertType.ERROR, "Error loading startup file, exiting.", true);
            System.exit(1);
        }

        try {
          appController.initialize(view, file);
        } catch (Exception e) {
            appController.alertOK(Alert.AlertType.ERROR, "Error initalizing application, exiting.", true);
            System.exit(1);
        }
    }

    private void setupRouteButton() {
        searchRouteButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if (searchbar.getText().equals("") || destinationBar.getText().equals("")) {
                    appController.alertOK(Alert.AlertType.WARNING, "Please specify search or target address", true);
                    return;
                }

                try {
                    Vehicle vehicle = Vehicle.valueOf(type.getSelectedToggle().getUserData().toString().toUpperCase());
                    view.shortestPath(searchbar.getText(), destinationBar.getText(), vehicle);

                    if (appController.fetchRouteDirections() != null) {
                        displayPane.getChildren().clear();

                        Map<String, Double> routeDirections = appController.fetchRouteDirections();
                        List<String> streetsOnRoute = new ArrayList<>(routeDirections.keySet());
                        Collections.reverse(streetsOnRoute);
                        if (appController.fetchRouteDirections().size() > 0) {
                            for (String street : streetsOnRoute) {
                                String text = street.equals("ååååå") ? "Unknown Street" : street;
                                Button route = new Button("Follow " + text + " for " + routeDirections.get(street) + " km");
                                route.setPrefWidth(375);
                                route.setPrefHeight(60);
                                route.setMouseTransparent(true);
                                route.setFocusTraversable(false);
                                Separator spacing = new Separator();
                                displayPane.getChildren().add(spacing);
                                displayPane.getChildren().add(route);
                            }
                            appController.clearRouteInfoData();
                        } else {
                            displayPane.getChildren().clear();
                            appController.alertOK(Alert.AlertType.INFORMATION, "No route(s) found!", true);
                        }

                    } else {
                        displayPane.getChildren().clear();
                        appController.alertOK(Alert.AlertType.INFORMATION, "No route(s) found!", true);
                    }
                } catch (NullPointerException e) {
                    appController.alertOK(Alert.AlertType.INFORMATION, "No route(s) found!", true);
                }
            }
        });
    }

    private void setupHbox() {
        hbox.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Do whatever you want
            canvas.setWidth((double) newVal - 400);
            view.repaint();
        });

        hbox.heightProperty().addListener((obs, oldVal, newVal) -> {
            // Do whatever you want
            canvas.setHeight((double) newVal);
            view.repaint();
        });
    }

    private void setupZoomSlider() {
        zoomSlider.setMax(126);
        zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {

                if (!scrollTrigger) {
                    double deltaValue = zoomSlider.getValue() - view.getSliderValue();
                    double factor = Math.pow(1.001, 20 * deltaValue);

                    view.zoom(factor, canvas.getWidth() / 2, canvas.getHeight() / 2, 20 * deltaValue);
                }
                view.setSliderValue(zoomSlider.getValue());
            }

        });
    }

    private void updateInterestPoints() {
        wayPointFlowPane.getChildren().clear();

        InterestPointData data = InterestPointData.getInstance();
        List<InterestPoint> interestPoints = data.getAllInterestPoints();

        for (int i = 0; i < interestPoints.size(); i++) {
            Text scoreText = new Text(i + ". Interest point");

            int s = i;

            scoreText.setFont(new Font("ARIAL", 25));
            scoreText.setStyle("-fx-font-weight: bold;");
            scoreText.setFill(Color.BLACK);

            Button button = new Button();
            button.setText("Delete");

            HBox box = new HBox(scoreText, button);
            box.setMinWidth(400);
            box.setPrefWidth(400);


            button.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    wayPointFlowPane.getChildren().remove(s);
                    interestPoints.remove(s);
                    updateInterestPoints();
                    view.repaint();
                }
            });

            wayPointFlowPane.getChildren().add(box);
        }
    }

    private void setupFileHandling() {
        openFile.setOnAction(e -> {
            try {

                File file = new FileChooser().showOpenDialog(Launcher.primaryStage);
                if (file != null) {
                    wayPointFlowPane.getChildren().clear();

                    view = new View(canvas);

                    appController.initialize(view, file);
                }
            } catch (Exception err) {
                appController.alertOK(Alert.AlertType.ERROR, "Error loading selected file, please retry with a new one.", false);
            }
        });
    }

    private void setupCanvas() {
        canvas.setOnScroll(e -> {
            double factor = Math.pow(1.001, e.getDeltaY());
            view.zoom(factor, e.getX(), e.getY(), e.getDeltaY());

            scrollTrigger = true;
            zoomSlider.setValue(view.getTimesZoomed());
            scrollTrigger = false;
        });

        canvas.setOnMousePressed(e -> {
            lastMouse = new Point2D(e.getX(), e.getY());

            if (e.isControlDown()) {
                Point2D converted = view.convertCoordinates(e.getX(), e.getY());

                InterestPointData interestPointData = InterestPointData.getInstance();
                interestPointData.saveInterestPoint(new InterestPoint((float) converted.getY(), (float) converted.getX()));
                updateInterestPoints();
            }

            view.repaint();
        });

        canvas.setOnMouseDragged(e -> {
            view.pan(e.getX() - lastMouse.getX(), e.getY() - lastMouse.getY());
            lastMouse = new Point2D(e.getX(), e.getY());
        });

        canvas.setOnMouseMoved(e -> {
            view.setMousePos(new Point2D(e.getX(), e.getY()));
            view.repaint();
        });
    }


    private void setupSearchButton() {
        searchAdressButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String searchText = searchAddress.getText();

                AddressData addressData = AddressData.getInstance();
                Address address = addressData.findAddress(searchText);

                if (address != null) {
                    view.setSearchAddress(address);
                } else {
                    appController.alertOK(Alert.AlertType.INFORMATION, "Typed address not found!", true);
                }
            }
        });

    }

    public static void main(String[] args) {
        Launcher.main(args);
    }

    public void load(ActionEvent actionEvent) throws IOException, XMLStreamException, FactoryConfigurationError {

    }

    public void save(ActionEvent actionEvent) throws IOException, XMLStreamException, FactoryConfigurationError {
        appController.generateBinary();
    }

    public void normalColorButton(ActionEvent actionEvent) {
        view.changeToColorBlindMode(false);
    }

    public void colorBlindButton(ActionEvent actionEvent) {
        view.changeToColorBlindMode(true);
    }
}
