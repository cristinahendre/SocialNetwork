package socialnetwork.repository.database;

import socialnetwork.domain.Message;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.RepoException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.PagingRepository;
import socialnetwork.repository.Repository;
import socialnetwork.repository.page.Page;
import socialnetwork.repository.page.Pageable;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;



public class MessageDB implements PagingRepository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Message> validator;
    Repository<Long,Utilizator> repository ;
    int nr=0;

    public MessageDB(String url, String username, String password, Validator<Message> validator,
                     Repository<Long,Utilizator> repository) {
        this.url = url;
        this.repository=repository;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    /**
     * Cauta un mesaj
     * @param aLong ->mesajul de cautat
     * @return mesajul gasit /null
     */
    @Override
    public Message findOne(Long aLong) {
        String SQL = "SELECT id,from_u,mesaj,datac,reply_to "
                + "FROM message "
                + "WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, Math.toIntExact(aLong));
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                long id = rs.getLong("id");
                long from_u = rs.getLong("from_u");
                String mesaj = rs.getString("mesaj");
                LocalDateTime data = rs.getObject( 4,LocalDateTime.class);
                long reply_to=rs.getLong("reply_to");

                Utilizator from= repository.findOne(from_u);
                List<Utilizator> to = MessageReceiver(id);
                Message m= new Message(from,to,mesaj);
                m.setReply(reply_to);
                m.setId(id);
                m.setDate(data);

                return m;
            }


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    /**
     * Ia toate datele din tabela messagereceiver
     * Creeaza o lista de long unde pune toti utilizatorii catre care se adreseaza
     * mesajul cu id-ul id
     * @param id ->mesajul a carui destinatarii ii caut
     * @return lista cu destinatarii
     */
    public List<Utilizator> MessageReceiver(long id){

        List<Utilizator> to  = new ArrayList<>();
        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from messagereceiver where id_mesaj=?");
            statement.setInt(1, Math.toIntExact(id));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id_mesaj = resultSet.getLong("id_mesaj");
                long id_user = resultSet.getLong("id_user");
                if(id_mesaj == id) {
                    Utilizator u = repository.findOne(id_user);
                    to.add(u);
                }
            }

            connection.close();
            return to;
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}
        return to;
    }



    /**
     *
     * @return toate mesajele din tabela message
     * De asemenea, vor fi setati si utilizatorii carora le sunt adresate mesajele
     */
    @Override
    public Iterable<Message> findAll() {
        Set<Message> messages = new HashSet<>();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from message");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id_u = resultSet.getLong("from_u");
                String mesaj= resultSet.getString("mesaj");
                LocalDateTime date=resultSet.getObject(4,LocalDateTime.class);
                Long reply_ornot=resultSet.getLong("reply_to");


                Utilizator u = repository.findOne(id_u);
                List<Utilizator> to = MessageReceiver(id);
                Message m =new Message(u,to,mesaj);
                m.setDate(date);
                m.setId(id);
                m.setReply(reply_ornot);


                messages.add(m);

            }

            return messages;
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}

        return messages;

    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Va adauga in tabela messagereceiver un nou id_mesaj si id_user
     * @param id_m ->un id de mesaj
     * @param id_u -> un id de user
     */
    public void addReceiver(long id_m, long id_u){
        String SQL = "INSERT INTO messagereceiver(id_mesaj,id_user) VALUES(?,?)";

        long id =0;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, Math.toIntExact(id_m));
            pstmt.setInt(2, Math.toIntExact(id_u));


            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }


    /**
     * Salveaza un nou mesaj
     * @param entity ->mesajul de salvat
     *         entity must be not null
     * @return mesajul salvat
     */
    @Override
    public Message save(Message entity) {

        String SQL = "INSERT INTO message(id,from_u,mesaj,datac,reply_to) VALUES(?,?,?,?,?)";

        long id = 0;


        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {


            pstmt.setInt(1, Math.toIntExact(entity.getId()));
            pstmt.setInt(2, Math.toIntExact(entity.getFrom().getId()));
            pstmt.setString(3, entity.getMessage());
            pstmt.setObject(4, entity.getDate());
            pstmt.setInt(5, Math.toIntExact(entity.getReply()));


            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                entity.getTo().forEach(x->addReceiver(entity.getId(),x.getId()));

            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return  entity;

    }


    /**
     * Sterge atat din messagereceiver, cat si din message
     * @param aLong ->se sterge mesajul cu acest id
     * @return null
     */
    @Override
    public Message delete(Long aLong) {


        String SQL2 = "DELETE FROM messagereceiver WHERE id_user = ?";

        int affectedrows2 = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL2)) {

            pstmt.setInt(1, Math.toIntExact(aLong));

            affectedrows2 = pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        String SQL = "DELETE FROM message WHERE id = ?";
        if(findOne(aLong) == null ) throw  new RepoException("Nu exista mesajul.");

        int affectedrows = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, Math.toIntExact(aLong));

            affectedrows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }





        return null;
    }






    /**
    @return Numarul de mesaje din tabel
     */
    @Override
    public int nrElem() {

        String SQL = "SELECT COUNT(*) FROM message ";

        int rez = 0;

        try (Connection conn = connect();
             Statement pstmt = conn.createStatement())

        {
            ResultSet rs = pstmt.executeQuery(SQL);
            rs.next();
            rez = rs.getInt(1);
            System.out.println(rez);
            return  rez;


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return rez;


    }


    /**
     *
     * @param id1 ->user 1
     * @param id2 ->user 2
     * @return numarul de mesaje dintre 2 useri
     */
    public int nrMesaje2(long id1, long id2) {
        String SQL = "SELECT COUNT(*) FROM message M where from_u= "+id1+
        " and exists (select * from messagereceiver MR "+
       " where MR.id_user= "+id2+" and M.id=MR.id_mesaj) or from_u= "+id2+ "and exists(select * from "+
        " messagereceiver where id_user= "+ id1 +"  and id_mesaj=M.id)";
        int rez = 0;

        try (Connection conn = connect();
             Statement pstmt = conn.createStatement())

        {
            ResultSet rs = pstmt.executeQuery(SQL);
            rs.next();
            rez = rs.getInt(1);
            System.out.println(rez);
            return  rez;


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return rez;


    }

    /**
     * Se verifica cine a trimis ultimul mesaj dintre cei  2 utilizatori
     * @param id1->user 1
     * @param id2->user 2
     * @return id-ul acelui ultim mesaj
     */
    public Message getLastMessage(long id1, long id2){
        List<Message> messages = new ArrayList<>();
        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT M.* " +
                    " FROM message M where M.from_u = ? and  " +
                    " exists (select * from messagereceiver MR  where MR.id_user= ? and MR.id_mesaj = M. id  ) or " +
                    " M.from_u= ? and exists (select * from messagereceiver where id_user= ? and id_mesaj=M.id)");

            statement.setInt(1,(int)id1);
            statement.setInt(2,(int)id2);
            statement.setInt(3,(int)id2);
            statement.setInt(4,(int)id1);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id_u = resultSet.getLong("from_u");
                String mesaj= resultSet.getString("mesaj");
                LocalDateTime date=resultSet.getObject(4,LocalDateTime.class);
                Long reply_ornot=resultSet.getLong("reply_to");


                Utilizator u = repository.findOne(id_u);
                List<Utilizator> to = MessageReceiver(id);
                Message m =new Message(u,to,mesaj);
                m.setDate(date);
                m.setId(id);
                m.setReply(reply_ornot);


                messages.add(m);


            }
            Comparator<Message> Comparator= java.util.Comparator.comparing(Message::getDate).reversed();
            messages.sort(Comparator);
//            if(messages.get(0).getFrom().getId()!=id1) {
//                return messages.get(0);
//            }
            return  messages.get(0);

        }
        catch(SQLException e){
            e.printStackTrace();}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }


        return  null;
    }


    /**
     * Compara 2 date cronolofic
     * @param l1 ->data 1
     * @param l2 ->data 2
     * @return data mai aproape cronologic
     */
    public int compareDate(LocalDateTime l1,LocalDateTime l2){
        if(l1.isBefore(l2)) return  1;
        return  0;
    }


    /**
     *
     * @param pageable ->pagina
     * @param i ->user
     * @return toate mesajele primite/trimise de un user
     */
    public Iterable<Message> findAllLimited(Pageable pageable, long i) {
        List<Message> users = new ArrayList<>();
        int offset =( pageable.getPageNumber())* pageable.getPageSize();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select distinct from_u,id_user from message inner join messagereceiver " +
                            " on id=id_mesaj where from_u = ? or id_user=? "+
                            " limit ? offset ?");
            statement.setInt(1,(int)i);
            statement.setInt(2,(int)i);
            statement.setInt(3,pageable.getPageSize());
            statement.setInt(4,offset);



            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long from_u = resultSet.getLong("from_u");
                long id_user = resultSet.getLong("id_user");
                if(from_u!=i){
                    Message last=getLastMessage(i,from_u);
                    if(last!=null) {
                        if(!users.contains(last))
                            users.add(last);
                }}
                else if( id_user!=i){
                    Message last=getLastMessage(i,id_user);
                    if(last!=null) {
                        if(!users.contains(last))
                            users.add(last);
                    }
                }

            }
            connection.close();

            Comparator<Message> ComparatorM
                    = Comparator.comparing(
                    Message::getDate, this::compareDate);

            if(!users.isEmpty()) {
                users.sort(ComparatorM);
                nr=users.size();
                users.forEach(System.out::println);
                return users;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return users;
    }


    /**
     *
     * @param pageable ->pagima
     * @param i ->id user 1
     * @param i2 ->id user 2
     * @return pagina ce contine mesajele dintre cei 2 useri, ordonate descrescator
     */
    public Iterable<Message> findMesaj2Limited(Pageable pageable, long i,long i2) {
        List<Message> users =new ArrayList<>();
        int offset =( pageable.getPageNumber())* pageable.getPageSize();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT M.* " +
                    " FROM message M where M.from_u = ? and  " +
                    " exists (select * from messagereceiver MR  where MR.id_user= ? and MR.id_mesaj = M. id  ) or " +
                    " M.from_u= ? and exists (select * from messagereceiver where id_user= ? and id_mesaj=M.id) order by datac desc limit ? offset ?");
            statement.setInt(1,(int)i);
            statement.setInt(2, (int )i2);
            statement.setInt(3,(int)i2);
            statement.setInt(4,(int)i);
            statement.setInt(5,pageable.getPageSize());
            statement.setInt(6,offset);



            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                Long id_u = resultSet.getLong("from_u");
                String mesaj = resultSet.getString("mesaj");
                LocalDateTime date = resultSet.getObject(4, LocalDateTime.class);
                long reply_ornot = resultSet.getLong("reply_to");


                Utilizator u = repository.findOne(id_u);
                List<Utilizator> to = MessageReceiver(id);
                Message m = new Message(u, to, mesaj);
                m.setDate(date);
                m.setId(id);
                m.setReply(reply_ornot);


                users.add(m);

            }
            connection.close();

            Comparator<Message> ComparatorM
                    = Comparator.comparing(
                    Message::getDate, Comparator.naturalOrder()).reversed();

            users.sort(ComparatorM);
            return users;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return users;
    }


    /**
     *
     * @param pageable ->pagina
     * @param id ->user
     * @return toate mesajele primite/trimise de un user
     */
    @Override
    public Page<Message> findAllFromOnePage(Pageable pageable, long id) {
        Page<Message> p= new Page();
        p.setTotalCount(nr);
        p.setContent(this.findAllLimited(pageable, id));
        return p;
    }



    /**
     *
     * @param pageable ->pagima
     * @param id ->id user 1
     * @param id2 ->id user 2
     * @return pagina ce contine mesajele dintre cei 2 useri, ordonate descrescator
     */
    @Override
    public Page<Message> findAllFromOnePage2(Pageable pageable, long id, long id2) {
        Page<Message> p= new Page();
        p.setTotalCount(nrMesaje2(id,id2));
        p.setContent(this.findMesaj2Limited(pageable, id,id2));
        return p;
    }


    /**
     *
     * @param id1 ->userul care a primit mesaje
     * @param id2 ->userul care a trimis mesaje
     * @param inc ->data de inceput
     * @param sf ->data de sfarsit
     * @return ->lista mesajelor trimise de id2 catre id1
     */
    @Override
    public List<Message> raport1(long id1, long id2, LocalDateTime inc, LocalDateTime sf) {

        List<Message> users = new ArrayList<>();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT id, from_u, mesaj, datac, reply_to " +
                    " FROM message M inner join " +
                    " messagereceiver on id=id_mesaj " +
                    " where id_user= ?");
            statement.setInt(1, Math.toIntExact(id1));

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                Long id_u = resultSet.getLong("from_u");
                String mesaj = resultSet.getString("mesaj");
                LocalDateTime date = resultSet.getObject(4, LocalDateTime.class);
                long reply_ornot = resultSet.getLong("reply_to");


                Utilizator trimite = repository.findOne(id_u);
                Utilizator primeste= repository.findOne(id1);
                List<Utilizator> list =new ArrayList<>();
                list.add(primeste);
                Message  m  =new Message(trimite,list,mesaj);
                m.setId(id);
                m.setReply(reply_ornot);
                m.setDate(date);
                if(date.isAfter(inc) && date.isBefore(sf))
                            users.add(m);


            }
            connection.close();


            return users;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return users;

    }


    /**
     *
     * @param id1 ->userul ce primeste mesaje
     * @param id2 ->userul ce trimite mesaje
     * @return mesajele primite de id1 de la id2
     */
    @Override
    public List<Message> raport2(long id1, long id2) {

        List<Message> users = new ArrayList<>();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT id, from_u, mesaj, datac, reply_to " +
                    " FROM message M inner join " +
                    " messagereceiver on id=id_mesaj " +
                    " where from_u= ? and id_user= ?");
            statement.setInt(1, Math.toIntExact(id2));
            statement.setInt(2, Math.toIntExact(id1));

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                Long id_u = resultSet.getLong("from_u");
                String mesaj = resultSet.getString("mesaj");
                LocalDateTime date = resultSet.getObject(4, LocalDateTime.class);
                long reply_ornot = resultSet.getLong("reply_to");


                Utilizator trimite = repository.findOne(id_u);
                Utilizator primeste= repository.findOne(id1);
                List<Utilizator> list =new ArrayList<>();
                list.add(primeste);
                Message  m  =new Message(trimite,list,mesaj);
                m.setId(id);
                m.setReply(reply_ornot);
                m.setDate(date);
                users.add(m);


            }
            connection.close();


            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return users;

    }


    /**
     *
     * @param m1 ->user1
     * @param m2 ->user 2
     * @return mesaje trimise de m1 sau de m2
     */
    @Override
    public Iterable<Message> findSomething(long m1, long m2) {
        Set<Message> users = new HashSet<>();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from message where from_u = ? or from_u =?");
            statement.setInt(1, Math.toIntExact(m1));
            statement.setInt(2, Math.toIntExact(m2));

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                Long id_u = resultSet.getLong("from_u");
                String mesaj = resultSet.getString("mesaj");
                LocalDateTime date = resultSet.getObject(4, LocalDateTime.class);
                long reply_ornot = resultSet.getLong("reply_to");


                Utilizator u = repository.findOne(id_u);
                List<Utilizator> to = MessageReceiver(id);
                if(id_u==m1){
                    List<Utilizator> lista= new ArrayList<>();

                    for(Utilizator user: to){
                        if(user.getId()==m2) lista.add(user);
                    }
                    if(lista.size()==1) {
                        Message m = new Message(u, lista, mesaj);
                        m.setDate(date);
                        m.setId(id);
                        m.setReply(reply_ornot);


                        users.add(m);
                    }
                }
                if(id_u==m2){
                    List<Utilizator> lista= new ArrayList<>();

                    for(Utilizator user: to){
                        if(user.getId()==m1) lista.add(user);
                    }
                    if(lista.size()==1) {


                        Message m = new Message(u, lista, mesaj);
                        m.setDate(date);
                        m.setId(id);
                        m.setReply(reply_ornot);


                        users.add(m);
                    }
                }


            }
            connection.close();


            return users;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return users;
    }







    @Override
    public Message findUser(String data ,String pass) {
        return null;
    }

    @Override
    public Page<Utilizator> findAllWhoSentMessages(Pageable pageable, long id) {
        return null;
    }

    @Override
    public Message update(Message entity) {

        return entity;
    }


    @Override
    public Page<Message> findAllFromOnePage3(Pageable pageable, long id, long id1 , String nume) {
        return null;
    }

    @Override
    public Page<Message> findAllFromOnePage4(Pageable pageable, long id, String nume) {
        return null;
    }

}
