package socialnetwork.repository.database;

import socialnetwork.domain.CerereDePrietenie;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.PagingRepository;
import socialnetwork.repository.Repository;
import socialnetwork.repository.page.Page;
import socialnetwork.repository.page.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;



public class CerereDePrietenieDB implements PagingRepository<Long, CerereDePrietenie> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<CerereDePrietenie> validator;
    private Repository<Long,Utilizator> repository;

    public CerereDePrietenieDB(String url, String username, String password, Validator<CerereDePrietenie> validator,
                               Repository<Long,Utilizator> repository) {
        this.url = url;
        this.repository=repository;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    /**
     * Cauta o cerere de prietenie
     * @param aLong ->id-ul cererii cautate
     * @return cererea de prietenie
     */
    @Override
    public CerereDePrietenie findOne(Long aLong) {
        String SQL = "SELECT *"
                + "FROM cereredeprietenie "
                + "WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, Math.toIntExact(aLong));
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                Long id = rs.getLong("id");
                Long id_1 = rs.getLong("id_1");
                Long id_2 = rs.getLong("id_2");

                String status  = rs.getString("status");
                LocalDateTime data = rs.getObject( 5,LocalDateTime.class);

                Utilizator u1=repository.findOne(id_1);
                Utilizator u2=repository.findOne(id_2);

                CerereDePrietenie u =new CerereDePrietenie(u1,u2);
                u.setId(id);
                u.setStatus(status);
                u.setData(data);
                return u;
            }


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return null;

    }


    /**
     *
     * @return toate cererile de prietenie
     */
    @Override
    public Iterable<CerereDePrietenie> findAll() {
        Set<CerereDePrietenie> cereri = new HashSet<>();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from cereredeprietenie");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id_1 = resultSet.getLong("id_1");
                Long id_2 = resultSet.getLong("id_2");
                String status= resultSet.getString("status");
                LocalDateTime data = resultSet.getObject( 5,LocalDateTime.class);


                Utilizator u1=repository.findOne(id_1);
                Utilizator u2=repository.findOne(id_2);
                CerereDePrietenie c = new CerereDePrietenie(u1,u2);
                c.setId(id);
                c.setStatus(status);
                c.setData(data);
                cereri.add(c);

            }
            return cereri;
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}

        return cereri;

    }


    /**
     *
     * @param pageable ->pagina
     * @param i ->user
     * @return toate cererile de prietenie trimise/primite de i
     */
    public Iterable<CerereDePrietenie> findAllLimited( Pageable pageable, long i) {
        Set<CerereDePrietenie> cereri = new HashSet<>();
        int offset =( pageable.getPageNumber())* pageable.getPageSize();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from cereredeprietenie where id_1 =? or id_2 = ? limit ? offset ?");

            statement.setInt(1, Math.toIntExact(i));
            statement.setInt(2, (int) i);
            statement.setInt(3,pageable.getPageSize());
            statement.setInt(4,offset);


            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id_1 = resultSet.getLong("id_1");
                Long id_2 = resultSet.getLong("id_2");
                String status= resultSet.getString("status");
                LocalDateTime data = resultSet.getObject( 5,LocalDateTime.class);

                if(!status.equals("pending"))
                    delete(id);
                else{
                    Utilizator u1=repository.findOne(id_1);
                    Utilizator u2=repository.findOne(id_2);
                    CerereDePrietenie c = new CerereDePrietenie(u1,u2);
                    c.setId(id);
                    c.setStatus(status);
                    c.setData(data);

                     cereri.add(c);
                }

            }
            return cereri;
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}

        return cereri;

    }


    /**
     *
     * @param pageable ->pagina
     * @param i ->user
     * @return toate cererile de prietenie trimise de i
     */
    public Iterable<CerereDePrietenie> findCereriTrimise( Pageable pageable, long i) {
        Set<CerereDePrietenie> cereri = new HashSet<>();
        int offset =( pageable.getPageNumber())* pageable.getPageSize();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from cereredeprietenie where id_1 =?  limit ? offset ?");

            statement.setInt(1, Math.toIntExact(i));

            statement.setInt(2,pageable.getPageSize());
            statement.setInt(3,offset);


            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id_1 = resultSet.getLong("id_1");
                Long id_2 = resultSet.getLong("id_2");
                String status= resultSet.getString("status");
                LocalDateTime data = resultSet.getObject( 5,LocalDateTime.class);

                if(!status.equals("pending"))
                    delete(id);
                else{
                    Utilizator u1=repository.findOne(id_1);
                    Utilizator u2=repository.findOne(id_2);
                    CerereDePrietenie c = new CerereDePrietenie(u1,u2);
                    c.setId(id);
                    c.setStatus(status);
                    c.setData(data);

                    cereri.add(c);
                }

            }
            return cereri;
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}

        return cereri;

    }


    /**
     *
     * @param pageable ->pagina
     * @param i ->user
     * @return toate cererile de prietenie primite de i
     */
    public Iterable<CerereDePrietenie> findCereriPrimite( Pageable pageable, long i) {
        Set<CerereDePrietenie> cereri = new HashSet<>();
        int offset =( pageable.getPageNumber())* pageable.getPageSize();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from cereredeprietenie where id_2 = ? limit ? offset ?");

            statement.setInt(1, Math.toIntExact(i));
            statement.setInt(2,pageable.getPageSize());
            statement.setInt(3,offset);


            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id_1 = resultSet.getLong("id_1");
                Long id_2 = resultSet.getLong("id_2");
                String status= resultSet.getString("status");
                LocalDateTime data = resultSet.getObject( 5,LocalDateTime.class);

                if(!status.equals("pending"))
                    delete(id);
                else{
                    Utilizator u1=repository.findOne(id_1);
                    Utilizator u2=repository.findOne(id_2);
                    CerereDePrietenie c = new CerereDePrietenie(u1,u2);
                    c.setId(id);
                    c.setStatus(status);
                    c.setData(data);

                    cereri.add(c);
                }

            }
            return cereri;
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}

        return cereri;

    }



    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }


    /**
     * Salveaza o noua cerere de prietenie
     * @param entity ->cererea de prietenie de salvat
     *         entity must be not null
     * @return ->cererea de prietenie salvata
     */
    @Override
    public CerereDePrietenie save(CerereDePrietenie entity) {

        String SQL = "INSERT INTO cereredeprietenie(id, id_1,id_2,status,datac) VALUES(?,?,?,?,?)";

        long id = 0;


        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {


            pstmt.setInt(1, Math.toIntExact(entity.getId()));
            pstmt.setInt(2, Math.toIntExact(entity.getTrimite().getId()));
            pstmt.setInt(3, Math.toIntExact(entity.getPrimeste().getId()));
            pstmt.setString(4, entity.getStatus());
            pstmt.setObject(5, entity.getData());


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


        return  entity;

    }


    /**
     * Sterge o cerere de prietenie
     * @param aLong ->id-ul unei cereri de prietenie
     * @return null
     */
    @Override
    public CerereDePrietenie delete(Long aLong) {

        String SQL = "DELETE FROM cereredeprietenie WHERE id = ?";
        if(findOne(aLong) == null ) throw  new ValidationException("Nu exista cererea.");

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
     * Modifica statusul unei cereri de prietenie
     * @param c -> o cerere de prietenie
     * @return cererea de prietenie modificata
     */
    @Override
    public CerereDePrietenie update(CerereDePrietenie c ){
        String SQL = "UPDATE cerereDePrietenie "
                + "SET status = ? "
                + "WHERE id = ?";

        int affectedrows = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {


            pstmt.setString(1, c.getStatus());
            pstmt.setInt(2, Math.toIntExact(c.getId()));

            affectedrows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return c;

    }


    /**
     *
     * @return numarul total de cereri de prietenie
     */
    @Override
    public int nrElem() {
        String SQL = "SELECT COUNT(*) FROM cereredeprietenie";

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
     * @param id ->id user
     * @return numarul de cereri de prietenie trimise de id
     */
    public int nrTrimise(long id) {
        String SQL = "SELECT COUNT(*) FROM cereredeprietenie where id_1 = "+id;

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
     * @param id ->id user
     * @return numarul de cereri de prietenie primite de id
     */
    public int nrPrimite(long id) {
        String SQL = "SELECT COUNT(*) FROM cereredeprietenie where id_2 = "+id;

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
     * @param id ->id user
     * @return numarul de cereri de prietenie trimise/primite de id
     */
    public int nrElemUser(long id) {
        String SQL = "SELECT COUNT(*) FROM cereredeprietenie where id_1= "+id+"or id_2= "+id+" and status = 'pending'";

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
     * @param id ->id de user
     * @return toate cererile de prietenie ale unui user
     */
    @Override
    public Page<CerereDePrietenie> findAllFromOnePage(Pageable pageable ,long id) {
        Page<CerereDePrietenie> p= new Page();
        p.setTotalCount(nrElemUser(id));
        p.setContent(this.findAllLimited(pageable, id));
        return p;
    }

    /**
     *
     * @param pageable ->pagina
     * @param id ->user
     * @param id2 ->nefolosit
     * @return    cererile trimise de userul curent
     */

    @Override
    public Page<CerereDePrietenie> findAllFromOnePage2(Pageable pageable, long id, long id2) {
        Page<CerereDePrietenie> p= new Page();
        p.setTotalCount(nrTrimise(id));
        p.setContent(this.findCereriTrimise(pageable, id));
        return p;
    }



    /**
     *
     * @param pageable ->pagina
     * @param id ->user
     * @param id1 ->nefolosit
     * @param nume ->nefolosit
     * @return  cererile primite de userul curent
     */
    @Override
    public Page<CerereDePrietenie> findAllFromOnePage3(Pageable pageable, long id, long id1, String nume) {
        Page<CerereDePrietenie> p= new Page();
        p.setTotalCount(nrPrimite(id));
        p.setContent(this.findCereriPrimite(pageable, id));
        return p;
    }

    @Override
    public Page<CerereDePrietenie> findAllFromOnePage4(Pageable pageable, long id, String nume) {
        return null;
    }


    /**
     *
     * @param id1 ->user 1
     * @param id2 ->user 2
     * @return cererile de prietenie trimise de id1 catre id2 si invers
     */
    @Override
    public Iterable<CerereDePrietenie> findSomething(long id1, long id2) {

        Set<CerereDePrietenie> cereri = new HashSet<>();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from cereredeprietenie where (id_1 =? and id_2 =?) or (id_1= ? and id_2 = ?)");
            statement.setInt(1,(int)id1);
            statement.setInt(2,(int)id2);

            statement.setInt(3,(int)id2);
            statement.setInt(4,(int)id1);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id_1 = resultSet.getLong("id_1");
                Long id_2 = resultSet.getLong("id_2");
                String status= resultSet.getString("status");
                LocalDateTime data = resultSet.getObject( 5,LocalDateTime.class);


                Utilizator u1=repository.findOne(id_1);
                Utilizator u2=repository.findOne(id_2);
                CerereDePrietenie c = new CerereDePrietenie(u1,u2);
                c.setId(id);
                c.setStatus(status);
                c.setData(data);
                cereri.add(c);

            }
            if(cereri.size()==0) return  null;
            return cereri;
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}

        return null;

    }




    @Override
    public CerereDePrietenie findUser(String data,String pass) {
        return null;
    }

    @Override
    public Page<Utilizator> findAllWhoSentMessages(Pageable pageable, long id) {
        return null;
    }


    @Override
    public List<CerereDePrietenie> raport1(long id1, long id2 , LocalDateTime i, LocalDateTime s) {
        return null;
    }

    @Override
    public List<CerereDePrietenie> raport2(long id1, long id2) {
        return null;
    }
}
