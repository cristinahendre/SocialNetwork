package socialnetwork.service;

import socialnetwork.domain.*;
import socialnetwork.domain.validators.ServiceException;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.PagingRepository;
import socialnetwork.repository.Repository;
import socialnetwork.repository.page.Page;
import socialnetwork.repository.page.Pageable;
import socialnetwork.repository.page.PageableImplementation;
import utils.events.ChangeEventType;
import utils.events.UtilizatorChangeEvent;
import utils.observers.Observable;
import utils.observers.Observer;
//import sun.nio.ch.Util;

import java.time.LocalDateTime;
import java.util.*;


import static constant.PaginationConstant.PAGE_SIZE;

public class UtilizatorService implements Observable<UtilizatorChangeEvent> {
    private final PagingRepository<Long, Utilizator> repo;
    private final Repository<Tuple<Long,Long>,Prietenie> prietenieRepository;
    private final PagingRepository<Long,CerereDePrietenie> cerereDePrietenieRepository;
    private final PagingRepository<Long, Message> messageRepository;

    private int page ;
    private List<Observer<UtilizatorChangeEvent>> observers=new ArrayList<>();


    /**
     * Constructor Utilizator Service
     * @param repo ->repo de utilizatori
     * @param prietenieRepository ->repo de prietenie
     * @param cerereDePrietenieRepository->repo de cereri de prietenie
     * @param messageRepository ->repo de mesaje
     */
    public UtilizatorService(PagingRepository<Long, Utilizator> repo,
                             Repository<Tuple<Long,Long>,Prietenie> prietenieRepository,
                             PagingRepository<Long,CerereDePrietenie> cerereDePrietenieRepository,
                             PagingRepository<Long,Message> messageRepository) {
        this.repo = repo;
        this.prietenieRepository=prietenieRepository;
        this.cerereDePrietenieRepository = cerereDePrietenieRepository;
        this.messageRepository=messageRepository;

    }

    /**
     * Sterge manual toate mesajele in care apare un id dat
     * Se sterg si mesajele trimise de el si mesajele pentru el
     * Se ia in vedere si cazul in care utilizatorul isi trimite mesaj lui
     * @param id ->un id de utilizator
     */
    public void stergeMesajeFrom(long id) {
        int nu;
        List<Long> list = new ArrayList<>();
        for (Message pr : messageRepository.findAll()) {
           if(pr.getFrom().getId()==id) list.add(pr.getId());
           for(Utilizator el : pr.getTo()){
               nu=0;
               for(Long r: list){
                   if(r == el.getId())
                       nu=1;
               }
               if(el.getId()==id && nu==0) list.add(pr.getId());

           }
        }
        list.forEach(messageRepository::delete);
    }


        /**
        sterge un prieten din lista prietenilor
         @param id  - un utilizator(ce se va sterge din lista de prieteni)
         */
    public void stergePrieten(long id){
        List<Prietenie> list =new ArrayList<>();
        for(Prietenie pr: prietenieRepository.findAll()){
           if(pr.getId().getRight() == id || pr.getId().getLeft() ==id)
               list.add(pr);
       }
        list.forEach(x-> prietenieRepository.delete(new Tuple<>(x.getId().getLeft(),x.getId().getRight())));

    }


    /**
     * Sterge manual cererile de prietenie trimise de id si catre id
     * @param id ->un id de utilizator
     */
    public void stergeCereri(long id) {
        List<CerereDePrietenie> list = new ArrayList<>();
        for (CerereDePrietenie pr : cerereDePrietenieRepository.findAll()){
           if(pr.getPrimeste().getId()== id || pr.getTrimite().getId() == id)
                list.add(pr);
        }
        list.forEach(x -> cerereDePrietenieRepository.delete(x.getId()));
    }



