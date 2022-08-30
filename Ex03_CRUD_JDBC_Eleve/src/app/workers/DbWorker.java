package app.workers;

import app.beans.Personne;
import app.exceptions.MyDBException;
import app.helpers.DateTimeLib;
import app.helpers.SystemLib;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbWorker implements DbWorkerItf {

    private Connection dbConnexion;
    private List<Personne> listePersonnes;
    private int index = 0;

    /**
     * Constructeur du worker
     */
    public DbWorker() {
    }

    @Override
    public void connecterBdMySQL(String nomDB) throws MyDBException {
        final String url_local = "jdbc:mysql://localhost:3306/" + nomDB;
        final String url_remote = "jdbc:mysql://LAPEMFB37-21.edu.net.fr.ch:3306/" + nomDB;
        final String user = "root";
        final String password = "emf123";

        System.out.println("url:" + url_remote);
        try {
            dbConnexion = DriverManager.getConnection(url_remote, user, password);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void connecterBdHSQLDB(String nomDB) throws MyDBException {
        final String url = "jdbc:hsqldb:file:" + nomDB + ";shutdown=true";
        final String user = "SA";
        final String password = "";
        System.out.println("url:" + url);
        try {
            dbConnexion = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void connecterBdAccess(String nomDB) throws MyDBException {
        final String url = "jdbc:ucanaccess://" + nomDB;
        System.out.println("url=" + url);
        try {
            dbConnexion = DriverManager.getConnection(url);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void deconnecter() throws MyDBException {
        try {
            if (dbConnexion != null) {
                dbConnexion.close();
            }
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public List<Personne> lirePersonnes() throws MyDBException {
        listePersonnes = new ArrayList<>();
        try {
            Statement st = dbConnexion.createStatement();
            ResultSet rs = st.executeQuery("select PK_PERS, Prenom, Nom, Date_naissance, No_rue, Rue, NPA, Ville, Actif, Salaire, date_modif from t_personne");
            while (rs.next()){
                int pk = rs.getInt("PK_PERS");
                String nom = rs.getString("Nom");
                String prenom = rs.getString("Prenom");
                Date dateNaissance = rs.getDate("Date_naissance");
                int noRue = rs.getInt("No_rue");
                String rue = rs.getString("Rue");
                int npa = rs.getInt("NPA");
                String ville = rs.getString("Ville");
                boolean actif = rs.getBoolean("Actif");
                double salaire = rs.getDouble("Salaire");
                Date dateModif = rs.getDate("date_modif");
                Personne pers = new Personne(pk, nom, prenom, dateNaissance, noRue, rue, npa, ville, actif, salaire, dateModif);
                listePersonnes.add(pers);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listePersonnes;
    }

    @Override
    public Personne lire(int lastPK) {
        return listePersonnes.get(lastPK);
    }

    @Override
    public void modifier(Personne p1) throws MyDBException {
        String prep = "update t_personne set Prenom=?, Nom=?, Date_naissance=?, No_rue=?, Rue=?, NPA=?, Ville=?, Actif=?, Salaire=?, date_modif=? where PK_PERS=?";
        try (PreparedStatement ps= dbConnexion.prepareStatement(prep)){
            ps.setString(1, p1.getPrenom());
            ps.setString(2, p1.getNom());
            ps.setDate(3, DateTimeLib.stringToSqldate(p1.getDateNaissance().toString()));
            ps.setInt(4, p1.getNoRue());
            ps.setString(5, p1.getRue());
            ps.setInt(6, p1.getNpa());
            ps.setString(7, p1.getLocalite());
            ps.setBoolean(8, p1.isActif());
            ps.setDouble(9, p1.getSalaire());
            ps.setDate(10, DateTimeLib.stringToSqldate(p1.getDateModif().toString()));
            int nb = ps.executeUpdate();
            if(nb != 1 ) {
                throw new MyDBException("modifier", "Erreur de mise à jour");
            }
        } catch (SQLException ex) {
            
        }
    }

    @Override
    public void effacer(Personne p) throws MyDBException {
        //String prep = "DELETE from t_personne where PK_PERS=" + p.getPkPers();
        String prep = "DELETE from t_personne where PK_PERS=?";
        try (PreparedStatement ps = dbConnexion.prepareStatement(prep)){
            ps.setInt(1, p.getPkPers());
            int nb = ps.executeUpdate();
            if(nb != 1){
                throw new MyDBException("effacer", "Erreur de suppression");
            }
        } catch (SQLException ex) {
            
        }
    }

    @Override
    public void creer(Personne federer) {
        String prep = "INSERT INTO t_personne VALUES (PK_PERS, Prenom, Nom, Date_naissance, No_rue, Rue, NPA, Ville, Actif, Salaire, date_modif) "
                    + "VALUES (? , '?' , '?' , '?' , ? , '?' , ? , '?' , ? , ? , '?') ";
        try (PreparedStatement ps= dbConnexion.prepareStatement(prep)){
            ps.setInt(1, federer.getPkPers());
            ps.setString(2, federer.getPrenom());
            ps.setString(3, federer.getNom());
            ps.setDate(4, DateTimeLib.stringToSqldate(federer.getDateNaissance().toString()));
            ps.setInt(5, federer.getNoRue());
            ps.setString(6, federer.getRue());
            ps.setInt(7, federer.getNpa());
            ps.setString(8, federer.getLocalite());
            ps.setBoolean(9, federer.isActif());
            ps.setDouble(10, federer.getSalaire());
            ps.setDate(11, DateTimeLib.stringToSqldate(federer.getDateModif().toString()));
        } catch (SQLException ex) {
            Logger.getLogger(DbWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
