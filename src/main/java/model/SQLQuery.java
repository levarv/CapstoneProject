package model;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class SQLQuery {

    private static final String DATABASE_URL = "jdbc:derby:firstdb";

    public static ArrayList<Song> getSongs() {

        ArrayList<Song> songs = new ArrayList<>();

        String query = "SELECT * FROM SONG";

        try (
                Connection connection =
                        DriverManager.getConnection(DATABASE_URL);

                Statement statement =
                        connection.createStatement();

                ResultSet resultSet =
                        statement.executeQuery(query)
        ) {

            ResultSetMetaData meta = resultSet.getMetaData();

            while (resultSet.next())
                songs.add( new Song( new File((String) resultSet.getObject(7))));

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return songs;
    }

    public static ArrayList<Song> getSongs(String q) {

        ArrayList<Song> songs = new ArrayList<>();

        try (
                Connection connection =
                        DriverManager.getConnection(DATABASE_URL);

                Statement statement =
                        connection.createStatement();

                ResultSet resultSet =
                        statement.executeQuery(q)
        ) {

            ResultSetMetaData meta = resultSet.getMetaData();

            while (resultSet.next())
                songs.add( new Song( new File((String) resultSet.getObject(7))));

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return songs;
    }

    public static void addSong(File songFile) {

        // Change this after checking the printed database columns.
        String query = "INSERT INTO SONG (FILE_PATH) VALUES (?)";

        try (
                Connection connection =
                        DriverManager.getConnection(DATABASE_URL);

                PreparedStatement statement =
                        connection.prepareStatement(query)
        ) {

            statement.setString(
                    1,
                    songFile.getAbsolutePath()
            );

            statement.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void update(String update) {

        try (
                Connection connection =
                        DriverManager.getConnection(DATABASE_URL);

                Statement statement =
                        connection.createStatement()
        ) {

            statement.executeUpdate(update);

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

}