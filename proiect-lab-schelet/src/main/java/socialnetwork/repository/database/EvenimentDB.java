package socialnetwork.repository.database;
import socialnetwork.domain.Eveniment;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.RepoException;
import socialnetwork.repository.PagingRepository;
import socialnetwork.repository.page.Page;
import socialnetwork.repository.page.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;



public class EvenimentDB implements PagingRepository<Long, Eveniment> {
    private final String url;
    private final String username;
    private final String password;
    PagingRepository<Long,Utilizator> repository ;
    long particip =0;

    public EvenimentDB(String url, String username, String password,
                     PagingRepository<Long,Utilizator> repository) {
        this.url = url;
        this.repository=repository;
        this.username = username;
        this.password = password;
        curataBaza();

    }

    /**
     * Cauta un eveniment
     * @param aLong ->evenimentul de cautat
     * @return evenimentul gasit /null
     */
    @Override
    public Eveniment findOne(Long aLong) {
        String SQL = "SELECT * "
                + "FROM eveniment "
                + "WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, Math.toIntExact(aLong));
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                long id = rs.getLong("id");
                long user=rs.getLong("organizator");
                LocalDateTime data = rs.getObject( 3,LocalDateTime.class);
                String nume=rs.getString("nume");
                String desc=rs.getString("descriere");


                Utilizator u= repository.findOne(user);
                List<Utilizator> to = getParticipanti(aLong);
                Eveniment e = new Eveniment(u, data, nume, desc);
                e.setId(aLong);
                if(to.size()!=0) {
                    e.setParticipanti(to);

                }
                return e;
            }


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }



    /**
     * Ia toate datele din tabela ev_participanti
     * Creeaza o lista de long unde pune toti utilizatorii care participa
     * la evenimentul cu id-ul id
     * @param id ->evenimentul a carui participanti ii caut
     * @return lista cu participantii
     */
    public List<Utilizator> getParticipanti(long id){

        List<Utilizator> to  = new ArrayList<>();
        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from ev_participanti where id_event =? ");
            statement.setInt(1,(int)id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id_event = resultSet.getLong("id_event");
                Long id_user = resultSet.getLong("id_user");

                Utilizator u = repository.findOne(id_user);
                to.add(u);

            }

            return to;
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}
        return to;
    }


    /**
     *
     * @return toate evenimentele din tabela eveniment
     * De asemenea, vor fi setati si utilizatorii care participa la evenimente
     */
    @Override
    public Iterable<Eveniment> findAll() {
        Set<Eveniment> events = new HashSet<>();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from eveniment");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                long id_u = resultSet.getLong(2);
                LocalDateTime date=resultSet.getObject(3,LocalDateTime.class);
                String nume=resultSet.getString(4);
                String desc=resultSet.getString(5);

                Utilizator u = repository.findOne(id_u);
                List<Utilizator> to = getParticipanti(id);
                Eveniment e=  new Eveniment(u,date, nume,desc);
                e.setId(id);
                if(to.size()!=0)
                  e.setParticipanti(to);
                events.add(e);

            }
            connection.close();
            return events;
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}

        return events;

    }


    /**
     * Ia toate evenimentele paginat
     * @param pageable ->pagina
     * @param i ->nefolosit
     * @return pagina cu evenimente
     */
    public Iterable<Eveniment> findAllLimited( Pageable pageable, long i) {
        Set<Eveniment> events = new HashSet<>();
        int offset =( pageable.getPageNumber())* pageable.getPageSize();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from eveniment limit ? offset ?");
            statement.setInt(1,pageable.getPageSize());
            statement.setInt(2,offset);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                long id_u = resultSet.getLong(2);
                LocalDateTime date=resultSet.getObject(3,LocalDateTime.class);
                String nume=resultSet.getString(4);
                String desc=resultSet.getString(5);

                Utilizator u = repository.findOne(id_u);
                List<Utilizator> to = getParticipanti(id);
                Eveniment e=  new Eveniment(u,date, nume,desc);
                e.setId(id);
                if(to.size()!=0)
                    e.setParticipanti(to);
                events.add(e);

            }
            connection.close();

        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}

        return events;

    }


    /**
     *
     * @param pageable ->pagina
     * @param user ->un id de user
     * @return toate evenimentele organizate de user
     */
    public Set<Eveniment> getMyEvents(Pageable pageable, long user){
        Set<Eveniment> events = new HashSet<>();
        int offset =( pageable.getPageNumber())* pageable.getPageSize();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from eveniment where organizator =? limit ? offset ?");
            statement.setInt(2,pageable.getPageSize());
            statement.setInt(3,offset);
            statement.setInt(1, (int) user);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id= resultSet.getLong(1);
                long id_u = resultSet.getLong(2);
                LocalDateTime date=resultSet.getObject(3,LocalDateTime.class);
                String nume=resultSet.getString(4);
                String desc=resultSet.getString(5);

                Utilizator u = repository.findOne(id_u);
                List<Utilizator> to = getParticipanti(id);
                Eveniment e=  new Eveniment(u,date, nume,desc);
                e.setId(id);
                if(to.size()!=0)
                    e.setParticipanti(to);
                events.add(e);

            }
            connection.close();

        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}

        return events;
    }


    /**
     *
     * @param pageable ->pagina
     * @param user ->id de user
     * @return toate evenimentele la care participa un user
     */
    public Set<Eveniment> getMyParticipation(Pageable pageable, long user){
        Set<Eveniment> events = new HashSet<>();
        int offset =( pageable.getPageNumber())* pageable.getPageSize();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT id, organizator, datac, nume, descriere " +
                    " FROM eveniment inner join ev_participanti on id= id_event " +
                    " where id_user= ? limit ? offset ?");
            statement.setInt(2,pageable.getPageSize());
            statement.setInt(3,offset);
            statement.setInt(1, (int) user);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id= resultSet.getLong(1);
                long id_u = resultSet.getLong(2);
                LocalDateTime date=resultSet.getObject(3,LocalDateTime.class);
                String nume=resultSet.getString(4);
                String desc=resultSet.getString(5);

                Utilizator u = repository.findOne(id_u);
                List<Utilizator> to = getParticipanti(id);
                Eveniment e=  new Eveniment(u,date, nume,desc);
                e.setId(id);
                if(to.size()!=0)
                    e.setParticipanti(to);
                events.add(e);

            }
            connection.close();

        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}
        particip=events.size();
        return events;
    }


    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }



    /**
     * Va adauga in tabela ev_participanti un nou id_event si id_user
     * @param id_m ->un id de eveniment
     * @param id_u -> un id de user
     */
    public void addParticipanti(long id_m, long id_u){
        String SQL = "INSERT INTO ev_participanti(id_event,id_user) VALUES(?,?)";

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
     * Salveaza un nou eveniment
     * @param entity ->evenimentul de salvat
     *         entity must be not null
     * @return evenimentul salvat
     */
    @Override
    public Eveniment save(Eveniment entity) {

        String SQL = "INSERT INTO eveniment(id,organizator,datac,nume,descriere) VALUES(?,?,?,?,?)";

        long id = 0;


        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {


            pstmt.setInt(1, Math.toIntExact(entity.getId()));
            pstmt.setInt(2, Math.toIntExact(entity.getOrganizator().getId()));
            pstmt.setObject(3, entity.getData());
            pstmt.setString(4,entity.getNume());
            pstmt.setString(5, entity.getDescriere());

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
                if(entity.getParticipanti()!=null)
                     entity.getParticipanti().forEach(x->addParticipanti(entity.getId(),x.getId()));

            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return  entity;

    }


    /**
     * Sterge atat din ev_participanti, cat si din eveniment
     * @param aLong ->se sterge evenimentul cu acest id
     * @return null
     */
    @Override
    public Eveniment delete(Long aLong) {

//
//        String SQL2 = "DELETE FROM ev_participanti WHERE id_user = ?";
//
//        int affectedrows2 = 0;
//
//        try (Connection conn = connect();
//             PreparedStatement pstmt = conn.prepareStatement(SQL2)) {
//
//            pstmt.setInt(1, Math.toIntExact(aLong));
//
//            affectedrows2 = pstmt.executeUpdate();
//
//        } catch (SQLException ex) {
//            System.out.println(ex.getMessage());
//        }

        String SQL = "DELETE FROM eveniment WHERE id = ?";
        if(findOne(aLong) == null ) throw  new RepoException("Nu exista evenimentul.");

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
     * Sterge un eveniment
     * @param aLong ->id event de sters
     */
    public void stergeEveniment(Long aLong) {


        String SQL = "DELETE FROM eveniment WHERE id = ?";

        int affectedrows = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, Math.toIntExact(aLong));

            affectedrows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }


    /**
     * Adauga noi participanti la un eveniment
     * @param entity ->event dat
     *          entity must not be null
     * @return evenimentul modificat
     */
    @Override
    public Eveniment update(Eveniment entity) {

        String SQL = "INSERT INTO ev_participanti(id_event, id_user) "

                + "VALUES(?,?)";

        int affectedrows = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            Long id= entity.getParticipanti().get(0).getId();
            System.out.println(id);
            pstmt.setInt(2,Math.toIntExact(entity.getParticipanti().get(0).getId()));
            pstmt.setInt(1, Math.toIntExact(entity.getId()));

            affectedrows = pstmt.executeUpdate();
            connect().close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return entity;
    }




    /**
     *
     * @return numarul total de events
     */
    @Override
    public int nrElem() {
        String SQL = "SELECT COUNT(*) FROM eveniment";

        int rez = 0;

        try (Connection conn = connect();
             Statement pstmt = conn.createStatement()) {

            ResultSet rs = pstmt.executeQuery(SQL);
            rs.next();
            rez = rs.getInt(1);
            return  rez;

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return rez;


    }


    /**
     *
     * @param id ->user
     * @return numarul de evenimente organizate de id
     */
    public int countMyEvents(long id) {
        String SQL = "SELECT COUNT(*) FROM eveniment where organizator = "+id;

        int rez = 0;

        try (Connection conn = connect();
             Statement pstmt = conn.createStatement()) {

            ResultSet rs = pstmt.executeQuery(SQL);
            rs.next();
            rez = rs.getInt(1);
            return  rez;

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return rez;


    }


    /**
     *
     * @param pageable ->pagina
     * @param id ->user (nefolosit)
     * @return toate evenimentele existente
     */
    @Override
    public Page<Eveniment> findAllFromOnePage(Pageable pageable, long id) {
        Page<Eveniment> p= new Page();
        p.setTotalCount(nrElem());
        p.setContent(this.findAllLimited(pageable, id));
        return p;
    }


    /**
     * Metoda folosita pentru a elimina din baza de date evenimentele ce au avut loc
     */
    private void curataBaza() {

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from eveniment ");

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                long id_u = resultSet.getLong(2);
                LocalDateTime date=resultSet.getObject(3,LocalDateTime.class);
                String nume=resultSet.getString(4);
                String desc=resultSet.getString(5);

                Utilizator u = repository.findOne(id_u);
                List<Utilizator> to = getParticipanti(id);
                Eveniment e=  new Eveniment(u,date, nume,desc);
                e.setId(id);
                if(to.size()!=0)
                    e.setParticipanti(to);
                if(e.getData().isBefore(LocalDateTime.now()))
                    stergeEveniment(e.getId());

            }
            connection.close();

        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}

    }


    /**
     *
     * @param pageable ->pagina
     * @param id ->user
     * @param id2 ->nefolosit
     * @return toate evenimentele organizate de id
     */
    @Override
    public Page<Eveniment> findAllFromOnePage2(Pageable pageable, long id, long id2) {

        Page<Eveniment> p= new Page();
        p.setTotalCount(countMyEvents(id));
        p.setContent(this.getMyEvents(pageable, id));
        return p;
    }




    /**
     *
     * @param pageable ->pagina
     * @param id ->user
     * @param id1 ->nefolosit
     * @param nume ->nefolosit
     * @return toate evenimentele la care participa  id
     */
    @Override
    public Page<Eveniment> findAllFromOnePage3(Pageable pageable, long id, long id1 , String nume) {

        Page<Eveniment> p= new Page();
        p.setTotalCount((int) particip);
        p.setContent(this.getMyParticipation(pageable, id));
        return p;
    }

    @Override
    public Page<Eveniment> findAllFromOnePage4(Pageable pageable, long id, String nume) {
        return null;
    }


    /**
     *
     * @param id ->id de user
     * @param id2 ->nefolosit
     * @return evenimentele la care participa un user
     */
    @Override
    public Iterable<Eveniment> findSomething(long id, long id2) {
        List<Eveniment> to  = new ArrayList<>();
        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from ev_participanti where id_user=?");
            statement.setInt(1,(int)id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id_event = resultSet.getLong("id_event");
                long id_user = resultSet.getLong("id_user");
                Eveniment ev= findOne(id_event);
                to.add(ev);
            }

            return to;
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}
        return to;

    }








    @Override
    public List<Eveniment> raport1(long id1, long id2 , LocalDateTime i, LocalDateTime s) {
        return null;
    }

    @Override
    public List<Eveniment> raport2(long id1, long id2) {
        return null;
    }

    @Override
    public Eveniment findUser(String data ,String pass) {
        return null;
    }

    @Override
    public Page<Utilizator> findAllWhoSentMessages(Pageable pageable, long id) {
        return null;
    }


}
