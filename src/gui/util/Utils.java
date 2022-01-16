package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	
	public static Stage currentStage(ActionEvent event) {
		
		/*Acessar o stage onde o meu controle de eventos esta no momento*/
		/*Por exemplo, se clico em um botao, recebo o stage daquele botao*/
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
		
	}

}
