package socialnetwork.service;

import socialnetwork.domain.*;
import socialnetwork.repository.page.Page;
import socialnetwork.repository.page.Pageable;
import socialnetwork.repository.page.PageableImplementation;
import utils.events.PrietenieChangeEvent;
import utils.observers.Observer;

import java.util.List;


import static constant.PaginationConstant.PAGE_SIZE;

public class PageService {

    private CerereDePrietenieService cerereDePrietenieService;
    private UtilizatorService service;
    private MessageService messageService;
    private PrietenieService prietenieService;
    private EvenimentService evenimentService;

    Utilizator u;
    UserPage userPage;


    private int pageMesaje ;
    private int sizeMesaje=1 ;
    private int pageU ;

    public CerereDePrietenieService getCerereDePrietenieService() {
        return cerereDePrietenieService;
    }

    public UtilizatorService getService() {
        return service;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public PrietenieService getPrietenieService() {
        return prietenieService;
    }

    public EvenimentService getEvenimentService() {
        return evenimentService;
    }

    private int sizeU=5 ;
    private int pageC ;
    private int sizeC=5 ;


    /**
     *
     * @return utilizatorul curent
     */
    public Utilizator getUser() {
        return u;
    }


    /**
     * Seteaza utilizatorul curent
     * @param u ->noul user
     */
    public void setUser(Utilizator u) {
        this.u = u;
    }

    public UserPage getUserPage() {
        return userPage;
    }

    public void setUserPage(UserPage userPage) {
        this.userPage = userPage;
    }

    public PageService(CerereDePrietenieService cerereDePrietenieService,
                       UtilizatorService service, MessageService messageService,
                       PrietenieService prietenieService, EvenimentService evenimentService
                        ) {
        this.cerereDePrietenieService = cerereDePrietenieService;
        this.service = service;
        this.messageService = messageService;
        this.evenimentService = evenimentService;
        this.prietenieService = prietenieService;
    }

    /**
     * Functia de findOne
     * Cauta si returneaza un utilizator
     * @return user gasit sau null
     */
    public Utilizator findOne(long id){ return  service.findOne(id);}


    /**
     * Adauga un nou observer la prietenieService
     * @param e ->observer de PrietenieChangeEvent
     */
    public void addPrietenieObserver(Observer<PrietenieChangeEvent> e){
       prietenieService.addObserver(e);
    }

    /**
     *
     * @return numarul total de utilizatori
     */
    public int nrUseri(){
        return service.nrE();
    }

    /**
     *
     * @return numarul de cereri de prietenie
     */
    public int nrCereri(){
        return cerereDePrietenieService.nrE();
    }

    /**
     *
     * @return numarul de mesaje salvate
     */

    public int nrMesaje(){
        return messageService.nrE();
    }

    /**
     * Intoarce toata informatia de pe o pagina

     */
    public Page<Message> findAllFromOnePageMesaje(Pageable pageable){
        return messageService.findAllFromOnePage(pageable,u.getId());
    }

    /**
     * Seteaza mesajele pe o pagina(din baza de date)
     * @param page ->pagina pe care se seteaza

     */
    public Page<Message> getMessagesOnPage(int page) {
        this.pageMesaje=page;
        Pageable pageable = new PageableImplementation(page,PAGE_SIZE);

        Page<Message> studentPage = messageService.findAllFromOnePage(pageable,u.getId());
        userPage.setMesajePrimite(studentPage);
        return studentPage;
    }


    /**
     *
     * @param page ->primul user
     * @param id2 ->al doilea user
     * @return o pagina ce contine mesajele dintre 2 useri
     */
    public Page<Message> getMessagesCu2OnPage(int page, long id2) {
        this.pageMesaje=page;
        Pageable pageable = new PageableImplementation(page,PAGE_SIZE);

        Page<Message> studentPage = messageService.find2MessagesFromOnePage(pageable,u.getId(),id2);
        return studentPage;
    }

    /**
     * Intoarce toata informatia de pe o pagina

     */
    private Page<Utilizator> findAllFromOnePageUtilizatori(Pageable pageable){
        return service.findAllFromOnePage(pageable,u.getId());
    }

    /**
     * Seteaza utilizatorii pe o pagina
     * @param page ->pagina pe care se seteaza

     */
    public Page<Utilizator> getUsersOnPage(int page) {
        this.pageU=page;
        Pageable pageable = new PageableImplementation(page,PAGE_SIZE);

        Page<Utilizator> studentPage = service.findAllFromOnePage(pageable,u.getId());
        userPage.setPrieteni(studentPage);
        return studentPage;
    }


    /**
     * Intoarce toata informatia de pe o pagina

     */
    private Page<CerereDePrietenie> findAllFromOnePageCereri(Pageable pageable){
        return cerereDePrietenieService.findAllFromOnePage(pageable, u.getId());
    }

    /**
     * Seteaza cererile de prietenie pe o pagina
     * @param page ->pagina pe care se seteaza

     */
    public Page<CerereDePrietenie> getCereriOnPage(int page) {
        this.pageC=page;
        Pageable pageable = new PageableImplementation(page,PAGE_SIZE);

        Page<CerereDePrietenie> studentPage = cerereDePrietenieService.findAllFromOnePage(pageable, u.getId());
        userPage.setCereri(studentPage);
        return studentPage;
    }


    /**
     * Sterge o cerere de prietenie
     * @param id ->cererea de sters
     */
    public void stergeCerere(long id){
        cerereDePrietenieService.stergeCerere(id);
    }


    /**
     *
     * @return prietenii unui utilizator dat
     */
    public List<Long> getFriends(){
        return service.getFriend2(u.getId());
    }

    /**
     * Adauga o noua cerere de prietenie
     * @param id1 ->trimite cererea
     * @param id2 ->primeste cererea
     */
    public void addCerere(long id1, long id2){
        cerereDePrietenieService.addCerere(id1,id2);
    }


    /**
     * Sterge prietenia dintre cei 2 users
     * @param id1 ->primul user
     * @param id2 ->al doilea user
     */
    public void stergePrietenie(long id1, long id2){
        prietenieService.stergePrietenie(id1,id2);
    }

    /**
     * Modifica statusul unei cereri de prietenie
     * @param id ->cererea de prietenie referita
     * @param status -> noul status
     */
    public void modificaCererea(long id, String status){
        cerereDePrietenieService.modificaCerere(id,status);
    }

    /**
     * Cauta un user dupa email
     * @param email ->email-ul dat
     * @return utilizatorul gasit ori null
     */
    public Utilizator cautaDupaEmail(String email,String pass){
        return service.cautaDupaEmail(email,pass);
    }

    /**
     * Adauga un nou user
     * @param u ->utilizatorul de adaugat
     */
    public void addUtilizator(Utilizator u){
        service.addUtilizator(u);
    }

    /**
     *
     * @return toate mesajele
     */
    public Iterable<Message> getMesaje(){
        return  messageService.getMessages();
    }

    /**
     *
     * @return toate cererile de prietenie
     */
    public Iterable<CerereDePrietenie> getCereri(){
        return  cerereDePrietenieService.getCereri();
    }

    /**
     *
     * @param id1 ->user 1
     * @param id2 ->user 2
     * @return o prietenie intre cei 2 useri
     */
    public Prietenie getPrietenie( long id1, long id2){
        return  prietenieService.findOne(id1,id2);
    }


    public Iterable<Message> getMesajeB2(long m1, long m2){
        return messageService.getMessagesB2(m1,m2);
    }





}

