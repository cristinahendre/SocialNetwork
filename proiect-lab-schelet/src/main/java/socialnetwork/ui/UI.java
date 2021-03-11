package socialnetwork.ui;

import socialnetwork.domain.*;
import socialnetwork.domain.validators.ServiceException;
import socialnetwork.domain.validators.RepoException;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;


public class UI {
    public void run(CerereDePrietenieService cerereDePrietenieService, UtilizatorService service,
                    PrietenieService prietenieService , MessageService messageService) throws ClassNotFoundException {
    int cmd;
    optiuni();
    Scanner scanner = new Scanner(System.in).useDelimiter("\n");;

        while(true) {
        System.out.println("Dati o comanda!");
        cmd = scanner.nextInt();
        try {
            switch (cmd) {
                case 7: {
                    AfisarePrieteniiUser(scanner,prietenieService);
                    break;
                }
                case 1: {
                    adaugaUI(scanner, service);
                    service.getAll().forEach(System.out::println);

                    break;
                }
                case 0: {

                    service.getAll().forEach(System.out::println);
                    System.out.println("Prietenii: ");
                    String[] pri = service.afisarePrieteni();
                    for(int i= 0; i< pri.length; i++){
                        Utilizator x= service.findOne(i);
                        if(x!=null) {
                            System.out.println("Prietenii lui " + x.getFirstName()+ " "+x.getLastName() + " : " + pri[i]);
                        }
                    }
                    break;

                }
                case 2: {
                    stergeUI(scanner, service);
                    service.getAll().forEach(System.out::println);
                    break;

                }
                case 9:{
                    AfisarePrieteniiUserData(scanner,prietenieService);
                    break;
                }
                case 4: {
                    nouPrieten(scanner, prietenieService);
                    break;
                }
                case 6: {

                    prietenieService.getPrieteni().forEach(System.out::println);
                    break;
                }
                case -1: {
                    return;
                }
                case 5: {
                    stergePrieten(scanner,prietenieService);
                    service.setFriends();
                    break;
                }
                case 14:{
                    addMsg(scanner,messageService);
                    break;
                }
                case 15:{
                    respondMessage(scanner,messageService);
                    break;
                }
                case 3: {
                    updateUI(scanner, service);
                    break;
                }
                case 13:{
                    messageService.getMessages().forEach(System.out::println);
                    break;
                }

                case 22:{
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");

                    String d="12.01.2001";
                    String d1="20.12.2017";
                    LocalDateTime data = LocalDate.parse(d,formatter).atStartOfDay();
                    LocalDateTime data1 = LocalDate.parse(d1,formatter).atStartOfDay();

                    List<String> s =service.getMessagesAndFriendships(1,data,data1);
                    for(String str: s) System.out.println(str);
                    break;
                }
                case 23:{

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");

                    String d="12.01.2001";
                    String d1="20.12.2020";
                    LocalDateTime data = LocalDate.parse(d,formatter).atStartOfDay();
                    LocalDateTime data1 = LocalDate.parse(d1,formatter).atStartOfDay();

                    Utilizator u1= service.findOne(9);
                    Utilizator u2= service.findOne(5);
                    List<String> s =service.getMesajeDelaPrieten(u1,u2,data,data1);
                    for(String str: s) System.out.println(str);
                    break;
                }
                case 16:{
                    afisareMessage(scanner,messageService);
                    break;
                }

                case 8:{
                    int rez= service.NrComunitati();
                    System.out.println("Numarul de comunitati e " + rez);
                    break;
                }
                case 10:{

                    addCerere(scanner,cerereDePrietenieService);
                    break;
                }
                case 17:
                {
                    cautaUser(scanner,service);
                    break;
                }
                case 11:{
                    cerereDePrietenieService.getCereri().forEach(System.out::println);
                    break;
                }
                case 12:{
                    modificaStatus(scanner, cerereDePrietenieService);
                    break;
                }

                default: {
                    System.out.println("Nu ati introdus o optiune valida.");
                }

            }

        }
        catch (ValidationException e) {
            System.err.println("eroare[validare] "+ e);
        }
        catch(IllegalArgumentException e){
            System.err.println("eroare[argument invalid] "+e);
        }
        catch (ServiceException e){
            System.err.println("eroare[prietenie] : "+ e);
        }
        catch (RepoException e) {
            System.err.println("eroare[utilizator] " + e);
        }
    }

}

    /**
     * Ofera un raspuns catre un mesaj deja trimis
     * Raspunde la UN singur mesaj
     * Se da id-ul unui mesaj existent(la care se raspunde), id-ul userului ce raspunde
     * si mesajul de raspuns(un string)
     * @param scanner ->folosit pentru a citi de la tastatura
     * @param m ->MessageService, folosit pentru a salva raspunsul la mesaj
     */
    public static void respondMessage(Scanner scanner, MessageService m){
        System.out.println("id mesaj la care se raspunde");
        long id = scanner.nextLong();
        System.out.println("id user ce raspunde la mesaj");
        long id_u = scanner.nextLong();
        System.out.println("mesajul de raspuns: ");
        String msg= scanner.next();
        msg+= scanner.nextLine();
        m.replyto(id,id_u,msg);

    }

