package controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ServiceException;
import socialnetwork.repository.page.Page;
import socialnetwork.service.MessageService;
import socialnetwork.service.UtilizatorService;
import utils.events.MessageChangeEvent;
import utils.observers.Observer;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static constant.PaginationConstant.PAGE_SIZE;


public class MessageSender implements Observer<MessageChangeEvent> {

    @FXML
    Label textNume;
    @FXML
    TableView<Message> tableView;
    @FXML
    Pagination page;
    @FXML
    TableColumn<Message,String> tableColumnFrom;
    @FXML
    TableColumn<Message,String> tableColumnTo;
    @FXML
    TableColumn<Message,String> tableColumnData;
    @FXML
    TableColumn<Message,String> tableColumnMesaj;
    @FXML
    TextArea textArea;




    String numeFurnizat;
    Predicate<Utilizator> pr;
    ObservableList<Message> model = FXCollections.observableArrayList();




    private MessageService service;
    private UtilizatorService utilizatorService;
    Stage dialogStage;
    Utilizator curent;
    Utilizator prieten;



    @FXML
    public void initialize() {
        tableColumnData.setCellValueFactory(new PropertyValueFactory<>("Date"));
        tableColumnTo.setCellValueFactory(new PropertyValueFactory<>("ToStrings"));
        tableColumnMesaj.setCellValueFactory(new PropertyValueFactory<>("Message"));
        tableColumnFrom.setCellValueFactory(new PropertyValueFactory<>("UserFrom"));
        page.setPageCount(1);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        initModel();
        textArea.setPromptText("Tastati un mesaj..");

    }

    private void initModel() {
        page.setPageFactory(this::createPage);

    }

    private Node createPage(int pageIndex) {


        Page<Message> messages =service.getMessagesW2(pageIndex, curent.getId(), prieten.getId());
        model.setAll( StreamSupport.stream(messages.getContent().spliterator(), false) .collect(Collectors.toList()));

        int size;
        if((messages.getTotalCount()%PAGE_SIZE) ==0)
            size= messages.getTotalCount()/PAGE_SIZE;
        else size=messages.getTotalCount()/PAGE_SIZE+1;
        page.setPageCount(size);
        tableView.setItems(model);
        return tableView;
    }


    public void setService(MessageService service, Stage stage, Utilizator curent, UtilizatorService utilizatorService,
                           Utilizator prieten) {
        this.service = service;
        this.dialogStage=stage;
        this.curent=curent;
        this.utilizatorService=utilizatorService;
        this.prieten=prieten;
        service.addObserver(this);
        textNume.setText("Mesaje cu : "+prieten.getLastName()+" "+prieten.getFirstName());

    }

    @FXML
    public void handleSend(){
        try {
           String mesaj=textArea.getText();

           List<Long> list =new ArrayList<>();

           list.add(prieten.getId());
           service.addMessage(curent.getId(), list, mesaj);
           textArea.clear();
           textArea.setPromptText("Tastati un mesaj..");



        }
        catch(ServiceException e){
            MessageAlert.showErrorMessage(null, e.toString());
        }


    }


    @FXML
    public void handleCancel(){
        dialogStage.close();
    }

//
//    public void handlePick(KeyEvent event) {
//      //  String nume = textFieldNume.getText();
//
//
//        System.out.println(nume);
//        numeFurnizat=nume;
//
//
//        Predicate<Utilizator> Pprenume = x->x.getFirstName().startsWith(nume);
//        Predicate<Utilizator> Pnume = x->x.getLastName().startsWith(nume);
//        pr = Pprenume.or(Pnume);
//        page.setPageFactory(this::createPageFiltered);
//
//    }

    private Node createPageFiltered(int pageIndex) {

        Page<Utilizator> finalPa= utilizatorService.getAllOnPage(pageIndex, curent.getId(),numeFurnizat);
        List<Utilizator> lista =new ArrayList<Utilizator>();
        finalPa.getContent().forEach(lista::add);

        List<Utilizator> finalPage =lista.stream().filter(pr)
                .collect(Collectors.toList());
        finalPage.forEach(System.out::println);
      //  model.setAll(finalPage);

        int size;
        if ((finalPage.size() % PAGE_SIZE) == 0)
            size = finalPage.size() / PAGE_SIZE;
        else size = finalPage.size() / PAGE_SIZE + 1;
        page.setPageCount(size);
        tableView.setItems(model);

        return tableView;

    }


    public void handlePick(KeyEvent keyEvent) {
    }

    @Override
    public void update(MessageChangeEvent messageChangeEvent) {
        initModel();
    }
}
