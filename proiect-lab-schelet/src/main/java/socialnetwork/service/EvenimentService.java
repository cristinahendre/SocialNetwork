package socialnetwork.service;

import socialnetwork.domain.*;
import socialnetwork.domain.validators.ServiceException;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.PagingRepository;
import socialnetwork.repository.page.Page;
import socialnetwork.repository.page.Pageable;
import socialnetwork.repository.page.PageableImplementation;
import utils.events.ChangeEventType;
import utils.events.EvenimentEvent;

import utils.observers.Observable;
import utils.observers.Observer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


import static constant.PaginationConstant.PAGE_SIZE;

public class EvenimentService implements Observable<EvenimentEvent>  {

    private PagingRepository<Long, Eveniment> repo;
    private PagingRepository<Long, Utilizator> utilizatorRepository;
    private PagingRepository<Long, Notificare> notiRepo;
    private int page ;
    private List<Observer<EvenimentEvent>> observers=new ArrayList<>();


    /**
     *
     * @return numarul total de evenimente
     */
    public int nrElem(){
        return repo.nrElem();
    }



    /**
     * Constructor Eveniment Service
     *

     */
    public EvenimentService(PagingRepository<Long, Eveniment> repo, PagingRepository<Long, Utilizator> utilizatorRepository,
                            PagingRepository<Long, Notificare> notiRepo) {
        this.repo = repo;
        this.utilizatorRepository=utilizatorRepository;
        this.notiRepo=notiRepo;
    }

    /**
     *
     * @param id ->id de eveniment
     * @return cauta si returneaza (in cazul in care il gaseste) un eveniment
     */
    public Eveniment findOne(long id){
        return repo.findOne(id);

    }

    /**
     *
     * @return toate notificarile
     */
    public Iterable<Notificare> getNotificari(){
        return  notiRepo.findAll();
    }

    /**
    @return  toate evenimentele
     */
    public Iterable<Eveniment> getAll() {
        return repo.findAll();
    }


    /**
     *
     * @return un id disponibil pentru o noua notificare
     */
    public long getIdNoti(){
        long id= notiRepo.nrElem()+1;
        while(notiRepo.findOne(id)!=null)
            id++;
        return id;
    }


    /**
     * Trimite notificari
     * @param u ->utilizator dat
     */
    public void sendNotifications(Utilizator u){

        if(u.getAbonat()==0)
            return;
        else {
            Iterable<Eveniment> evenimente = repo.findSomething(u.getId(), u.getId());

            evenimente
                    .forEach(x -> {


                      //  sendOneMessage(x, u);
                        trimiteNotificari(x,u);

                    });
        }

    }

    public void sendOffline(Utilizator u){
        if(u.getAbonat()!=0){
            Iterable<Eveniment> eveniments = repo.findSomething(u.getId(),u.getId());
            eveniments.forEach(x->{
                if(x.getData().isBefore(LocalDateTime.now())){
                    //System.out.println(x);
                    Notificare n = new Notificare(x, u);
                    n.setRepeat(4);
                    n.setId(getIdNoti());
                    n.setData(LocalDateTime.now());
                    n.setDescriere("Evenimentul a avut loc.");
                    notiRepo.save(n);
                }
            });
        }
    }




    /**
     * Adauga un nou eveniment, fiind date informatiile
     *

     */
    public void addEvent(long id,LocalDateTime date, String nume, String desc) {
        Utilizator org = utilizatorRepository.findOne(id);

        if (org == null ) {
            throw new ServiceException("Uilizatorul nu exista!!");
        }


        if(date.isBefore(LocalDateTime.now())){
            throw  new ValidationException("Nu putem organiza evenimente in trecut.");
        }
        long idEv= repo.nrElem()+1;
        while(findOne(idEv)!=null){
            idEv++;
        }


        Eveniment e= new Eveniment(org,date,nume,desc);
        e.setId(idEv);

        repo.save(e);
        notifyObservers(new EvenimentEvent(ChangeEventType.ADD, e));

    }


    /**
     * Adauga participanti la un eveniment
     * @param id ->id de eveniment
     * @param user ->utilizatorul de adaugat
     */
    public void addParticipanti(long id, Utilizator user){

             Eveniment e= repo.findOne(id);
             if(e.getData().isBefore(LocalDateTime.now()))
                 throw  new ValidationException("Evenimentul a avut loc deja.");
             if(e==null ) throw new ValidationException("Nu exista evenimentul.");
             if(e.getParticipanti()!=null) {
                 for (Utilizator u : e.getParticipanti()) {
                     if (user.equals(u))
                         throw new ValidationException("Deja v-ati inscris.");
                 }
             }
             if(e.getOrganizator().equals(user)){
                 throw  new ValidationException("Nu puteti participa la evenimentul organizat de dumneavoastra.");

             }
             List<Utilizator> p = new ArrayList<>();
             p.add(user);
             e.setParticipanti(p);
             repo.update(e);
             notifyObservers(new EvenimentEvent(ChangeEventType.UPDATE, e));


    }


