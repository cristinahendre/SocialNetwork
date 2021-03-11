package socialnetwork;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.PagingRepository;
import socialnetwork.repository.Repository;
import socialnetwork.repository.Repository0;
import socialnetwork.repository.database.*;
import socialnetwork.repository.file.PrietenieFile;
import socialnetwork.repository.file.UtilizatorFile;
import socialnetwork.repository.memory.InMemoryRepository;
import socialnetwork.service.*;
import socialnetwork.ui.UI;
//import sun.nio.ch.Util;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        //String fileName=ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
//        String fileName = "data/users.csv";
//        String fName = "data/friends.csv";
//        final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
//        final String username = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
//        final String pasword = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
//
//        //validatori
//        Validator validator = new UtilizatorValidator();
//        Validator validator1 = new PrietenieValidator();
//
//        //repo-File
//        Repository<Long, Utilizator> userFileRepository = new UtilizatorFile(fileName
//                , validator);
//        Repository<Tuple<Long, Long>, Prietenie> prietenieRepository = new PrietenieFile(fName, validator1);
//
//        //service-file
//        PrietenieService prietenieService = new PrietenieService(userFileRepository, prietenieRepository);
//
//
//        //repo-DB
////        Repository<Long, Utilizator> userFileRepository3 =
////                new RepoDB(url, username, pasword, new UtilizatorValidator());
//        PagingRepository<Long, Utilizator> userFileRepository3 =
//                new RepoDB(url, username, pasword, new UtilizatorValidator());
//        PagingRepository<Long, CerereDePrietenie> cerereDBRepo =
//                new CerereDePrietenieDB(url, username, pasword, new CerereDePrietenieValidator(),userFileRepository3);
//        Repository<Tuple<Long, Long>, Prietenie> prietenieRepository2 = new PrietenieRepoDB(url,username,pasword,validator1);
//        PagingRepository<Long,Message> messageRepository= new MessageDB(url,username,pasword,new MessageValidator(),userFileRepository3);
//        PagingRepository<Long,Eveniment> eventRepo =new EvenimentDB(url,username,pasword,userFileRepository3);
//
//        //service-DB
//        UtilizatorService serviceBD= new UtilizatorService(userFileRepository3,prietenieRepository2,cerereDBRepo,messageRepository);
//        CerereDePrietenieService cerereDePrietenieService = new CerereDePrietenieService(userFileRepository3,prietenieRepository2,cerereDBRepo);
//        //UtilizatorService service = new UtilizatorService(userFileRepository, prietenieRepository,cerereDBRepo,messageRepository);
//        //service.setFriends();
//        PrietenieService prietenieServiceBD = new PrietenieService(userFileRepository3, prietenieRepository2);
//        MessageService messageService= new MessageService(messageRepository,userFileRepository3);
//        EvenimentService evenimentService = new EvenimentService(eventRepo,userFileRepository3);
//
//       //serviceBD.insert100();
//        serviceBD.getNextMessages();
//        for(Eveniment e: evenimentService.getAll()){
//            System.out.println(e);
//        }
//        //UI+run
//        UI u= new UI();
//        u.run(cerereDePrietenieService,serviceBD,prietenieServiceBD, messageService);


  MainApp.main(args);


    }
}



