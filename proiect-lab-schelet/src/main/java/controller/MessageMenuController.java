package controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.MessageService;
import socialnetwork.service.UtilizatorService;


import java.util.ArrayList;
import java.util.List;


public class MessageMenuController {

    private MessageService messageService;
    private UtilizatorService service;
    Stage dialogStage;
    Utilizator curent;

    @FXML
    TextField textFieldMesaj;
    @FXML
    Pagination page;
    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableColumn<Utilizator, String> tableColumnNume;
    @FXML
    TableColumn<Utilizator, String> tableColumnPrenume;

    private int dataSize=13;
    private static final int rows=5;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();



    @FXML
    private void initialize() {
        tableColumnNume.setCellValueFactory(new PropertyValueFactory<>("LastName"));
        tableColumnPrenume.setCellValueFactory(new PropertyValueFactory<>("FirstName"));

        page.setPageCount(3);

    }

    public void initModel(){
        Iterable<Utilizator> messageIterable = service.getAll();
        List<Utilizator> list = new ArrayList<>();
        for(Utilizator m : messageIterable){
           if(!m.equals(curent)) list.add(m);
        }

        model.setAll(list);
        int size;
        if(model.size()%5 ==0) size= model.size()/5;
        else size= (model.size()/5)+1;
        page.setPageCount(size);

        page.setPageFactory(this::createPage);
    }

    private Node createPage(int pageIndex) {

        int from = pageIndex * rows;
        int to = Math.min(from + rows, model.size());
        tableView.setItems(model);
        tableView.setItems(FXCollections.observableArrayList(model.subList(from, to)));
        return tableView;

    }

    public void setService(MessageService messageService,UtilizatorService service, Stage stage,Utilizator curent)
    {
        this.service = service;
        this.curent=curent;
        this.messageService=messageService;
        this.dialogStage=stage;
        dataSize=service.nrE();
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        initModel();

    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }

    public void sendMesaj(ActionEvent event) {
        try{

            Utilizator[] users= tableView.getSelectionModel().getSelectedItems().toArray(new Utilizator[0]);
            if(users.length==0){
                MessageAlert.showErrorMessage(null,"Nu ati selectat destinatarii. Alegeti din tabela.");
                return;
            }
            if(textFieldMesaj.getText().isEmpty()){
                MessageAlert.showErrorMessage(null, "Introduceti un mesaj in casuta.");
                return;
            }
            List<Long> uti= new ArrayList<>();
            for(Utilizator u: users) uti.add(u.getId());
            messageService.addMessage(curent.getId(),uti, textFieldMesaj.getText());
            handleCancel();
        }
        catch (NullPointerException e){
            MessageAlert.showErrorMessage(null, "Introduceti un mesaj in casuta.");
            return;
        }
    }
}
