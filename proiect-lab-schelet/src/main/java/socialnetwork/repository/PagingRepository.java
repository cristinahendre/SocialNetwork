package socialnetwork.repository;

import socialnetwork.domain.Entity;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.page.Page;
import socialnetwork.repository.page.Pageable;

import java.time.LocalDateTime;
import java.util.List;


public interface PagingRepository<ID ,
        E extends Entity<ID>>
        extends Repository<ID, E> {

    Page<E> findAllFromOnePage(Pageable pageable, long id);
    Page<E> findAllFromOnePage2(Pageable pageable, long id, long id1);
    Page<E> findAllFromOnePage3(Pageable pageable, long id, long id1,String nume);
    Page<E> findAllFromOnePage4(Pageable pageable, long id, String nume);




    List<E> raport1(long id1, long id2, LocalDateTime in, LocalDateTime sf);
    List<E> raport2(long id1, long id2);

    Iterable<E> findSomething(long id, long id2);
    E findUser(String data,String pass);

    Page<Utilizator> findAllWhoSentMessages(Pageable pageable,long id);



}
