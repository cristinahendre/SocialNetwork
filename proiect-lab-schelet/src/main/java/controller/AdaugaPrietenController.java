package controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ServiceException;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.page.Page;
import socialnetwork.service.CerereDePrietenieService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static constant.PaginationConstant.PAGE_SIZE;


public class AdaugaPrietenController {
    @FXML
    private TextField textFieldNume;

    @FXML
    TableView<Utilizator> tableView;
    @FXML
    Pagination page;
    @FXML
    TableColumn<Utilizator,String> tableColumnNume;
    @FXML
    TableColumn<Utilizator,String> tableColumnPrenume;
    @FXML
    Label textLabel;
    @FXML
    TextArea area;

    String numeFurnizat;
    Predicate<Utilizator> pr;


    private UtilizatorService service;
    private PrietenieService prietenieService;
    private CerereDePrietenieService cerereDePrietenie;
    Stage dialogStage;
    Utilizator curent;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    private int dataSize=10;
    private static final int rows=5;


    @FXML
    private void initialize() {
        tableColumnNume.setCellValueFactory(new PropertyValueFactory<>("LastName"));
        tableColumnPrenume.setCellValueFactory(new PropertyValueFactory<>("FirstName"));

        textFieldNume.setPromptText("Cauta user..");
        page.setPageCount(3);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    private Node createPage(int pageIndex) {


        //pageM.setCurrentPageIndex(1);
        Page<Utilizator> users =service.getUsersOnPage(pageIndex,curent.getId());

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

    public void initModel(){

        page.setPageFactory(this::createPage);

    }

    public void setService(UtilizatorService service,  Stage stage, Utilizator m, PrietenieService prietenieService,
                           CerereDePrietenieService cerereDePrietenie) {
        this.service = service;
        this.dialogStage=stage;
        this.curent=m;
        this.prietenieService=prietenieService;
        this.cerereDePrietenie=cerereDePrietenie;
        dataSize=service.nrE();
        initModel();


    }

    @FXML
    public void handleSave(){
        try{
            textLabel.setText("Va rog sa asteptati.");

            Utilizator[] users= tableView.getSelectionModel().getSelectedItems().toArray(new Utilizator[0]);
            if(users.length ==0){
                MessageAlert.showErrorMessage(null,"Selectati din primul tabel. (ctrl+click=selectie multipla)");
                return;
            }


            for(Utilizator u: users)
                cerereDePrietenie.addCerere(curent.getId(), u.getId());

            MessageAlert.showMessage(null,"Cerere trimisa cu succes.");
          //  handleCancel();
            initModel();
            textLabel.setText("");

        }
        catch(NullPointerException e){
            MessageAlert.showErrorMessage(null, "Selectati un utilizator din tabel");
            return;
        }
        catch(ValidationException | ServiceException e){
            MessageAlert.showErrorMessage(null, e.toString());
            return;

        }

    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }




    public void searchUser(KeyEvent keyEvent) {
        String nume = textFieldNume.getText();

        if(nume.equals("")){
            initModel();
        }
        else {
            numeFurnizat = nume;


            Predicate<Utilizator> Pprenume = x -> x.getFirstName().startsWith(nume);
            Predicate<Utilizator> Pnume = x -> x.getLastName().startsWith(nume);
            pr = Pprenume.or(Pnume);
            page.setPageFactory(this::createPageFiltered);
        }

    }

    private Node createPageFiltered(int pageIndex) {

        Page<Utilizator> finalPa= service.getNonFriendsFilter(pageIndex, curent.getId(),numeFurnizat);
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




}

