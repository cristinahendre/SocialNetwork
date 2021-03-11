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
import socialnetwork.service.EvenimentService;
import socialnetwork.service.UtilizatorService;


import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static constant.PaginationConstant.PAGE_SIZE;


public class NotificariController {

    private EvenimentService service;
    private UtilizatorService utilizatorService;


    ObservableList<Notificare> model = FXCollections.observableArrayList();


    Stage dialogStage;
    Utilizator curent;
    @FXML
    Pagination page;
    @FXML
    TableView<Notificare> tableView;
    @FXML
    TableColumn<Notificare,String> tableColumnMesaj;
    @FXML
    TableColumn<Notificare,String> tableColumnEveniment;
    @FXML
    TableColumn<Notificare,String> tableColumnData;



    public void setService(EvenimentService service, Utilizator curent, Stage stage,
                           UtilizatorService utilizatorService) {
        this.service = service;
        this.dialogStage=stage;
        this.utilizatorService=utilizatorService;
        this.curent=curent;
        init();

    }

    public void initialize(){
        tableColumnMesaj.setCellValueFactory(new PropertyValueFactory<>("Mesaj"));
        tableColumnEveniment.setCellValueFactory(new PropertyValueFactory<>("NumeEvent"));
        tableColumnData.setCellValueFactory(new PropertyValueFactory<>("Data"));
        page.setPageCount(1);

    }

    private void init() {

        page.setPageFactory(this::createPage);
    }

    private Node createPage(int pageIndex) {

        if(curent.getAbonat()!=0) {
            Page<Notificare> all = service.getNotificariOnPage(pageIndex, curent.getId());

            model.setAll(StreamSupport.stream(all.getContent().spliterator(), false).collect(Collectors.toList()));


            int size;
            if ((all.getTotalCount() % PAGE_SIZE) == 0)
                size = all.getTotalCount() / PAGE_SIZE;
            else size = all.getTotalCount() / PAGE_SIZE + 1;
            page.setPageCount(size);
            tableView.setItems(model);
        }
        return tableView;

    }



    @FXML
    public void handleCancel(){
        dialogStage.close();
    }


    public void dezabonareB(ActionEvent event) {
        if(curent.getAbonat()==1) {
            utilizatorService.dezabonare(curent);
            handleCancel();
        }
        else{
            MessageAlert.showErrorMessage(null,"Sunteti deja dezabonat");
        }
    }

    public void subscribe(ActionEvent event) {
        if(curent.getAbonat()==0) {
            utilizatorService.abonare(curent);
            //  service.stergeNotificari(curent.getId());
            init();
        }
        else
        {
            MessageAlert.showErrorMessage(null,"Sunteti abonat deja.");
        }

    }
}