    /**
     * Se da un utilizator(id-ul sau)
     * Se cere afisarea tuturor prieteniilor pe care el le are sub forma:
     * Nume Prieten|Prenume Prieten |Data de la care sunt prieteni
     * @param scanner ->pentru citirea de la tastatura
     * @param prietenieService ->pentru a avea acces la prietenii
     */
    public static void AfisarePrieteniiUser(Scanner scanner, PrietenieService prietenieService){

        long id;
        System.out.println("Dati id-ul utilizatorului : ");
        id= scanner.nextLong();
        Predicate<Prietenie> stanga=((Prietenie x)->x.getId().getLeft()==id);
        Predicate<Prietenie> dreapta=((Prietenie x)->x.getId().getRight()==id);
        List<EntityDTO> prietenies = prietenieService.AfisarePrieteniUser(stanga,dreapta);
        prietenies.forEach(System.out::println);
    }

    /**
     *
     * Returneaza utilizatorul ce are id-ul dat de la tastatura
     * @param scanner ->folosit pentru a citi de la tastatura
     * @param service ->service de utilizatori
     */
    public static void cautaUser(Scanner scanner, UtilizatorService service){

        System.out.println("Dati id: ");
        long id = scanner.nextLong();
        Utilizator u=service.findOne(id);
        if(u== null) System.out.println("error");
        else
         System.out.println(u.toString());
    }

    /**
     * Se dau 2 utilizatori(prin id-urile lor)
     * Se afiseaza toate conversatiile(mesajele) dintre ei, in ordine cronologica
     * @param scanner->pentru citirea de la tastatura
     * @param m ->MessageService, folosit pentru a obtine mesajele existente
     */
    public static void afisareMessage(Scanner scanner, MessageService m){
        System.out.println("id 1");
        long id1= scanner.nextLong();
        System.out.println("id 2");
        long id2= scanner.nextLong();

        List<Message> list=m.AfisareConversatii(id1,id2);
        list.forEach(System.out::println);
    }


    /**
     *
     * @param scanner -> pentru a citi de la tastatura
     * @param prietenieService->service de Prietenii
     *  Afiseaza toti prietenii unui utilizator citit de la tastatura(doar id)
     *   Se vor afisa doar prietenii facuti intr-o luna citita de la tastatura
     *  Format de afisare: NumePrieten| PrenumePrieten| DataPrietenie
     */
    public static void AfisarePrieteniiUserData(Scanner scanner, PrietenieService prietenieService){

        long id;
        System.out.println("Dati id-ul utilizatorului : ");
        id= scanner.nextLong();
        System.out.println("Dati luna(nr intreg): ");
        int luna = scanner.nextInt();
        Predicate<Prietenie> stanga=((Prietenie x)->x.getId().getLeft()==id && x.getDate().getMonth().getValue() == luna);
        Predicate<Prietenie> dreapta=((Prietenie x)->x.getId().getRight()==id && x.getDate().getMonth().getValue() == luna);


        List<EntityDTO> prietenies = prietenieService.AfisarePrieteniUser(stanga,dreapta);
        prietenies.forEach(System.out::println);
    }

    /**
     * Trimite o cerere de prietenie
     * Se dau 2 utilizatori(prin id-urile lor)
     * Primul utilizator ii trimite  o cerere de prietenie celui de al doilea
     * Statusul cererii e (by default) pending
     * @param scanner ->pentru a citi de la tastatura
     * @param s ->pentru a adauga cererea de prietenie- CerereDePrietenieService
     */
    public static void addCerere(Scanner scanner, CerereDePrietenieService s)
    {
        System.out.println("id user 1");
        long id1= scanner.nextLong();
        System.out.println("id user 2");
        long id2= scanner.nextLong();
        s.addCerere(id1,id2);
        s.getCereri().forEach(System.out::println);
    }

    /**
     * Adauga un nou utilizator in lista de utilizatori
     * Se da numele si prenumele sau (id-ul e generat automat)
     * @param scanner ->pentru a citi de la tastatura
     * @param service ->pentru a salva noul utilizator
     */
    public static void adaugaUI(Scanner scanner, UtilizatorService service){
        System.out.println("Nume: ");
        String nume= scanner.next();
        System.out.println("Prenume: ");
        String prenume= scanner.next();
        System.out.println("Email: ");
        String email= scanner.next();
        Utilizator u=new Utilizator(prenume,nume);
        u.setEmail(email);
        service.addUtilizator(u);

    }

