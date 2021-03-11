package socialnetwork.service;
import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.PagingRepository;
import socialnetwork.repository.page.Page;
import socialnetwork.repository.page.Pageable;
import socialnetwork.repository.page.PageableImplementation;
import utils.events.ChangeEventType;
import utils.events.MessageChangeEvent;
import utils.observers.Observable;
import utils.observers.Observer;

import java.time.LocalDateTime;
import java.util.*;

import static constant.PaginationConstant.PAGE_SIZE;

public class MessageService implements Observable<MessageChangeEvent> {
    private PagingRepository<Long, Message> messageRepository ;
    private  PagingRepository<Long, Utilizator> utilizatorRepository;

    private List<Observer<MessageChangeEvent>> observers=new ArrayList<>();
    private int page ;




    /**
     * Constructor MessageService
     * @param messageRepository ->repo de mesaje
     * @param utilizatorRepository ->repo de utilizatori
     */
    public MessageService(PagingRepository<Long, Message> messageRepository, PagingRepository<Long, Utilizator> utilizatorRepository) {
        this.messageRepository = messageRepository;
        this.utilizatorRepository = utilizatorRepository;
    }

    /**
     *
     * @return toate mesajele
     */
    public Iterable<Message> getMessages(){ return  messageRepository.findAll();}


    /**
     * Adauga un nou mesaj in tabela messages
     * @param id_u ->id-ul utilizatorului ce trimite mesajul
     * @param to ->lista ce contine id-urile utilizatorilor catre care se trimite mesajul
     * @param msg->mesajul de trimis
     * @return mesajul adaugat
     */
    public Message addMessage(long id_u, List<Long> to, String msg){
        long nr= messageRepository.nrElem()+1;
        while(messageRepository.findOne(nr)!=null) nr++;
        LocalDateTime date= LocalDateTime.now();
        Utilizator u =utilizatorRepository.findOne(id_u);
        List<Utilizator> lista =new ArrayList<>();
        for(Long el : to){
            lista.add(utilizatorRepository.findOne(el));
        }
        Message m=new Message(u,lista,msg);
        m.setId(nr);
        m.setDate(date);
        messageRepository.save(m);
        notifyObservers(new MessageChangeEvent(ChangeEventType.ADD, m));
        return m;
    }


    /**
     * Reprezinta un raspuns la un mesaj primit
     * Se verifica sa fi primit un mesaj inainte de a raspunde
     * Raspunsul va fi salvat tot ca un mesaj in tabela messages
     * In coloana reply_to din tabela messages se va pune id-ul mesajului primit,
     * acela la care se ofera raspunsul
     * @param id ->id-ul mesajului la care se raspunde
     * @param id_u ->id user ce raspunde la mesaj
     * @param msg ->mesajul de raspuns
     * @return mesajul nou de reply
     */
    public Message replyto(long id, long id_u, String msg){
        long i= messageRepository.nrElem()+1;
        while(messageRepository.findOne(i)!=null) i++; //id mesaj nou de raspuns
        LocalDateTime date= LocalDateTime.now();
        Message m= messageRepository.findOne(id);
        if(m==null) throw new ValidationException("Nu exista acest mesaj.Incercati sa il trimiteti intai.");
        int gasit=0;
        for(Utilizator to:m.getTo()){
            if(to.getId()==id_u){
                gasit=1; break;
            }
        }
        if(gasit==0) throw new ValidationException("Nu puteti da reply daca nu ati primit un mesaj.");
        List<Utilizator> to = new ArrayList<>();
        to.add(m.getFrom());
        Utilizator u= utilizatorRepository.findOne(id_u);
       // Message reply=new Message(id_u,to,msg);
        Message newM= new Message(u,to,msg);
        newM.setId(i);
        newM.setDate(date);
        newM.setReply(m.getId());
        messageRepository.save(newM);
        notifyObservers(new MessageChangeEvent(ChangeEventType.ADD, m));

        return newM;
    }


    /**
     * Se vor afisa conversatiile dintre cei doi utilizatori, dati prin id-urile lor
     * Conversatiile se afiseaza in ordine cronologica
     * @param id1 ->primul utilizator
     * @param id2 ->al doilea utilizator
     * @return lista cu mesajele dintre ei
     */
    public List<Message> AfisareConversatii(long id1, long id2){
        List<Message> lis=new ArrayList<>();
        for(Message m: messageRepository.findAll()){
            if(m.getFrom().getId()==id1){
                int bun=0;
                for(Utilizator to: m.getTo()){
                    if(to.getId() == id2)
                    { bun=1; break;}
                }
                if(bun==1) lis.add(m);
            }
            else{
                if(m.getFrom().getId()==id2){
                    int bun=0;
                    for(Utilizator to: m.getTo()){
                        if(to.getId() == id1)
                        { bun=1; break;}
                    }
                    if(bun==1) lis.add(m);

                }
            }

        }
//        lis.sort(new Comparator<Message>() {
//            @Override
//            public int compare(Message o1, Message o2) {
//               if(o1.getDate().isBefore(o2.getDate()))
//                   return 0;
//               return 1;
//            }
//        });
        Comparator<Message> ComparatorM
           = Comparator.comparing(
                Message::getDate, LocalDateTime::compareTo);

        lis.sort(ComparatorM);
        return lis;
    }


    /**
     * Adauga un observer
     * @param e ->observer
     */
    @Override
    public void addObserver(Observer<MessageChangeEvent> e) {
        observers.add(e);

    }


    /**
     * sterge un observer
     * @param e ->observer
     */
    @Override
    public void removeObserver(Observer<MessageChangeEvent> e) {

    }


    /**
     * Notifica observers la un event
     * @param t ->eveniment
     */
    @Override
    public void notifyObservers(MessageChangeEvent t) {
        observers.forEach(x->x.update(t));

    }

    /**
     *
     * @return numarul total de mesaje
     */
    public int nrE(){
        return  messageRepository.nrElem();
    }


    /**
     *
     * @param page ->pagina data
     * @param id ->utilizatorul a carui mesaje le vreau
     * @return toate mesajele de pe o pagina
     */
    public Page<Message> getMessagesOnPage(int page, long id) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page,PAGE_SIZE);

        Page<Message> studentPage = messageRepository.findAllFromOnePage(pageable,id);
        return  studentPage;
    }


    /**
     *
     * @param page ->pagina ceruta
     * @param id ->primul user
     * @param id2 ->al doilea user
     * @return o pagina pe care sunt mesaje intre 2 utilizatori
     */
    public Page<Message> getMessagesW2(int page, long id,long id2) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page,PAGE_SIZE);

        Page<Message> studentPage = messageRepository.findAllFromOnePage2(pageable,id,id2);
        return studentPage;
    }

    public Page<Message> findAllFromOnePage(Pageable pageable, long id){
        return messageRepository.findAllFromOnePage(pageable, id);
    }


    public Page<Message> find2MessagesFromOnePage(Pageable pageable, long id, long id2){
        return messageRepository.findAllFromOnePage2(pageable, id,id2);
    }


    /**
     *
     * @param m1 ->primul user
     * @param m2 ->al doilea user
     * @return mesajele trimise de un user sau de altul
     */
    public Iterable<Message> getMessagesB2(long m1,long m2){
        return  messageRepository.findSomething(m1,m2);

    }
}
