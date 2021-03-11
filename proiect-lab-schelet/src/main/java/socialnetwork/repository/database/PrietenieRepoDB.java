package socialnetwork.repository.database;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;



public class PrietenieRepoDB implements Repository<Tuple<Long,Long>, Prietenie> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Utilizator> validator;

    public PrietenieRepoDB(String url, String username, String password, Validator<Utilizator> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }


    /**
     *
     * @param longLongTuple ->id-ul prieteniei
     * @return prietenia data de un id
     */
    @Override
    public Prietenie findOne(Tuple<Long, Long> longLongTuple) {

        String SQL = "SELECT id_1,id_2,datac "
                + "FROM prietenie "
                + "WHERE id_1 = ? AND id_2 = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, Math.toIntExact(longLongTuple.getLeft()));
            pstmt.setInt(2, Math.toIntExact(longLongTuple.getRight()));

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                Long id_1 = rs.getLong("id_1");
                Long id_2 = rs.getLong("id_2");
                LocalDateTime data = rs.getObject( 3,LocalDateTime.class);


                Prietenie u= new Prietenie();
                u.setId(new Tuple<>(id_1,id_2));
                u.setDate(data);
                return u;
            }


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    /**
     *
     * @return toate prieteniile din tabelul prietenie
     */
    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> prietenies = new HashSet<>();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from prietenie");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id_1");
                Long id2= resultSet.getLong("id_2");
                LocalDateTime data = resultSet.getObject( 3,LocalDateTime.class);



                Prietenie pr =new Prietenie();
                pr.setId(new Tuple<>(id1,id2));
                pr.setDate(data);
             //   pr.setTime(timp);
                prietenies.add(pr);
            }
            return prietenies;
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}

        return prietenies;

    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }


    /**
     * Adauga o prietenie  in tabel
     * @param entity ->prietenia de adaugat
     *         entity must be not null
     * @return prietenia adaugata
     */
    @Override
    public Prietenie save(Prietenie entity) {

        String SQL = "INSERT INTO prietenie(id_1,id_2, datac) VALUES(?,?,?)";

        long id = 0;


        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {


            pstmt.setInt(1, Math.toIntExact(entity.getId().getLeft()));
            pstmt.setInt(2, Math.toIntExact(entity.getId().getRight()));
            pstmt.setObject(3, entity.getDate());


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
        return entity;
    }


    /**
     * Sterge o prietenie
     * @param longLongTuple ->id-ul prieteniei de sters
     * @return null
     */
    @Override
    public Prietenie delete(Tuple<Long, Long> longLongTuple) {
        String SQL = "DELETE FROM prietenie WHERE id_1 = ? AND id_2 =?";

     //   if(findOne(longLongTuple) == null) throw  new ServiceException("Nu exista prietenia");
        int affectedrows = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, Math.toIntExact(longLongTuple.getLeft()));
            pstmt.setInt(2, Math.toIntExact(longLongTuple.getRight()));


            affectedrows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }




    /**
     *
     * @return numarul de prietenii
     */
    @Override
    public int nrElem() {
        String SQL = "SELECT COUNT(*) FROM prietenie";

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






    @Override
    public Prietenie update(Prietenie entity) {
        return null;
    }
}
