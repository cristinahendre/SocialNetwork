package socialnetwork;
import controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.PagingRepository;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;
import socialnetwork.service.*;

import javax.swing.text.Document;
import java.io.IOException;


public class MainApp  extends Application {

    Validator validator1 = new PrietenieValidator();
    final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
    final String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
    final String pasword = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
    //repo-DB
//    Repository<Long, Utilizator> userFileRepository3 =
//            new RepoDB(url, username, pasword, new UtilizatorValidator());
    PagingRepository<Long, Utilizator> userFileRepository3 =
            new RepoDB(url, username, pasword, new UtilizatorValidator());

    PagingRepository<Long, Eveniment> eventRepo = new EvenimentDB(url,username,pasword,userFileRepository3);
//    Repository<Long, CerereDePrietenie> cerereDBRepo =
//            new CerereDePrietenieDB(url, username, pasword, new CerereDePrietenieValidator(),userFileRepository3);
   PagingRepository<Long, Notificare> notiRepo= new NotificareDB(url,username,pasword,userFileRepository3,eventRepo);

    PagingRepository<Long, CerereDePrietenie> cerereDBRepo =
        new CerereDePrietenieDB(url, username, pasword, new CerereDePrietenieValidator(),userFileRepository3);
    Repository<Tuple<Long, Long>, Prietenie> prietenieRepository2 = new PrietenieRepoDB(url, username, pasword, validator1);
    //Repository<Long, Message> messageRepository = new MessageDB(url, username, pasword, new MessageValidator(),userFileRepository3);
    PagingRepository<Long, Message> messageRepository = new MessageDB(url, username, pasword, new MessageValidator(),userFileRepository3);

    //service-DB
    UtilizatorService serviceBD = new UtilizatorService(userFileRepository3, prietenieRepository2, cerereDBRepo, messageRepository);
    CerereDePrietenieService cerereDePrietenieService = new CerereDePrietenieService(userFileRepository3, prietenieRepository2, cerereDBRepo);
    EvenimentService evenimentService= new EvenimentService(eventRepo,userFileRepository3,notiRepo);

    PrietenieService prietenieServiceBD = new PrietenieService(userFileRepository3, prietenieRepository2);
    MessageService messageService = new MessageService(messageRepository, userFileRepository3);
    PageService pageService =new PageService(cerereDePrietenieService,serviceBD,messageService,prietenieServiceBD,evenimentService);


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {



        initView(primaryStage);
        primaryStage.setWidth(430);
        primaryStage.setTitle("Welcome!");
        primaryStage.show();

    }


    private void initView(Stage primaryStage) throws IOException {


        FXMLLoader messageLoader = new FXMLLoader();
        messageLoader.setLocation(getClass().getResource("/views/loginView.fxml"));
        AnchorPane messageTaskLayout = messageLoader.load();
        primaryStage.setScene(new Scene(messageTaskLayout));

        LoginController controller=messageLoader.getController();
        controller.setService(pageService);

    }
}