        /**
         Adauga un nou utilizator
         @param messageTask  - un utilizator
         */
    public Utilizator addUtilizator(Utilizator messageTask) {
        long nr=(long) nrE()+1;
        while(repo.findOne(nr)!=null){
            nr++;
        }
        messageTask.setId(nr);
        for(Utilizator u: getAll()){
            if(messageTask.getEmail().equals(u.getEmail())){
                throw  new ValidationException("Exista deja aceasta adresa de email");
            }
        }

        Utilizator task = repo.save(messageTask);
        if(task == null) {
            notifyObservers(new UtilizatorChangeEvent(ChangeEventType.ADD,task));
        }

        return task;
    }

    /**
    Returneaza numarul de elemente din map
     */
    public int nrE(){
        return repo.nrElem();
    }


    /**
    Actualizeaza datele unui utlizator, dat fiind id-ul sau
     @param id  - id-ul existent al utilizatorului
     @param messageTask  -datele cu care se inlocuieste
     */
    public Utilizator updateUtilizator(long id, Utilizator messageTask) {
        messageTask.setId(id);
        Utilizator task = repo.update(messageTask);
        setFriends();
        if(task!=null) {
            notifyObservers(new UtilizatorChangeEvent(ChangeEventType.UPDATE, task));
        }
        return task;

    }

    /**
     *
     * @param id ->un id de utilizator
     * @return un utilizator care are id-ul dat
     */
    public Utilizator findOne(long id){
        return repo.findOne(id);
    }


    /**
    Sterg un utilizator
     @param id  - id-ul utilizatorului de sters
     */
    public Utilizator removeUtilizator(long id){

        Utilizator u = repo.findOne(id);
        if(repo.findOne(id) == null) throw  new ServiceException("Nu exista acest utilizator.");
        stergePrieten(id);
        stergeCereri(id);
        stergeMesajeFrom(id);
        setFriends();

        repo.delete(id);
        if(u!=null) {
            notifyObservers(new UtilizatorChangeEvent(ChangeEventType.DELETE, u));
        }
        return  u;

    }

    /**
     * Cauta un user dupa adresa de email
     */
    public Utilizator cautaDupaEmail(String email,String pass){

        return  repo.findUser(email,pass);
    }



    /**
    Returnez toti utilizatorii
     */
    public Iterable<Utilizator> getAll(){

        return repo.findAll();
    }


    /**
    Functia ce seteaza  prietenii unui utilizator
     */
    public void setFriends(){

        for(Utilizator u: getAll()){
            u.setList0();
        }
        for(Prietenie pr : prietenieRepository.findAll()){
            Utilizator u1=repo.findOne(pr.getId().getRight());
            Utilizator u2=repo.findOne(pr.getId().getLeft());
            u1.setFriends(u2);
            u2.setFriends(u1);
        }


    }

    /**
    Afiseaza numarul de comunitati
     */
    public  int NrComunitati(){

        int nr= 0;
        for(Utilizator u: getAll()){
            nr++;
        }
        int noduri = nr;
        int muchii=prietenieRepository.nrElem();
        ComponenteConexe ccc= new ComponenteConexe(noduri);
        for(Prietenie  p: prietenieRepository.findAll()){
            ccc.adaugaMuchie(p.getId().getLeft().intValue(), p.getId().getRight().intValue());
        }
        return ComponenteConexe.NrComponente(noduri,muchii,ccc );
    }



    /**
     * Afiseaza toti utilizatorii si prietenii acestora
     */
    public String[] afisarePrieteni(){

        int nr = 0,i;
        for(Utilizator u: repo.findAll()) nr++;
        String[] pri = new String[nr+1];

        for(i =0; i< pri.length;i++) pri[i] = " ";


        for(Prietenie pr : prietenieRepository.findAll()){
            Utilizator u =repo.findOne(pr.getId().getLeft());
            Utilizator u1= repo.findOne(pr.getId().getRight());
            String nume= " |"+u1.getFirstName()+" "+u1.getLastName();
            String util1 = u.getFirstName()+" "+u.getLastName();
            String util2 = u1.getFirstName()+" "+u1.getLastName();
            pri[Math.toIntExact(u.getId())]+=nume;
            String nume1=" |"+ u.getFirstName()+" "+u.getLastName();
            pri[Math.toIntExact(u1.getId())]+=nume1;


        }
        return pri;

    }


