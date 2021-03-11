package controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.repository.page.Page;
import socialnetwork.service.MessageService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static constant.PaginationConstant.PAGE_SIZE;


public class SendMoreController {
    @FXML
    private TextField textFieldNume;

    @FXML
    TableView<Utilizator> tableView;

    @FXML
    Pagination page;

    @FXML
    TextArea textMesaj;
    @FXML
    TableColumn<Utilizator,String> tableColumnNume;
    @FXML
    TableColumn<Utilizator,String> tableColumnPrenume;

    private UtilizatorService service;
    Predicate<Utilizator> pr;
    private PrietenieService prietenieService;
    private MessageService messageService;
    Stage dialogStage;
    Utilizator curent;
    Utilizator u2;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    private int dataSize=10;
    private static final int rows=5;
    String numeFurnizat;


    @FXML
    private void initialize() {
        tableColumnNume.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
        tableColumnPrenume.setCellValueFactory(new PropertyValueFactory<>("LastName"));


        textMesaj.setPromptText("Scrieti un mesaj..");
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);




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





    @FXML
    public void handleCancel(){
        dialogStage.close();
    }



    public void handleSend(ActionEvent event) {
        try{
            Utilizator[] users= tableView.getSelectionModel().getSelectedItems().toArray(new Utilizator[0]);
            if(users.length==0){
                MessageAlert.showErrorMessage(null,"Nu ati selectat destinatarii. Alegeti din tabela.");
                return ;
            }
            if(textMesaj.getText().isEmpty()){
                MessageAlert.showErrorMessage(null, "Introduceti un mesaj in casuta.");
                return ;
            }

            for(Utilizator m: users) {
                List<Long> uti= new ArrayList<>();
                uti.add(m.getId());
                messageService.addMessage(curent.getId(),uti, textMesaj.getText());


            }

            handlePick();
            init();
            textMesaj.clear();
            textMesaj.setPromptText("Scrieti un mesaj..");


        }
        catch (NullPointerException e){
            MessageAlert.showErrorMessage(null, "Introduceti un mesaj in casuta.");
            return;
        }
    }



    public void handlePick() {
        String nume = textFieldNume.getText();

        if(nume.equals(""))
            init();
        else {
            System.out.println(nume);
            numeFurnizat = nume;


            Predicate<Utilizator> Pprenume = x -> x.getFirstName().startsWith(nume);
            Predicate<Utilizator> Pnume = x -> x.getLastName().startsWith(nume);
            pr = Pprenume.or(Pnume);
            page.setPageFactory(this::createPageFiltered);
        }
    }



}

