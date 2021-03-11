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
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.ServiceException;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.database.NotificareDB;
import socialnetwork.repository.page.Page;
import socialnetwork.service.*;
import sun.rmi.rmic.Util;
import utils.events.EvenimentEvent;
import utils.events.PrietenieChangeEvent;
import utils.observers.Observer;

import java.io.IOException;

import java.util.*;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static constant.PaginationConstant.PAGE_SIZE;

public class MenuController implements Observer<EvenimentEvent> {
    PageService service;

    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    ObservableList<CerereDePrietenie> modelCereri = FXCollections.observableArrayList();
    ObservableList<Message> modelMesaj = FXCollections.observableArrayList();
    ObservableList<Notificare> modelN = FXCollections.observableArrayList();


    long id;
    long idDat;
    Utilizator curent;
    private int dataSize=13;
    private int dataSizeM=13;
    private int dataSizeC=13;
    private static final int rows=5;

    Stage loginStage;


    @FXML
    Label abonatLabel;
    @FXML
    Label numePrenume;
    @FXML
    MenuBar menuBar;
    @FXML
    Pagination page;
    @FXML
    Pagination pageC;
    @FXML
    Pagination pageN;
    @FXML
    Pagination pageM;
    @FXML
    Button logOut;
    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableView<Message> tableViewM;
    @FXML
    TableView<Notificare> tableViewN;
    @FXML
    TableView<CerereDePrietenie> tableViewCereri;



    @FXML
    TableColumn<Notificare,String> tableColumnMesajN;
    @FXML
    TableColumn<Notificare,String> tableColumnEveniment;
    @FXML
    TableColumn<Notificare,String> tableColumnDataN;




    @FXML
    TextField TextFieldNumeU;
    @FXML
    TextField TextFieldPrenumeU;


    public void setServices(PageService service,  long id, Stage loginStage) {
        this.id=id;
        this.service = service;
        curent=service.getUser();
        if(curent.getAbonat()==1){
            abonatLabel.setText("Sunteti abonat la notificari.");
        }
        else{
            abonatLabel.setText("Sunteti dezabonat de la notificari.");

        }
        this.loginStage=loginStage;

        service.getEvenimentService().addObserver(this);
        dataSize=service.nrUseri();
        dataSizeC=service.nrCereri();
        dataSizeM=service.nrMesaje();
        Lighting lighting = new Lighting();
        numePrenume.setText("Bine ai venit, "+curent.getFirstName()+" "+curent.getLastName()+"!");
        numePrenume.setEffect(lighting);
        numePrenume.setFont(Font.font("Arial",20));

       // handleSearchUser();
        //initModel();
        //initMesaje();
        //tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //tableViewCereri.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


    }

    @FXML
    public void initialize() {

        menuBar.setFocusTraversable(true);

//        tableColumnNume.setCellValueFactory(new PropertyValueFactory<>("LastName"));
//        tableColumnPrenume.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
//
//        tableColumnFrom.setCellValueFactory(new PropertyValueFactory<>("NameTrimite"));
//        tableColumnTo.setCellValueFactory(new PropertyValueFactory<>("NamePrimeste"));
//        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("Status"));
//        tableColumnData.setCellValueFactory(new PropertyValueFactory<>("Data"));
//
//
//        tableColumnDateM.setCellValueFactory(new PropertyValueFactory<>("Date"));
//        tableColumnToM.setCellValueFactory(new PropertyValueFactory<>("ToStrings"));
//        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<>("Message"));
//        tableColumnFromM.setCellValueFactory(new PropertyValueFactory<>("UserFrom"));
//        page.setPageCount(1);
//        pageC.setPageCount(1);
//        pageM.setPageCount(1);

        tableColumnMesajN.setCellValueFactory(new PropertyValueFactory<>("Mesaj"));
        tableColumnEveniment.setCellValueFactory(new PropertyValueFactory<>("NumeEvent"));
        tableColumnDataN.setCellValueFactory(new PropertyValueFactory<>("Data"));
        pageN.setPageCount(1);
      //  service.getEvenimentService().sendNotifications(curent);

        initNo();

    }

    private void initNo() {

        pageN.setPageFactory(this::createPageNotificari);
    }

    private Node createPageNotificari(int pageIndex) {

        if(curent.getAbonat()!=0) {
            Page<Notificare> all = service.getEvenimentService().getNotificariOnPage(pageIndex, curent.getId());

            modelN.setAll(StreamSupport.stream(all.getContent().spliterator(), false).collect(Collectors.toList()));


            int size;
            if ((all.getTotalCount() % PAGE_SIZE) == 0)
                size = all.getTotalCount() / PAGE_SIZE;
            else size = all.getTotalCount() / PAGE_SIZE + 1;
            pageN.setPageCount(size);
            tableViewN.setItems(modelN);
            return  tableViewN;
        }
        return null;

    }