    /**
     * Se adauga intr-o lista toti prietenii unui user dat(doar id-urile lor)
     * @param id ->id-ul unui utilizator existent
     * @return lista de prieteni a utilizatorului
     */
    public List<Long> getFriend2(long id){
        List<Long> deReturnat= new ArrayList<>();
        for(Prietenie p: prietenieRepository.findAll()){
            if(p.getId().getRight() == id)
                deReturnat.add(p.getId().getLeft());
            if(p.getId().getLeft()==id)
                deReturnat.add(p.getId().getRight());
        }
        return  deReturnat;
    }


    /**
     * Cauta un utilizator dupa nume si prenume
     * @param nume ->numele dat
     * @param prenume ->prenumele dat
     * @return primul utilizator ce are numele si prenumele dat
     */
    public Utilizator cautaUser(String nume, String prenume){
        for(Utilizator u : getAll()){
            if(u.getFirstName().equals(prenume) && u.getLastName().equals(nume))
                return u;
        }
        return null;
    }



    /**
     * Adauga un observer
     * @param e
     */
    @Override
    public void addObserver(Observer<UtilizatorChangeEvent> e) {
        observers.add(e);

    }


    /**
     * sterge un observer
     * @param e ->observer de sters
     * metoda nu e implementata deoarece nu  e utilizata
     */
    @Override
    public void removeObserver(Observer<UtilizatorChangeEvent> e) {

    }

    /**
     * Notifica observers atunci cand are loc o modificare
     * @param t
     */
    @Override
    public void notifyObservers(UtilizatorChangeEvent t) {
        observers.stream().forEach(x->x.update(t));


    }

    /**
     * Returneaza intr-o lista de stringuri niste propozitii ce contin:
     *   +cu cine s-a imprietenit userul in perioada data
     *   +ce mesaje a trimis userul in perioada data
     * @param id ->id-ul unui utilizator
     * @param inceput ->data de inceput
     * @param sfarsit ->data de sfarsit
     * Pentru raport1 -pdf
     * @return
     */
    public List<String> getMessagesAndFriendships(long id, LocalDateTime inceput, LocalDateTime sfarsit){
        List<String> rez= new ArrayList<>();
        Utilizator u =findOne(id);
        rez.add("Mesajele si prieteniile utilizatorului: "+u.getFirstName()+" "+u.getLastName());
        rez.add("Data aleasa: "+inceput.toString()+" : "+sfarsit.toString());
        rez.add(" ");
        /*for(Prietenie  p: prietenieRepository.findAll()){
            if(p.getDate().isBefore(sfarsit) && p.getDate().isAfter(inceput)) {
                if (p.getId().getLeft() == id) {
                    Utilizator prieten = findOne(p.getId().getRight());
                    rez.add("Prieten nou: " + prieten.getFirstName() + " " + prieten.getLastName() +"  in data "+
                            p.getDate().toString());
                }
                else{
                    if (p.getId().getRight() == id) {
                        Utilizator prieten = findOne(p.getId().getLeft());
                        rez.add("Prieten nou : " + prieten.getFirstName() + " " + prieten.getLastName() +"  in data "+
                                p.getDate().toString());
                    }
                }
            }
        }

         */

        repo.raport1(id,id,inceput,sfarsit).forEach(
                x-> {
                    rez.add("Prieten nou : " + x.getFirstName() + " " + x.getLastName() );
                }
        );

        messageRepository.raport1(id,id,inceput,sfarsit).forEach(
                x->{
                    if(x.getDate().isBefore(sfarsit) && x.getDate().isAfter(inceput)){
                        rez.add("A primit mesajul: " + x.getMessage() + " in data: " + x.getDate());

                    }
                }
        );

        Utilizator deCautat=findOne(id);
        if(rez.isEmpty()) rez.add("Nicio activitate a userului: "+deCautat.getFirstName() +" "+deCautat.getLastName());
        return  rez;
    }


