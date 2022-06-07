package be.bf;

import java.security.SecureRandom;
import java.sql.*;

public class LeaderBoard {

    Connection conn = null;
    Statement stmt = null;
    String dbPath;

    public LeaderBoard(String dbPath) {
        this.dbPath = "/home/rvdemael/Code/sqlite/demo.db";
        connect(dbPath);
        if(this.conn!=null) {
            createTable();
            display();
            try {
                this.conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }


    }

    public void connect(String dbPath) {
        try {
            String url = "jdbc:sqlite:"+dbPath;
            conn = DriverManager.getConnection(url);
            createTable();
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void createTable()  {
        StringBuilder sqlDDL = new StringBuilder();
        sqlDDL.append("CREATE TABLE IF NOT EXISTS LeaderBoard (\n")
                .append(" id integer PRIMARY KEY AUTOINCREMENT,\n")
                .append(" pseudo VARCHAR(16) NOT NULL,\n")
                .append(" score INTEGER NOT NULL );");
        try {
            this.stmt = this.conn.createStatement();
            this.stmt.execute(sqlDDL.toString());
            this.stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert() {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO  LeaderBoard (pseudo,score)\n")
                .append(" VALUES \n");
        SecureRandom sr = new SecureRandom();
        final int nPseudo = sr.nextInt(50,100);
        for (int i=0;i<nPseudo;++i) {
            final int score = sr.nextInt(1000,50000);
            final int pseudoLength = sr.nextInt(3,17);
            StringBuilder pseudo = new StringBuilder();
            for(int j=0;j<pseudoLength;++j ) {
                pseudo.append((char) sr.nextInt(48,91));
            }
            sql.append("\t(\"").append(pseudo.toString()).append("\",")
                    .append(String.valueOf(score)).append(")").append(i==nPseudo-1?";":",").append("\n");
        }

        try {
            this.stmt = this.conn.createStatement();
            this.stmt.executeUpdate(sql.toString());
            this.stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private ResultSet select() {
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("SELECT pseudo,score FROM LeaderBoard ORDER BY score LIMIT 25;");
        ResultSet res = null;
        try {
            this.stmt = this.conn.createStatement();
            res = this.stmt.executeQuery(sqlQuery.toString());
            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void display() {
        StringBuilder print = new StringBuilder("======== LEADERBOARD ========\n");
        ResultSet res = this.select();

        if(res!=null) {
            try {
                System.out.printf("%b\n",res.next());
                while (res.next()) {
                    String pseudo = res.getString("pseudo");
                    final int score = res.getInt("score");
                    print.append(pseudo).append(" : ").append(String.valueOf(score)).append("\n");
                }
                this.stmt.close();//close the statement from this.select because ifclosed in closed res is empty
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(print.toString());
    }

}
