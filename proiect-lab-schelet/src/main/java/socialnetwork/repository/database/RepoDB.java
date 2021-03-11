package socialnetwork.repository.database;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.RepoException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.PagingRepository;
import socialnetwork.repository.page.Page;
import socialnetwork.repository.page.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;



public class RepoDB implements PagingRepository<Long, Utilizator> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Utilizator> validator;

    long nr= 0;
    long nrNon=0;

    public RepoDB(String url, String username, String password, Validator<Utilizator> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    /**
     * Va returna utilizator dat de id-ul sau
     * @param aLong ->un id de utilizator
     * @return utilizator gasit(sau null, daca nu exista)
     */
    @Override
    public Utilizator findOne(Long aLong) {

        String SQL = "SELECT *"
                + "FROM utilizatori "
                + "WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, Math.toIntExact(aLong));
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email =rs.getString("email");
                String pass=rs.getString("pass");
                int abonat=rs.getInt("abonat");

                Utilizator u = new Utilizator(firstName, lastName);
             //   System.out.println(u.toString());
                u.setId(aLong);
                u.setEmail(email);
                u.setPassword(pass);
                u.setAbonat(abonat);
                return u;
            }


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return null;

    }



    /**
     *
     * @return toti utilizatorii din tabela utilizatori
     */
    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from utilizatori");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email =resultSet.getString("email");
                String  pass=resultSet.getString("pass");
                int abonat=resultSet.getInt(6);

                Utilizator utilizator = new Utilizator(firstName, lastName);
                utilizator.setId(id);
                utilizator.setAbonat(abonat);
                utilizator.setPassword(pass);
                utilizator.setEmail(email);
                users.add(utilizator);

            }
            connection.close();
            return users;
        }
         catch(SQLException | ClassNotFoundException e){
                e.printStackTrace();}

        return users;

    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }


    /**
     * Se introduce o noua entitate in tabel
     * @param entity ->un utilizator de adaugat in tabela
     *         entity must be not null
     * @return utilizatorul adaugat
     */
    @Override
    public Utilizator save(Utilizator entity) {

        String SQL = "INSERT INTO utilizatori(id,first_name,last_name,email,pass,abonat) VALUES(?,?,?,?,?,?)";

        long id = 0;
        long idaux=entity.getId();
        while(findOne(idaux)!=null){
            idaux ++;
        }

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {


            pstmt.setInt(1, Math.toIntExact(idaux));
            pstmt.setString(2, entity.getFirstName());
            pstmt.setString(3, entity.getLastName());
            pstmt.setString(4, entity.getEmail());
            pstmt.setString(5, entity.getPassword());
            pstmt.setInt(6,entity.getAbonat());

            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                        connect().close();

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
     * Se sterge un utilizator, dat prin id-ul sau
     * @param aLong ->id utilizator de sters
     * @return null
     */
    @Override
    public Utilizator delete(Long aLong) {

        String SQL = "DELETE FROM utilizatori WHERE id = ?";
        if(findOne(aLong) == null ) throw  new RepoException("Nu exista utilizatorul.");

        int affectedrows = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, Math.toIntExact(aLong));

            affectedrows = pstmt.executeUpdate();
            connect().close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }


    /**
     * Modifica prenumele unui utilizator
     * @param id ->id-ul unui utilizator existent
     * @param firstname ->prenumele ce inlocuieste prenumele curent
     */
    public  void setBDFirstName(long id, String firstname ){
        String SQL = "UPDATE utilizatori "
                + "SET first_name = ? "
                + "WHERE id = ?";

        int affectedrows = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {


            pstmt.setString(1, firstname);
            pstmt.setInt(2, Math.toIntExact(id));

            affectedrows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }


    /**
     * Modifica numele de familie a unui utilizator
     * @param id ->id-ul unui utilizator existent
     * @param lastname -> numele ce inlocuieste numele curent

     */
    public void setBDLastName(long id, String lastname){
        String SQL = "UPDATE utilizatori "
                + "SET last_name = ? "
                + "WHERE id = ?";

        int affectedrows = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, lastname);
            pstmt.setInt(2, Math.toIntExact(id));

            affectedrows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }


    /**
     * Modifica coloana abonat
     * @param id ->user
     * @param abonat -> noua valoare
     */
    public void updateAbonat(long id, int abonat){
        String SQL = "UPDATE utilizatori "
                + "SET abonat = ? "
                + "WHERE id = ?";

        int affectedrows = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, abonat);
            pstmt.setInt(2, Math.toIntExact(id));

            affectedrows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }


    /**
     *
     * @param pageable ->pentru a calcula offset
     * @param i ->user dat
     * @return toti userii cu care e prieten  un user dat
     */
    public Iterable<Utilizator> findAllLimited(  Pageable pageable, long i) {
        Set<Utilizator> users = new HashSet<>();
        int offset =( pageable.getPageNumber())* pageable.getPageSize();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from prietenie where id_1 =? or id_2 =? limit ? offset ?");
            statement.setInt(1,(int)i);
            statement.setInt(2, (int )i );
            statement.setInt(3,pageable.getPageSize());
            statement.setInt(4,offset);


            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id_1=resultSet.getLong(1);
                long id_2=resultSet.getLong(2);
                if(id_1 !=i){
                    Utilizator u =findOne(id_1);
                    users.add(u);
                }

                if(id_2!=i) {
                        Utilizator u = findOne(id_2);
                        users.add(u);
                }


            }
            connection.close();
            return users;
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}

        return users;

    }



    /**
     *
     * @param pageable ->pentru a calcula offset
     * @param i ->user dat
     * @return toti userii cu care nu e prieten  un user dat
     */
    public Iterable<Utilizator> findNonFriendsLimited(  Pageable pageable, long i) {
        Set<Utilizator> users = new HashSet<>();
        int offset =( pageable.getPageNumber())* pageable.getPageSize();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM utilizatori U where id != ? and (not exists(select * "+
                  "  from prietenie where  id_1= ?  and id_2=U.id or "+
                  "  id_1=U.id and id_2= ? ))"+
                    "and (not exists(select * "+
                            " from cereredeprietenie where  id_1= ?  and id_2=U.id or "+
                    " id_1=U.id and id_2= ? )) limit ? offset ?");


            statement.setInt(1,(int) i);
            statement.setInt(2,(int) i);
            statement.setInt(3,(int) i);
            statement.setInt(4,(int) i);
            statement.setInt(5,(int) i);

            statement.setInt(6,pageable.getPageSize());
            statement.setInt(7,offset);


            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
              long id=resultSet.getLong(1);
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email =resultSet.getString("email");
                String  pass=resultSet.getString("pass");
                int abonat=resultSet.getInt(6);

                Utilizator utilizator = new Utilizator(firstName, lastName);
                utilizator.setId(id);
                utilizator.setAbonat(abonat);
                utilizator.setPassword(pass);
                utilizator.setEmail(email);
                users.add(utilizator);



            }
            connection.close();
            return users;
        }
        catch(SQLException e){
            e.printStackTrace();}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        return users;

    }


    /**
     *
     * @param pageable ->pagina
     * @param i ->id user
     * @param nume ->nume dupa care se filtreaza
     * @return pagina cu userii care nu sunt prieteni (si nici nu au cereri de
     * prietenie primite/trimise de i), filtrate dupa nume
     */
    public Iterable<Utilizator> findNonFriendsFiler(  Pageable pageable, long i, String nume) {
        Set<Utilizator> users = new HashSet<>();
        int offset =( pageable.getPageNumber())* pageable.getPageSize();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM utilizatori U where id != ? and (not exists(select * "+
                    "  from prietenie where  id_1= ?  and id_2=U.id or "+
                    "  id_1=U.id and id_2= ? ))"+
                    "and (not exists(select * "+
                    " from cereredeprietenie where  id_1= ?  and id_2=U.id or "+
                    " id_1=U.id and id_2= ? )) and (first_name LIKE '" +nume+"%' OR last_name LIKE '"+nume+"%') limit ? offset ?");


            statement.setInt(1,(int) i);
            statement.setInt(2,(int) i);
            statement.setInt(3,(int) i);
            statement.setInt(4,(int) i);
            statement.setInt(5,(int) i);

            statement.setInt(6,pageable.getPageSize());
            statement.setInt(7,offset);


            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id=resultSet.getLong(1);
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email =resultSet.getString("email");
                String  pass=resultSet.getString("pass");
                int abonat=resultSet.getInt(6);

                Utilizator utilizator = new Utilizator(firstName, lastName);
                utilizator.setId(id);
                utilizator.setAbonat(abonat);
                utilizator.setPassword(pass);
                utilizator.setEmail(email);
                users.add(utilizator);



            }
            connection.close();
            nrNon=users.size();
            return users;
        }
        catch(SQLException e){
            e.printStackTrace();}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        return users;

    }


    /**
     * Returnez toti userii care nu sunt egali cu cel dat ca parametru
     * @param pageable -> de la ce pagina incep datele
     * @param i ->id de user
     * @return un set de useri
     */
    public Iterable<Utilizator> findEveryone(  Pageable pageable, long i, String nume) {
        Set<Utilizator> users = new HashSet<>();
        int offset =( pageable.getPageNumber())* pageable.getPageSize();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM utilizatori U where id != ? and (first_name LIKE '" +nume+"%' OR last_name LIKE '"+nume+"%')" +
                   " limit ? offset ?");
            statement.setInt(1,(int) i);

            statement.setInt(2,pageable.getPageSize());
            statement.setInt(3,offset);


            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id=resultSet.getLong(1);
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email =resultSet.getString("email");
                String  pass=resultSet.getString("pass");
                int abonat=resultSet.getInt(6);

                Utilizator utilizator = new Utilizator(firstName, lastName);
                utilizator.setId(id);
                utilizator.setAbonat(abonat);
                utilizator.setPassword(pass);
                utilizator.setEmail(email);
                users.add(utilizator);



            }
            connection.close();
            return users;
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}

        return users;

    }



    /**
     * Se modifica numele si prenumele unui utilizator existent
     * @param entity ->datele cu care se inlocuiesc cele curente
     *          entity must not be null
     * @return utilizatorul modificat
     */
    @Override
    public Utilizator update(Utilizator entity) {
        setBDFirstName(entity.getId(), entity.getFirstName());
        setBDLastName(entity.getId(), entity.getLastName());
        updateAbonat(entity.getId(), entity.getAbonat());
        return findOne(entity.getId());
    }


    /**
     *
     * @param id ->un user
     * @return numarul de useri cu care userul curent nu e prieten
     */
    public int nrNonFriends(long id) {
        String SQL = "SELECT COUNT(*) FROM utilizatori U where id != "+id+
                " and (not exists(select * " +
                " from prietenie " +
                " where  id_1= "+id+ " and id_2=U.id or " +
                "id_1=U.id and id_2=" +id+"))"+
                " and (not exists(select * " +
                "    from cereredeprietenie where  id_1= "+id+"  and id_2=U.id or " +
                "     id_1=U.id and id_2= "+id+" )) ";

        int rez = 0;

        try (Connection conn = connect();
             Statement pstmt = conn.createStatement()) {

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
     * @param id ->user
     * @return numarul de useri care nu sunt egali cu un user dat
     */
    public int nrUsers(long id) {
        String SQL = "SELECT COUNT(*) FROM utilizatori U where id != "+id;

        int rez = 0;

        try (Connection conn = connect();
             Statement pstmt = conn.createStatement()) {

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
     * @return numarul de elemente din tabela
     */
    @Override
    public int nrElem() {
        String SQL = "SELECT COUNT(*) FROM utilizatori ";

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
     * @return numarul de prieteni ai userului dat
     */
    public int nrPrieteniUser(long id) {
        String SQL = "SELECT COUNT(*) FROM prietenie where id_1= "+id+"or id_2= "+id;

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
     * @param id ->user
     * @return toti prietenii userului dat
     */
    @Override
    public Page<Utilizator> findAllFromOnePage(Pageable pageable,long id) {
        Page<Utilizator> p= new Page();
        p.setTotalCount(nrPrieteniUser(id));
        p.setContent(this.findAllLimited(pageable, id));
        return p;
    }


    /**
     *
     * @param pageable ->pagina
     * @param id ->id user
     * @param id ->nefolosit
     * @return pagina cu utilizatorii care nu sunt prieteni cu userul cu id id
     */
    @Override
    public Page<Utilizator> findAllFromOnePage2(Pageable pageable, long id , long id2) {
        Page<Utilizator> p= new Page();
        p.setTotalCount(nrNonFriends(id));
        p.setContent(this.findNonFriendsLimited(pageable,id));
        return p;    }



    /**
     *
     * @param pageable ->pagina
     * @param id ->id user
     * @param id1 ->nefolosit
     * @param nume ->nefolosit
     * @return pagina cu toti userii, mai putin cel cu id-ul id
     */
    @Override
    public Page<Utilizator> findAllFromOnePage3(Pageable pageable, long id, long id1 , String nume) {
        Page<Utilizator> p= new Page();
        p.setTotalCount(nrUsers(id));
        p.setContent(this.findEveryone(pageable,id,nume));
        return p;
    }

    @Override
    public Page<Utilizator> findAllFromOnePage4(Pageable pageable, long id, String nume) {
        Page<Utilizator> p= new Page();
        p.setTotalCount((int) nrNon);
        p.setContent(this.findNonFriendsFiler(pageable,id,nume));
        return p;
    }


    /**
     *
     * @param id1 ->id de user
     * @param id2 ->nefolosit
     * @param inc ->data de inceput
     * @param sf ->data de sfarsit
     * @return  toti prietenii userului dat
     */
    @Override
    public List<Utilizator> raport1(long id1, long id2,LocalDateTime inc, LocalDateTime sf) {

      //  select * from prietenie where id_1=15 or id_2 =15;
        List<Utilizator> users = new ArrayList<>();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from prietenie where id_1= ? or id_2 = ?");

            statement.setInt(1,(int) id1);
            statement.setInt(2,(int) id1);


            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id=resultSet.getLong(1);
                long id_1 = resultSet.getLong("id_1");
                long  id_2 = resultSet.getLong("id_2");
                LocalDateTime data =resultSet.getObject(3,LocalDateTime.class);

                if(data.isBefore(sf) && data.isAfter(inc)) {
                    if (id_1 != id1) {
                        Utilizator u1 = findOne(id_1);
                        users.add(u1);
                    }
                    if (id_2 != id1) {
                        Utilizator u1 = findOne(id_2);
                        users.add(u1);
                    }
                }




            }
            connection.close();
            return users;
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}

        return users;

    }


    /**
     *
     * @param data ->email
     * @param parola ->parola
     * @return userul cu email-ul si parola data
     */
    @Override
    public Utilizator findUser(String data ,String parola) {
        String SQL = "SELECT *"
                + "FROM utilizatori "
                + "WHERE email = ? and pass = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1,data);
            pstmt.setString(2,parola);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                long id=rs.getLong(1);
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email =rs.getString("email");
                String pass=rs.getString("pass");
                int abonat=rs.getInt("abonat");

                Utilizator u = new Utilizator(firstName, lastName);
                //   System.out.println(u.toString());
                u.setId(id);
                u.setEmail(email);
                u.setPassword(pass);
                u.setAbonat(abonat);
                return u;
            }


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return null;

    }


    /**
     *
     * @param id ->id de user
     * @return id-urile userilor ce i-au trimis mesaj userului cu id id si
     * id-urile userilor care au primit mesaj de la userul cu id id
     */
    public List<Long> getIdUseri(long id){
        List<Long> users = new ArrayList<>();

        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select distinct from_u,id_user from message inner join messagereceiver " +
                    " on id=id_mesaj where (from_u = ? or id_user=?)");
            statement.setInt(1,(int)id);
            statement.setInt(2, (int )id );



            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id_1=resultSet.getLong("from_u");
                long id_2=resultSet.getLong("id_user");
                if(id_1 !=id){
                    users.add(id_1);
                }

                if(id_2!=id) {
                    users.add(id_2);
                }


            }
           return  users;
        }
        catch(SQLException e){
            e.printStackTrace();}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        return  users;

    }


    /**
     *
     * @param pageable ->pagina
     * @param id ->id dat de utilizator
     * @return pagina cu userii ce au trimis /primit mesaje de userul cu id id
     */
    @Override
    public Page<Utilizator> findAllWhoSentMessages(Pageable pageable, long id) {
        Set<Utilizator> users = new HashSet<>();
        int offset =( pageable.getPageNumber())* pageable.getPageSize();
        List<Long> idUsers= getIdUseri(id);
        String lista=" ( "+idUsers.get(0);
        idUsers.remove(0);
        for(Long l: idUsers){
            lista+=", "+l.toString();
        }
        lista+=")";
        System.out.println("Lista= "+lista +" end.");
        try {

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from utilizatori where id in  "+lista+" limit ? offset ?");

           // statement.setString(1,lista);

            statement.setInt(1,pageable.getPageSize());
            statement.setInt(2,offset);


            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id_user=resultSet.getLong(1);
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email =resultSet.getString("email");
                String  pass=resultSet.getString("pass");
                int abonat=resultSet.getInt(6);

                Utilizator utilizator = new Utilizator(firstName, lastName);
                utilizator.setId(id_user);
                utilizator.setAbonat(abonat);
                utilizator.setPassword(pass);
                utilizator.setEmail(email);
                users.add(utilizator);



            }
            connection.close();
            Page<Utilizator> p= new Page();
            p.setTotalCount(users.size());
            p.setContent(users);
            System.out.println("database");
            p.getContent().forEach(System.out::println);
            return p;
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();}

        return  null;
    }









    @Override
    public List<Utilizator> raport2(long id1, long id2) {
        return null;
    }

    @Override
    public Iterable<Utilizator> findSomething(long id, long id2) {
        return  null;
    }



}