    /**
     * Se afiseaza toate mesajele primite de u1 de la u2
     * @param u1 ->un user
     * @param u2 ->un user
     * @param inceput ->data de inceput
     * @param sfarsit ->data de final
     * Pentru raport 2-pdf
     * @return
     */
    public List<String> getMesajeDelaPrieten(Utilizator u1, Utilizator u2,LocalDateTime inceput,LocalDateTime sfarsit){
        List<String> rez= new ArrayList<>();
        rez.add("Mesajele primite de "+u1.getFirstName()+" "+u1.getLastName()+
                " de la "+u2.getFirstName()+" "+u2.getLastName());
        rez.add("Perioada aleasa: "+inceput.toString()+" : "+sfarsit.toString());
        rez.add(" ");
        /*for(Message m: messageRepository.findAll()){

            if(m.getDate().isBefore(sfarsit) &&
                    m.getDate().isAfter(inceput)){

                if(m.getFrom().getId()==u2.getId()) {

                    for (Utilizator persoana : m.getTo()) {
                        if (persoana.getId() == u1.getId()) {
                            rez.add("A primit mesajul: " + m.getMessage() + " in data: " + m.getDate());
                        }
                    }
                }
                }


        }*/
        messageRepository.raport2(u1.getId(),u2.getId()).forEach(
                x->{
               if(x.getDate().isAfter(inceput) && x.getDate().isBefore(sfarsit)) {
                   rez.add("A primit mesajul: " + x.getMessage() + " in data: " + x.getDate());
               }
        }
        );
        return  rez;
    }


    /**
     *
     * @param page ->numarul paginii cerute
     * @param id ->id-ul userului
     * @return o pagina pe care apar 5 prieteni ai utilizatorului
     */
    public Page<Utilizator> getFriendsOnPage(int page,long id) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page,PAGE_SIZE);

        Page<Utilizator> studentPage = repo.findAllFromOnePage(pageable,id);
        return studentPage;
    }

    /**
     * Returneaza toti utilizatorii din baza de date, mai putin cel dat ca parametru
     * @param page ->de la ce pagina se iau datele
     * @param id->userul
     * @return o pagina
     */
    public Page<Utilizator> getAllOnPage(int page,long id, String nume) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page,PAGE_SIZE);

        Page<Utilizator> studentPage = repo.findAllFromOnePage3(pageable,id,id,nume);
        return studentPage;
    }


    /**
     * Returneaza utilizatorii care nu sunt prietenii cu userul dat
     * @param page ->pagina de la care sa se aduca date
     * @param id ->userul curent
     * @return ->pagina
     */
    public Page<Utilizator> getUsersOnPage(int page,long id) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page,PAGE_SIZE);

        Page<Utilizator> studentPage =findAUsersFromOnePage(pageable,id);
        return studentPage;
    }


    /**
     * Returneaza utilizatorii care nu sunt prieteni cu userul dat, filtrati dupa nume
     * @param page ->pagina de la care sa se aduca date
     * @param id ->userul curent
     * @return ->pagina
     */
    public Page<Utilizator> getNonFriendsFilter(int page,long id, String nume) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page,PAGE_SIZE);

        Page<Utilizator> studentPage =repo.findAllFromOnePage4(pageable,id,nume);
        return studentPage;
    }



    public Page<Utilizator> findAllFromOnePage(Pageable pageable, long id){
        return repo.findAllFromOnePage(pageable,id);
    }

    public Page<Utilizator> findAUsersFromOnePage(Pageable pageable, long id){
        return repo.findAllFromOnePage2(pageable,id,id);
    }


    /**
     * Dezaboneaza un utilizator de la notificari
     * @param u ->utilizatorul
     */
    public void dezabonare(Utilizator u){
        u.setAbonat(0);
        repo.update(u);

    }


    /**
     * Aboneaza un utilizator la notificari
     * @param u ->utilizatorul
     */
    public void abonare(Utilizator u){
        u.setAbonat(1);
        repo.update(u);
    }

}
