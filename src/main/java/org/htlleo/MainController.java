package org.htlleo;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.htlleo.chatclient.SocketListener;
import org.htlleo.models.Message;
import org.htlleo.models.MessageDistributor;
import org.htlleo.pattern.Observer;

public class MainController implements Observer, Initializable  {

    @FXML
    public TextArea txtContent;
    @FXML
    public TextArea txtBody;
    @FXML
    public TextField txtName;
    @FXML
    public TextField txtServer;
    @FXML
    public TextField txtPort;
    @FXML
    public Button btnStart;
    @FXML
    public Button btnStop;
    @FXML
    public Button btnSend;

    private UUID id;
    private SocketListener socketListener;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        id = UUID.randomUUID();

        txtContent.setDisable(false);
        txtContent.setEditable(false);

        txtBody.setDisable(false);
        txtBody.setEditable(false);

        txtServer.setText("localhost");
        txtServer.setDisable(false);

        txtPort.setText("3333");
        txtPort.setDisable(false);

        txtName.setText("");
        txtName.setDisable(false);

        btnStart.setDefaultButton(true);
        btnStart.setDisable(false);

        btnStop.setDisable(true);
        btnSend.setDisable(true);

        MessageDistributor.getInstance().addObserver(this);
    }

    @Override
    public void notify(Object sender, Object args) {
        if (args instanceof Message) {
            Platform.runLater(() -> txtContent.appendText(args + "\n"));
        }
    }

    public void onStart(ActionEvent actionEvent) {
        if (actionEvent == null)
            throw new IllegalArgumentException("actionEvent");

        if (socketListener == null) {
            int port = Integer.parseInt(txtPort.getText());

            txtContent.clear();
            try {
                Socket socket = new Socket(txtServer.getText(), port);

                socketListener = new SocketListener(socket);
                socketListener.start();

                txtServer.setDisable(true);
                txtPort.setDisable(true);
                txtName.setDisable(true);
                txtBody.setEditable(true);

                btnStart.setDisable(true);
                btnStop.setDisable(false);
                btnSend.setDisable(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onStop(ActionEvent actionEvent) {
        if (actionEvent == null)
            throw new IllegalArgumentException("actionEvent");

        if (socketListener != null) {
            Message message = new Message();

            message.setId(id);
            message.setFrom(txtName.getText());
            message.setCommand("quit");
            socketListener.writeMessage(message);

            txtPort.setDisable(false);
            txtServer.setDisable(false);
            txtName.setDisable(false);
            txtBody.setEditable(false);

            btnStart.setDisable(false);
            btnStop.setDisable(true);
            btnSend.setDisable(true);
        }
        socketListener = null;
    }

    public void onSend(ActionEvent actionEvent) {
        if (actionEvent == null)
            throw new IllegalArgumentException("actionEvent");

        if (socketListener != null) {
            Message message = new Message();

            message.setId(id);
            message.setFrom(txtName.getText());
            message.setCommand("send");
            message.setBody(txtBody.getText());
            socketListener.writeMessage(message);
            txtBody.setText("");
        }
    }
}