    /**
     * Adauga un nou observer
     * @param e ->observer de adaugat
     */
    @Override
    public void addObserver(Observer<EvenimentEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<EvenimentEvent> e) {

    }

    /**
     * Notifica observers
     * @param t ->evenimentul ce produce notificarea
     */
    @Override
    public void notifyObservers(EvenimentEvent t) {
        observers.forEach(x->x.update(t));

    }


    /**
     *
     * @param utilizator ->utilizator dat
     * @return toate notificarile unui user
     */
    public Iterable<Notificare> getNotiOfUser(Utilizator utilizator){
        List<Notificare> list =new ArrayList<>();
        for(Notificare n: getNotificari()){
            if(n.getCatre().equals(utilizator)){
                list.add(n);
            }
        }
        return  list;
    }


    /**
     *
     * @param page ->pagina ceruta
     * @param id ->neutilizat
     * @return pagina pe care se afla evenimentele
     */
    public Page<Eveniment> getEventsOnPage(int page,long id) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page,PAGE_SIZE);

        Page<Eveniment> studentPage =findAllFromOnePage(pageable,id);
        return studentPage;
    }


    /**
     *
     * @param page ->pagina ceruta
     * @param id ->id user
     * @return pagina pe care se afla evenimentele organizate de un user
     */
    public Page<Eveniment> getMyEventsOnPage(int page,long id) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page,PAGE_SIZE);

        Page<Eveniment> studentPage =repo.findAllFromOnePage2(pageable,id,id);
        return studentPage;
    }



    /**
     *
     * @param page ->pagina ceruta
     * @param id ->id user
     * @return pagina pe care se afla evenimentele la care participa un user
     */
    public Page<Eveniment> getMyParticipation(int page,long id) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page,PAGE_SIZE);

        Page<Eveniment> studentPage =repo.findAllFromOnePage3(pageable,id,id,"");
        return studentPage;
    }


    public Page<Eveniment> findAllFromOnePage(Pageable pageable, long id){
        return repo.findAllFromOnePage(pageable,id);
    }


    /**
     *
     * @param page ->pagina ceruta
     * @param id ->id user
     * @return pagina ce contine notificarile unui user
     */
    public Page<Notificare> getNotificariOnPage(int page, long id) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page,PAGE_SIZE);

        Page<Notificare> studentPage = notiRepo.findAllFromOnePage(pageable,id);
        return studentPage;
    }


    /**
     * Se trimit notificari unui utilizator.
     * Notificarile sunt de forma: Evenimentul are loc in x zile, unde
     * x va fi continuu updatat, si reprezinta numarul de zile dintre momentul curent
     * si ziua evenimentului
     * Daca evenimentul are loc, notificarea se sterge
     * @param e ->id event
     * @param u ->id user
     */
    public void trimiteNotificari(Eveniment e, Utilizator u){
        Iterable<Notificare> notifies = notiRepo.findSomething(u.getId(), e.getId());
        List<Notificare> l=new ArrayList<>();
        notifies.forEach(l::add);
        Notificare n =l.get(0);
        if(e.getData().isBefore(LocalDateTime.now())){
            notiRepo.delete(n.getId());
            repo.delete(e.getId());
            notifyObservers(new EvenimentEvent(ChangeEventType.DELETE, e));
            return;
        }
        else{

            Duration d=Duration.between(LocalDateTime.now(),e.getData());
            if(!d.isNegative()){

                if(n.getDescriere().equals("Evenimentul are loc in "+d.toDays()+" zile")) return;
                n.setDescriere("Evenimentul are loc in "+d.toDays()+" zile");
                n.setData(LocalDateTime.now());
                notiRepo.update(n);
            }
        }

    }


    /**
     * Se trimite o prima notificare la inscrierea la eveniment
     * @param e ->eveniment
     * @param u ->user
     */
    public void primaNotificare(Eveniment e, Utilizator u){
        if(u.getAbonat()==0) return;
        else{
            Duration d= Duration.between(LocalDateTime.now(), e.getData());
            Notificare n =new Notificare(e,u);
            n.setId(getIdNoti());
            n.setData(LocalDateTime.now());
            n.setRepeat(0);
            n.setDescriere(("Evenimentul are loc in "+d.toDays()+" zile"));
            notiRepo.save(n);
        }
    }

}