    private Node createPageMesaje(int pageIndex) {


        //pageM.setCurrentPageIndex(1);
        Page<Message> messages =service.getMessagesOnPage(pageIndex);



        modelMesaj.setAll( StreamSupport.stream(messages.getContent().spliterator(), false) .collect(Collectors.toList()));
        //    modelMesaj.setAll(messages.getContent());

        int size;
        if((messages.getTotalCount()%PAGE_SIZE) ==0)
            size= messages.getTotalCount()/PAGE_SIZE;
        else size=messages.getTotalCount()/PAGE_SIZE+1;
        pageM.setPageCount(size);
        tableViewM.setItems(modelMesaj);
        return tableViewM;

    }


    public void initMesaje(){

        pageM.setPageFactory(this::createPageMesaje);

    }



    private void initModel() {

        pageC.setPageFactory(this::createPageCereri);
    }

    private Node createPage(int pageIndex) {

        Page<Utilizator> all =service.getUsersOnPage(pageIndex);

        model.setAll( StreamSupport.stream(all.getContent().spliterator(), false) .collect(Collectors.toList()));


        int size;
        if((all.getTotalCount()%PAGE_SIZE) ==0)
            size= all.getTotalCount()/PAGE_SIZE;
        else size=all.getTotalCount()/PAGE_SIZE+1;
        page.setPageCount(size);
        tableView.setItems(model);
        return tableView;

    }





    private Node createPageCereri(int pageIndex) {

        Page<CerereDePrietenie> all =service.getCereriOnPage(pageIndex);

        modelCereri.setAll( StreamSupport.stream(all.getContent().spliterator(), false) .collect(Collectors.toList()));

        int size;
        if((all.getTotalCount()%PAGE_SIZE) ==0)
            size= all.getTotalCount()/PAGE_SIZE;
        else size=all.getTotalCount()/PAGE_SIZE+1;
        pageC.setPageCount(size);
        tableViewCereri.setItems(modelCereri);
        return tableViewCereri;

    }

    @Override
    public void update(EvenimentEvent task) {

        //handleSearchUser();
       // initModel();
        initNo();
    }




    public void AdaugaPrietenFereastra(ActionEvent event) {

        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/editUtilizatorView.fxml"));

            AnchorPane root =  loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Prieteni");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);


            EditUtilizatorController editController = loader.getController();
            editController.setService(service.getService(), dialogStage, curent,service.getPrietenieService(),service.getCerereDePrietenieService());

            dialogStage.show();
          //  dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //initModel();
    }





    /**
     * Returneaza o lista cu toti Utilizatorii
     * @return
     */
    public List<Utilizator> creareListaUsers() {

//        List<Utilizator> aux = new ArrayList<>();
//        service.getAll().forEach(aux::add);
//        return aux;
        return  null;

    }

    /**
     * La cautarea unui user, se vor afisa in tableview toti prietenii lui

     */
    @FXML
    public void handleSearchUser() {


//        System.out.println(id);
//        Utilizator u = service.getUser(id);
//        System.out.println(u);
//        if (u == null)
//            MessageAlert.showErrorMessage(null, "Nu am gasit userul");
//        // System.out.println(u.toString());
//
//        model.setAll(
//                creareLista(u.getId())
//        );
      /*  Set<Utilizator> users =service.getUsersOnPage(page.getCurrentPageIndex(),id);

        model.setAll(users);

*/

        //pageM.setCurrentPageIndex(1);


        page.setPageFactory(this::createPage);

    }


    /**
     * Cauta toti utilizatorii ce au numele si prenumele dat
     * @param keyEvent-..
     */
    public void searchName(KeyEvent keyEvent) {
        try{
            String nume = TextFieldNumeU.getText();
            System.out.println(nume);
            String prenume = TextFieldPrenumeU.getText();
            System.out.println(prenume);

            model.setAll(creareListaUsers().stream()
                    .filter(x -> x.getLastName().startsWith(nume))
                    .filter(x -> x.getFirstName().startsWith(prenume))
                    .collect(Collectors.toList())
            );
        }
        catch(NullPointerException e){
            MessageAlert.showErrorMessage(null,"Cautati dupa nume sau prenume");
            return;
        }
    }



    /**
     * Arata toate cererile de prietenie ale unui utilizator
     * @param actionEvent ->event
     */
    public AfisareCereriController showCereri(ActionEvent actionEvent) {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/afisareCereri.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Cererile de prietenie ale userului "+service.getUser().getFirstName()+" "+service.getUser().getLastName());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AfisareCereriController afisare = loader.getController();
            afisare.setService(service.getCerereDePrietenieService(), dialogStage, id);

            dialogStage.show();
            handleSearchUser();

            return afisare;


        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }


