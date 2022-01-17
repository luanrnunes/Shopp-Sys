package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	private Department entity;
	
	private DepartmentService service;
	
	private List<DataChangeListener> dataChangeListeners =  new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	/*Adiciona o evento para ser enviado ao listener da tela*/
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	/*Ao salvar, a variavel entity recebera os dados preenchidos no campo*/
	/*como manda o metodo getFormData*/
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		
		try {
		
		entity = getFormData();
		service.saveOrUpdate(entity);
		/*Metodo para notificar os listeners para refresh*/
		notifyDataChangeListeners();
		/*Fecha a janela apos salvamento dos dados*/
		Utils.currentStage(event).close();
		
	} catch (ValidationException e) {
		setErrorMessages(e.getErrors());
	}
		catch(DbException e) {
		Alerts.showAlert("Error while saving Department", null, e.getMessage(), AlertType.ERROR);
	}
		
	}
	
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
		
	}

	/*Metodo que vai pegar as informacoes do campo e inserir na base*/
	/*e retornar um novo objeto to tipo Department*/
	private Department getFormData() {
		Department obj = new Department();
		
		ValidationException exception = new ValidationException("Validation Error");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(txtName.getText());
		
		/*Se houver alguma exception no map, sera lancado aqui*/
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}
	
	private void initializeNodes () {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
	public void updateFormData() {
		
		/*Como o campo que vai receber a informacao do ID espera uma string, e necessario*/
		/*converter para string*/
		
		if (entity == null) {
			throw new IllegalStateException("Required fields are empty");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
	
	/*Set. Captura as informacoes de um Map*/
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
		
	}

}
