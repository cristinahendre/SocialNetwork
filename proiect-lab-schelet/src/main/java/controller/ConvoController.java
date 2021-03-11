package controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.page.Page;
import socialnetwork.service.MessageService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;
import utils.events.MessageChangeEvent;
import utils.observers.Observer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static constant.PaginationConstant.PAGE_SIZE;


public class ConvoController implements Observer<MessageChangeEvent> {
    @FXML
    private TextField textFieldNume;

    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableView<Message> tableViewM;
    @FXML
    Pagination page;
    @FXML
    Pagination pageM;
    @FXML
    TextArea textMesaj;
    @FXML
    TableColumn<Utilizator,String> tableColumnNume;
    @FXML
    TableColumn<Utilizator,String> tableColumnPrenume;

    @FXML
    ProgressIndicator progres;


    @FXML
    TableColumn<Message,String> tableColumnFromM;
    @FXML
    TableColumn<Message,String> tableColumnToM;
    @FXML
    TableColumn<Message,String> tableColumnDateM;
    @FXML
    TableColumn<Message,String> tableColumnMessage;


    private UtilizatorService service;
    Predicate<Utilizator> pr;
    private PrietenieService prietenieService;
    private MessageService messageService;
    Stage dialogStage;
    Utilizator curent;
    Utilizator u2;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    ObservableList<Message> modelM = FXCollections.observableArrayList();

    private int dataSize=10;
    private static final int rows=5;
    String numeFurnizat;


    @FXML
    private void initialize() {
        tableColumnNume.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
        tableColumnPrenume.setCellValueFactory(new PropertyValueFactory<>("LastName"));


     //   textMesaj.setPromptText("Scrieti un mesaj..");



    }

    @FXML
    private void init() {

        page.setPageFactory(this::createPage);
    }

    private Node createPage(int pageIndex) {

        Page<Utilizator> users =service.getAllOnPage(pageIndex,curent.getId(),"");

        model.setAll( StreamSupport.stream(users.getContent().spliterator(), false) .collect(Collectors.toList()));
        //    modelMesaj.setAll(messages.getContent());

        int size;
        if((users.getTotalCount()%PAGE_SIZE) ==0)
            size= users.getTotalCount()/PAGE_SIZE;
        else size=users.getTotalCount()/PAGE_SIZE+1;
        page.setPageCount(size);
        tableView.setItems(model);
        return tableView;

    }



    private Node createPageFiltered(int pageIndex) {


        Page<Utilizator> finalPa= service.getAllOnPage(pageIndex, curent.getId(),numeFurnizat);
        List<Utilizator> lista =new ArrayList<Utilizator>();
        finalPa.getContent().forEach(lista::add);

        List<Utilizator> finalPage =lista.stream().filter(pr)
                .collect(Collectors.toList());
        finalPage.forEach(System.out::println);
        model.setAll(finalPage);

        int size;
        if ((finalPage.size() % PAGE_SIZE) == 0)
            size = finalPage.size() / PAGE_SIZE;
        else size = finalPage.size() / PAGE_SIZE + 1;
        page.setPageCount(size);
        tableView.setItems(model);

        return tableView;

    }





    public void setService(UtilizatorService service,  Stage stage, Utilizator m, PrietenieService prietenieService,
                           MessageService messageService)
                           {
        this.service = service;
        this.messageService=messageService;
        this.dialogStage=stage;
        this.curent=m;
        this.prietenieService=prietenieService;
        dataSize=service.nrE();
        textFieldNume.setPromptText("Tastati un nume de utilizator..");
        init();


    }

    private Node createPageMesajeCu2(int pageIndex) {


        Page<Message> messages =messageService.getMessagesW2(pageIndex, curent.getId(), u2.getId());
        modelM.setAll( StreamSupport.stream(messages.getContent().spliterator(), false) .collect(Collectors.toList()));

        int size;
        if((messages.getTotalCount()%PAGE_SIZE) ==0)
            size= messages.getTotalCount()/PAGE_SIZE;
        else size=messages.getTotalCount()/PAGE_SIZE+1;
        pageM.setPageCount(size);
        tableViewM.setItems(modelM);
        return tableViewM;


    }

    @FXML
    public void handlePick(){
        try{
            u2=tableView.getSelectionModel().getSelectedItem();

            try {
                // create a new stage for the popup dialog.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/messageSender.fxml"));

                AnchorPane root =  loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Vizualizare Mesaje");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                //dialogStage.initOwner(primaryStage);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                MessageSender ctr = loader.getController();
                ctr.setService(messageService, dialogStage, curent,service,u2);

                dialogStage.show();
               // dialogStage.showAndWait();


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        catch(NullPointerException e){
            MessageAlert.showErrorMessage(null, "Selectati un utilizator din tabel");
        }
        catch(ValidationException e){
            MessageAlert.showErrorMessage(null, e.toString());
        }

    }


    @FXML
    public void handleCancel(){
        dialogStage.close();
    }



    public void sendMessage(ActionEvent event) {
        try{
            progres.setVisible(false);
            Utilizator[] users= tableView.getSelectionModel().getSelectedItems().toArray(new Utilizator[0]);
            if(users.length==0){
                        MessageAlert.showErrorMessage(null,"Nu ati selectat destinatarii. Alegeti din tabela.");
                        return ;
                    }
            if(textMesaj.getText().isEmpty()){
                        MessageAlert.showErrorMessage(null, "Introduceti un mesaj in casuta.");
                        return ;
                    }
            List<Long> uti= new ArrayList<>();
            for(Utilizator m: users) {
                        uti.add(m.getId());

                    }
            messageService.addMessage(curent.getId(),uti, textMesaj.getText());

            handlePick();
            init();
            progres.setVisible(false);
            textMesaj.clear();
            textMesaj.setPromptText("Scrieti un mesaj..");


        }
        catch (NullPointerException e){
            MessageAlert.showErrorMessage(null, "Introduceti un mesaj in casuta.");
            return;
        }
    }


    @Override
    public void update(MessageChangeEvent messageChangeEvent) {
        handlePick();
    }

    public void generareConvo(ActionEvent event) {
        try{
            u2=tableView.getSelectionModel().getSelectedItem();

            try {
                // create a new stage for the popup dialog.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/sendtomoreView.fxml"));

                AnchorPane root =  loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Trimite Mesaje");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                //dialogStage.initOwner(primaryStage);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                SendMoreController ctr = loader.getController();
                ctr.setService(service, dialogStage,curent,prietenieService,messageService);

                dialogStage.show();
                // dialogStage.showAndWait();


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        catch(NullPointerException e){
            MessageAlert.showErrorMessage(null, "Selectati un utilizator din tabel");
        }
        catch(ValidationException e){
            MessageAlert.showErrorMessage(null, e.toString());
        }


    }

    public void searchUser(KeyEvent keyEvent) {
        String nume = textFieldNume.getText();


        if(nume.equals(""))
            init();
        else {
            numeFurnizat = nume;


            Predicate<Utilizator> Pprenume = x -> x.getFirstName().startsWith(nume);
            Predicate<Utilizator> Pnume = x -> x.getLastName().startsWith(nume);
            pr = Pprenume.or(Pnume);
            page.setPageFactory(this::createPageFiltered);
        }

    }
}