    /**
     Metoda ce genereaza fereastra pentru raport
     Primeste un prieten ca parametru
     Daca prietenul e null=> raport 1, altfel raport2
     */
    public void Raport1(){

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/RapoarteView.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Raport1");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            RapoarteController afisare = loader.getController();
            afisare.setService(service.getService(), curent);

            dialogStage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Raport2 pentru pdf
     * Arata toate mesajele primite de la un anumit prieten ale userului cautat
     */
    public void Raport2() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/raport2View.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Raport2");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            Raport2Controller afisare = loader.getController();
            afisare.setService(service.getService(), curent);

            dialogStage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void logOut(ActionEvent actionEvent) {

        /*try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/loginView.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Login");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            LoginController loginController = loader.getController();
            loginController.setService(service );

            dialogStage.show();

            Stage stage = (Stage) menuBar.getScene().getWindow();
            stage.close();



        } catch (IOException e) {
            e.printStackTrace();
        }

         */

        Stage stage = (Stage) menuBar.getScene().getWindow();
        stage.close();
        loginStage.show();


    }

    public void sendMessage(ActionEvent actionEvent) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/MessageMenuView.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Send Message");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            try {

                MessageMenuController sender = loader.getController();
                sender.setService(service.getMessageService(), service.getService(),dialogStage,curent);

                dialogStage.showAndWait();
            }
            catch(NullPointerException e){
                MessageAlert.showErrorMessage(null,"Nu ati selectat destinatarul..");
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        initMesaje();
    }

    public void alegeUserPentruMesaj(ActionEvent actionEvent) {

        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/ConvoView.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Vizualizare Mesaje");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            ConvoController sender = loader.getController();
            sender.setService(service.getService(), dialogStage, curent, service.getPrietenieService(),service.getMessageService());

            dialogStage.showAndWait();
           // initMesaje();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void aprobaCererea(ActionEvent event) {

        try {
                CerereDePrietenie pr = tableViewCereri.getSelectionModel().getSelectedItem();
                if (pr == null) {
                    MessageAlert.showErrorMessage(null, "Nu ati selectat o cerere din tabelul cu cereri.");
                    return;
                }
                System.out.println(pr);
                if (!pr.getStatus().equals("pending")) {
                    MessageAlert.showErrorMessage(null, "Cererea nu poate fi aprobata/respinsa.");
                    return;
                }
                if (pr.getPrimeste().getId().equals(id)) {

                    service.modificaCererea(pr.getId(), "approved");

                }
                else {
                    MessageAlert.showErrorMessage(null, "Nu puteti accepta/refuza cererea de prietenie pe care ati trimis-o.");
                    return;
                }


        } catch (NullPointerException e) {
                MessageAlert.showErrorMessage(null,"Nu ati selectat o cerere din tabelul cu cereri.");
                return;
            }

        initModel();
        handleSearchUser();

    }



    public void refuzaCererea(ActionEvent event) {

        try {
                CerereDePrietenie pr = tableViewCereri.getSelectionModel().getSelectedItem();
                if (pr == null) {
                    MessageAlert.showErrorMessage(null, "Nu ati selectat o cerere de prietenie din tabelul cu cereri.");
                    return;
                }
                System.out.println(pr);
                if (!pr.getStatus().equals("pending")) {
                    MessageAlert.showErrorMessage(null, "Cererea nu poate fi aprobata/respinsa.");
                    return;
                }
                if (pr.getPrimeste().getId().equals(id)) {

                    service.modificaCererea(pr.getId(), "rejected");

                }
                else {
                    MessageAlert.showErrorMessage(null, "Nu puteti accepta/refuza cererea de prietenie pe care ati trimis-o.");
                    return;
                }

            } catch (NullPointerException e) {
                MessageAlert.showErrorMessage(null,"Nu ati selectat o cerere din tabelul cu cereri.");
                return;

            }

        initModel();


    }

    public void createEvent(ActionEvent event) {

        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/createEventView.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Creeaza un eveniment");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            CreateEventViewCtr ctr = loader.getController();
            ctr.setService(service.getEvenimentService(), curent,dialogStage);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showEvents(ActionEvent event) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/eventsView.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Toate Evenimentele");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            EvenimentController ctr = loader.getController();
            ctr.setService(service.getEvenimentService(),curent);

            dialogStage.showAndWait();
            initNo();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void dezabonare(ActionEvent event) {
        if(curent.getAbonat()==1) {
            service.getService().dezabonare(curent);
            abonatLabel.setText("Sunteti dezabonat de la notificari.");
            initNo();
        }
        else{
            MessageAlert.showErrorMessage(null,"Sunteti deja dezabonat");
        }
    }

    public void abonare(ActionEvent event) {
        if(curent.getAbonat()==0) {
            service.getService().abonare(curent);
            abonatLabel.setText("Sunteti reabonat la notificari.");
            initNo();
        }
        else
        {
            MessageAlert.showErrorMessage(null,"Sunteti abonat deja.");
        }
    }

    public void fereastraCereri(ActionEvent event) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/afisareCereri.fxml"));
            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Cereri de prietenie");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AfisareCereriController ctr = loader.getController();
            ctr.setService(service.getCerereDePrietenieService(),dialogStage,id);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showmyEvents(ActionEvent event) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/myEvents.fxml"));
            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Evenimente create");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            MyEventsController ctr = loader.getController();
            ctr.setService(service.getEvenimentService(),curent);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void myparticipation(ActionEvent event) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/evLacareparticip.fxml"));
            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Evenimente la care participati");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            EvParticip ctr = loader.getController();
            ctr.setService(service.getEvenimentService(),curent);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