    /**
     * Se da un id de utilizator existent(daca nu exista=> eroare)
     * Se da nume si prenume
     * Se va modifica utilizatorul cu id-ul dat astfel:
     * numele si prenumele existente se vor inlocui cu acestea noi, citite acum
     * @param scanner ->pentru a citi de la tastatura
     * @param service ->pentru a salva entitatea modificata
     */
    public static void updateUI(Scanner scanner, UtilizatorService service){
        System.out.println("Id ");
        long id= scanner.nextLong();
        System.out.println("Nume: ");
        String nume= scanner.next();
        System.out.println("Prenume: ");
        String prenume= scanner.next();
        Utilizator u=new Utilizator(prenume,nume);
        u.setId((long) 2);
        service.updateUtilizator(id,u);
        service.getAll().forEach(System.out::println);

    }

    /**
     * Se da un id de utilizator existent
     * Se va sterge utilizatorul indicat de id-ul citit
     * @param scanner ->pentru a citi de la tastatura
     * @param service ->pentru a salva modificarea
     */
    public static void stergeUI(Scanner scanner, UtilizatorService service){
        System.out.println("Dati id: ");
        long id=scanner.nextLong();

        service.removeUtilizator(id);


    }

    /**
     * Se da un id ce reprezinta o cerere de prietenie deja trimisa
     * Se va modifica statusul cererii de prietenie din pending in approved/rejected
     * @param scanner ->pentru a citi de la tastatura
     * @param s -> CerereDePrietenieService, salveaza noul status
     */
    public static void modificaStatus(Scanner scanner, CerereDePrietenieService s){
        System.out.println("id cerere de prietenie");
        long id = scanner.nextLong();
        System.out.println("status nou[approved/rejected]");
        String status= scanner.next();
        s.modificaCerere(id,status);

    }

    /**
     * Se adauga un nou mesaj in lista de mesaje
     * Se da : -idul userului ce trimite mesajul (idul mesajului e generat automat)
     *         -catre cati utilizatori se trimite mesajul respectiv
     *         -idurile utilizatorilor catre care se trimite mesajul
     *         -mesajul de trimis
     * @param scanner ->pentru a citi de la tastatura
     * @param m-> MessageService, pentru a adauga mesajul
     */
    public static void addMsg(Scanner scanner, MessageService m){

        System.out.println("id user-cel ce trimite mesajul");
        long id_u = scanner.nextLong();
        System.out.println("Catre cati useri trimite: ");
        int nr= scanner.nextInt();
        System.out.println("id useri-catre cine trimite");
        List<Long> to =new ArrayList<>();
        for(int i=0;i<nr;i++){
            to.add(scanner.nextLong());
        }
        System.out.println("mesajul");
        String msg= scanner.next();
        msg+= scanner.nextLine();
        m.addMessage(id_u,to,msg);

    }

    /**
     * Se da 2 iduri de utilizatori
     * Se va intemeia o prietenie intre cei doi utilizatori
     * @param scanner ->pentru a citi de la tastatura
     * @param service ->PrietenieService, pentru a salva noua prietenie
     */
    public static void nouPrieten(Scanner scanner, PrietenieService service){
        System.out.println("Dati id (primul om): ");
        long id1=scanner.nextLong();
        System.out.println("Dati id: ");
        long id2=scanner.nextLong();


        service.addPrietenie(id1,id2);
        service.getPrieteni().forEach(System.out::println);
        //  service.getAll().forEach(System.out::println);

    }


    /**
     * Se da id-urile celor 2 utilizatori ce formeaza prietenia
     * Se va sterge prietenia indicata
     * @param scanner ->pentru a citi de la tastatura
     * @param service ->PrietenieService, folosit pentru a salva stergerea
     */
    public static void stergePrieten(Scanner scanner, PrietenieService service){
        System.out.println("Dati id (primul om): ");
        long id1=scanner.nextLong();
        System.out.println("Dati id: ");
        long id2=scanner.nextLong();


        service.stergePrietenie(id1,id2);

        service.getPrieteni().forEach(System.out::println);
        //  service.getAll().forEach(System.out::println);

    }



    public static final void optiuni(){
        System.out.println("Bine ati venit in aplicatie. Iata optiunile:");
        System.out.println("1 pentru a adauga utilizator");
        System.out.println("0 pentru a vedea lista utilizatorilor");
        System.out.println("2 pentru a sterge un utilizator");
        System.out.println("4 pentru a adauga un prieten");
        System.out.println("3 pentru a modifica un utilizator");
        System.out.println("-1 pentru a iesi din aplicatie");
        System.out.println("6 pentru a vedea toate prieteniile");
        System.out.println("5 pentru a sterge o prietenie");
        System.out.println("8 pentru numarul de comunitati");
        System.out.println("7 pentru Relatii de Prietenie a Userului");
        System.out.println("9 pentru Relatii de Prietenie -Luna");
        System.out.println("10 pentru a trimite o cerere de prietenie.");
        System.out.println("11 pentru a vedea cererile de prietenie");
        System.out.println("12 pentru a modifica statusul unei cereri de prietenie");
        System.out.println("13 pentru a vedea toate mesajele");
        System.out.println("14 pentru a trimite un mesaj");
        System.out.println("15 pentru a raspunde unui mesaj");
        System.out.println("16 pentru a vedea mesajele dintre 2 useri");
        System.out.println("17 pentru a cauta un utilizator");
    }
}
