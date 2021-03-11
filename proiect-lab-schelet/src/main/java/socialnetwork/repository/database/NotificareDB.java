package socialnetwork.repository.database;

import socialnetwork.domain.*;

import socialnetwork.repository.PagingRepository;
import socialnetwork.repository.page.Page;
import socialnetwork.repository.page.Pageable;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;



public class NotificareDB implements PagingRepository<Long, Notificare> {
    private final String url;
    private final String username;
    private final String password;
    private PagingRepository<Long,Utilizator> repository;
    private PagingRepository<Long, Eveniment> evRepo;

    public NotificareDB(String url, String username, String password,
                               PagingRepository<Long,Utilizator> repository,
                                PagingRepository<Long, Eveniment> evRepo) {
        this.url = url;
        this.repository=repository;
        this.username = username;
        this.evRepo=evRepo;
        this.password = password;
    }

    /**
     * Cauta o notificare
     * @param aLong ->id-ul notificarii cautate
     * @return  notificarea
     */
    @Override
    public Notificare findOne(Long aLong) {
        String SQL = "SELECT *"
                + "FROM notificari "
                + "WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, Math.toIntExact(aLong));
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                Long id = rs.getLong("id");
                Long id_catre = rs.getLong("catre");
                Long id_event= rs.getLong("id_event");
                String desc=rs.getString("descriere");
                LocalDateTime data = rs.getObject( 5,LocalDateTime.class);
                int repeat =rs.getInt(6);

                Eveniment e= evRepo.findOne(id_event);
                Utilizator catre=repository.findOne(id_catre);

                Notificare n =new Notificare(e,catre);
                n.setId(id);
                n.setData(data);
                n.setRepeat(repeat);
                n.setDescriere(desc);
            }


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return null;

    }



    /**
     *
     * @return toate notificarile
     */
    @Override
    public Iterable<Notificare> findAll() {
        Set<Notificare> notificareSet = new HashSet<>();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from notificari");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id_user = resultSet.getLong("catre");
                Long id_event = resultSet.getLong("id_event");
                String desc=resultSet.getString("descriere");
                LocalDateTime data = resultSet.getObject( 5,LocalDateTime.class);
                int re=resultSet.getInt(6);


                Utilizator u=repository.findOne(id_user);
                Eveniment e= evRepo.findOne(id_event);
                Notificare c= new Notificare(e,u);
                c.setDescriere(desc);
                c.setData(data);
                c.setRepeat(re);
                c.setId(id);

                notificareSet.add(c);

            }
            return notificareSet;
        }
        catch(SQLException e){
            e.printStackTrace();}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        return notificareSet;

    }


    /**
     *
     * @param pageable ->pagina
     * @param i ->id de user
     * @return pagina cu notificarile primite de un anumit user
     */
    public Iterable<Notificare> findAllLimited(Pageable pageable ,long i) {
        Set<Notificare> notificareSet = new HashSet<>();
        int offset =( pageable.getPageNumber())* pageable.getPageSize();


        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from notificari  where catre =?  limit ? offset ?");
            statement.setInt(1,(int) i);
            statement.setInt(2,pageable.getPageSize());
            statement.setInt(3,offset);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id_user = resultSet.getLong("catre");
                Long id_event = resultSet.getLong("id_event");
                String desc=resultSet.getString("descriere");
                LocalDateTime data = resultSet.getObject( 5,LocalDateTime.class);
                int re=resultSet.getInt(6);


                Utilizator u=repository.findOne(id_user);
                Eveniment e= evRepo.findOne(id_event);
                Notificare c= new Notificare(e,u);
                c.setData(data);
                c.setDescriere(desc);
                c.setId(id);
                c.setRepeat(re);

                notificareSet.add(c);

            }
            Comparator<Notificare> ComparatorM
                    = Comparator.comparing(
                    Notificare::getData).reversed()
                    ;

            List<Notificare> list =new ArrayList<>(notificareSet);
            list.sort(ComparatorM);
            return list;
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}

        return notificareSet;

    }


    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }


    /**
     * Salveaza o noua notificare
     * @param entity ->notificarea de salvat
     *         entity must be not null
     * @return ->notificarea salvata
     */
    @Override
    public Notificare save(Notificare entity) {

        String SQL = "INSERT INTO notificari(id,catre,id_event,descriere,datac,repeat) VALUES(?,?,?,?,?,?)";

        long id = 0;


        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {


            pstmt.setInt(1, Math.toIntExact(entity.getId()));
            pstmt.setInt(2, Math.toIntExact(entity.getCatre().getId()));
            pstmt.setInt(3, Math.toIntExact(entity.getEvent().getId()));
            pstmt.setString(4,entity.getDescriere());
            pstmt.setObject(5,entity.getData());
            pstmt.setInt(6,entity.getRepeat());

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
     * Sterge o notificare
     * @param aLong ->id-ul unei notificari
     * @return null
     */
    @Override
    public Notificare delete(Long aLong) {

        String SQL = "DELETE FROM notificari where id = ?";

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
     * Modifica descrierea unei notificari
     * @param c -> o notificare
     * @return notificarea modificata
     */
    @Override
    public Notificare update(Notificare c ){
        String SQL = "UPDATE notificari "
                + "SET descriere = ? "
                + "WHERE id = ?";

        int affectedrows = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {


            pstmt.setString(1, c.getDescriere());
            pstmt.setInt(2, Math.toIntExact(c.getId()));

            affectedrows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return c;


    }


    /**
     *
     * @return numarul total de notificari
     */
    @Override
    public int nrElem() {
        String SQL = "SELECT COUNT(*) FROM notificari";

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
     * @param i ->id user
     * @return numarul de notificari primite de un user
     */
    public int nrElemUser(long i) {
        String SQL = "SELECT COUNT(*) FROM notificari where catre = "+i;

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
     * @param id ->user dat
     * @return notificarile primite de un user
     */
    @Override
    public Page<Notificare> findAllFromOnePage(Pageable pageable, long id) {
        Page<Notificare> p= new Page();
        p.setTotalCount(nrElemUser(id));
        p.setContent(this.findAllLimited(pageable, id));
        return p;
    }


    /**
     *
     * @param catre ->user
     * @param event ->eveniment
     * @return notificarile primite un user in legatura cu un eveniment
     */
    @Override
    public Iterable<Notificare> findSomething(long catre, long event) {
        Set<Notificare> notificareSet = new HashSet<>();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT *" +
                    " FROM notificari where catre= ? and id_event= ? order by repeat desc");
            statement.setInt(1, Math.toIntExact(catre));
            statement.setInt(2, Math.toIntExact(event));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id_user = resultSet.getLong("catre");
                Long id_event = resultSet.getLong("id_event");
                String desc=resultSet.getString("descriere");
                LocalDateTime data = resultSet.getObject( 5,LocalDateTime.class);
                int re =resultSet.getInt(6);


                Utilizator u=repository.findOne(id_user);
                Eveniment e= evRepo.findOne(id_event);
                Notificare c= new Notificare(e,u);
                c.setDescriere(desc);
                c.setData(data);
                c.setId(id);
                c.setRepeat(re);

                notificareSet.add(c);


            }
            Comparator<Notificare> ComparatorM
                    = Comparator.comparing(
                    Notificare::getRepeat, Integer::compareTo);

            List<Notificare> list =new ArrayList<>(notificareSet);
            list.sort(ComparatorM);
           // list.forEach(System.out::println);
            return list;
        }
        catch(SQLException e){
            e.printStackTrace();}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        return notificareSet;
    }








    @Override
    public Page<Notificare> findAllFromOnePage2(Pageable pageable, long id, long id2) {
        return null;
    }

    @Override
    public Page<Notificare> findAllFromOnePage3(Pageable pageable, long id, long id1, String nume) {
        return null;
    }

    @Override
    public Page<Notificare> findAllFromOnePage4(Pageable pageable, long id, String nume) {
        return null;
    }

    @Override
    public List<Notificare> raport1(long id1, long id2, LocalDateTime i, LocalDateTime s) {
        return null;
    }

    @Override
    public List<Notificare> raport2(long id1, long id2) {
        return null;
    }
    @Override
    public Notificare findUser(String data ,String pass) {
        return null;
    }

    @Override
    public Page<Utilizator> findAllWhoSentMessages(Pageable pageable, long id) {
        return null;
    }


}
