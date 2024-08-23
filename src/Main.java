import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.sql.*;
import java.io.*;



public class Main {

    private Connection connection;
    private List<MovieStar> mStars;
    private List<Movie> movies;
    private List<Star> stars;


    String path = "parserReport/";
    FileWriter overAll;
    FileWriter stars_dup;
    FileWriter mi_file;
    FileWriter md_file;
    FileWriter cast_file;
    FileWriter cast_dup;
    FileWriter genre_file;


    Set<String> seenActors = new HashSet<>();
    ArrayList<Star> duplicatesActors = new ArrayList<>();
    Set<String> seenMovie = new HashSet<>();
    ArrayList<Movie> duplicatesMovie = new ArrayList<>();
    ArrayList<Movie> inconsistentMovie = new ArrayList<>();
    Set<String> seenCast = new HashSet<>();
    ArrayList<MovieStar> duplicatesCast = new ArrayList<>();
    ArrayList<MovieStar> inconsistentCast = new ArrayList<>();
    Set<String> genreIncons = new HashSet<>();

    public Main() throws IOException {

        try {
            getConnection();
        }
        catch (Exception e) {
            System.out.println("no connection when obj created");
        }
        CastsParser castParser = new CastsParser();
        MainsParser mainParser = new MainsParser();
        ActorsParser actorParser = new ActorsParser();
        mainParser.runParser();
        actorParser.runParser();
        castParser.runParser();


        mStars = castParser.getCast();
        movies = mainParser.getMovies();
        stars = actorParser.getStars();

        File parserFolder = new File(path);
        if (!parserFolder.exists()) {
            parserFolder.mkdirs();
        }

        genre_file = new FileWriter(path + "Genre_Inconsistent.txt");
        overAll = new FileWriter(path + "OverAllData.txt");
        stars_dup = new FileWriter(path + "Stars_Dups.txt");
        mi_file = new FileWriter(path + "Movie_Inconsistent.txt");
        md_file = new FileWriter(path + "Movie_Dups.txt");
        cast_file = new FileWriter(path + "Cast_Inconsistent.txt");
        cast_dup = new FileWriter(path + "Cast_Dups.txt");

    }

    private void getConnection() throws Exception {
        // Incorporate mySQL driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Connect to the test database
        connection = DriverManager.getConnection("jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false",
                "mytestuser", "My6$Password");

        if (connection != null) {
            System.out.println("Connection established!!");
        }
        else {
            System.out.println("no connection");
        }
    }

    public void insertActors() throws IOException {

        for (Star s : stars) {
            if (!seenActors.contains(s.getName())) {
                seenActors.add(s.getName());
                try {
                    String query = "CALL stars_to_db(?, ?, ?, ?)";
                    CallableStatement statement = connection.prepareCall(query);
                    statement.setString(1,s.getId());
                    statement.setString(2,s.getName());
                    if (s.getYear() != 0) {
                        statement.setInt(3,s.getYear());
                        statement.registerOutParameter(4, Types.INTEGER);

                    }
                    else {
                        statement.setNull(3, java.sql.Types.INTEGER);
                        statement.registerOutParameter(4, Types.INTEGER);
                    }
                    statement.execute();
                    statement.close();
                    System.out.println("added star: " + s.getName());
                }
                catch (Exception e) {
                    System.out.println("caught exception at inserting");
                    System.out.println(e.getClass().getName());
                }
            }
            else
            {
                duplicatesActors.add(s);
            }
        }
    }

    public void insertMovies() throws IOException {

        for (Movie m : movies) {
            if (isConsistent(m) && !seenMovie.contains(m.getId())) {
                seenMovie.add(m.getId());
                //insert
                try {
                    String query = "CALL movie_to_db(?, ?, ?, ?, ?)";
                    try (CallableStatement statement = connection.prepareCall(query)) {
                        statement.setString(1, m.getId());
                        statement.setString(2, m.getTitle());
                        statement.setInt(3, m.getYear());
                        statement.setString(4, m.getDirector());
                        statement.registerOutParameter(5, Types.INTEGER);
                        statement.execute();
                        statement.close();
                        System.out.println("added movie: " + m.getTitle());
                        int rs = 0;
                        if (rs == 2) {
                            if (!duplicatesMovie.contains(m)) {
                                duplicatesMovie.add(m);
                            }
                        }
                        else {

                            String queryGenre = "CALL genres_to_db(?, ?)";
                            String temp = "";
                            try (CallableStatement statementGenre = connection.prepareCall(queryGenre)) {
                                for (String g : m.getGenres()) {
                                    statementGenre.setString(1, g);
                                    statementGenre.setString(2, m.getId());
                                    statementGenre.addBatch();
                                    temp = g;
                                }
                                statementGenre.executeBatch();
                                statementGenre.close();
                                System.out.println("added genres...");
                            } catch (SQLException e) {
                                genreIncons.add(temp);
                            }

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
                catch (Exception e) {
                    System.out.println("caught exception at inserting");
                    System.out.println(e);
                }
            }
            else if (seenMovie.contains(m.getId()))
            {
                if (!duplicatesMovie.contains(m)) {
                    duplicatesMovie.add(m);
                }
            }
            else
            {
                if (!inconsistentMovie.contains(m)) {
                    inconsistentMovie.add(m);
                }
            }
        }
    }

    public void insertCast() throws IOException {

        for (MovieStar c : mStars) {
            if (!seenCast.contains(c.getStarName())) {
                seenCast.add(c.getStarName());
                try {
                    String query = "CALL starsToTable(?, ?, ?, ?, ?)";
                    CallableStatement statement = connection.prepareCall(query);
                    statement.setString(1, c.getmId());
                    statement.setString(2, c.getStarName());
                    statement.setString(3, c.getTitle());
                    statement.registerOutParameter(4, Types.INTEGER);
                    statement.registerOutParameter(5, Types.VARCHAR);
                    statement.execute();
                    if (statement.getInt(4) == 2 || statement.getInt(4) == 3) {
                        if (!inconsistentCast.contains(c)) {
                            inconsistentCast.add(c);
                        }
                    }
                    if (statement.getInt(4) == 4) {
                        if (!duplicatesCast.contains(c)) {
                            duplicatesCast.add(c);
                        }
                    }

                } catch (Exception e) {
                    System.out.println("caught exception at inserting for cast");
                    System.out.println(e);
                }
            }
        }
    }

    //helper for insertMovies()
    private boolean isConsistent(Movie m) {
        if (m.getId() == null) {
            return false;
        }
        if (m.getDirector() == null) {
            return false;
        }
        if (m.getTitle() == null) {
            return false;
        }
        if (m.getGenres().size() == 0)
        {
            return false;
        }
        if (m.getYear() == 0)
        {
            return true;
        }
        return true;
    }

    public void writeToFile() throws IOException {
        genre_file.write("GENRES INCONSISTENCY\n");
        stars_dup.write("STARS DUPLICATES\n");
        stars_dup.write("----\n");

        mi_file.write("INCONSISTENT DATA\n");
        mi_file.write("----\n");
        md_file.write("MOVIE DUPLICATES\n");
        md_file.write("----\n");

        cast_file.write("INCONSISTENT DATA\n");
        cast_file.write("----\n");
        cast_dup.write("CAST DUPLICATES\n");
        cast_dup.write("----\n");

        for (Star s : duplicatesActors) {
            stars_dup.write("ID: " + s.getId() + "  |  Star Name: " + s.getName() + "  |  Birth Year: " + s.getYear() + "\n");
        }
        for (Movie m : duplicatesMovie) {
            md_file.write("ID: " + m.getId() + "  |  Movie Title: " + m.getTitle() + "  |  Movie Year: " + m.getYear() + "\n");
        }
        for (Movie m : inconsistentMovie) {
            mi_file.write("ID: " + m.getId() + "  |  Movie Title: " + m.getTitle() + "  |  Movie Year: " + m.getYear() + "\n");//
        }

        for (MovieStar c : duplicatesCast) {
            cast_dup.write("Movie ID: " + c.getmId() + "  |  Movie Title: " + c.getTitle() + "  |  Cast Name: " + c.getStarName() + "\n");
        }
        for (MovieStar c : inconsistentCast) {
            cast_file.write("Movie ID: " + c.getmId() + "  |  Movie Title: " + c.getTitle() + "  |  Cast Name: " + c.getStarName() + "\n");
        }

        for (String c : genreIncons) {
            genre_file.write(c + "\n");
        }


        overAll.write("\nOVERALL DATA FOR STARS: \n");
        System.out.println("\nOVERALL DATA FOR STARS: \n");
        System.out.println("added " + seenActors.size() + " actors");
        System.out.println("found " + duplicatesActors.size() + " duplicate actors");
        overAll.write("added " + seenActors.size() + " actors\n");
        overAll.write("found " + duplicatesActors.size() + " duplicate actors\n");


        System.out.println("\nOVERALL DATA FOR MOVIES: \n");
        overAll.write("\nOVERALL DATA FOR MOVIES: \n");
        System.out.println("added " + seenMovie.size() + " movies");
        System.out.println("there were " + duplicatesMovie.size() + " duplicate movies");
        System.out.println("there were " + inconsistentMovie.size() + " inconsistent movies");
        overAll.write("added " + seenMovie.size() + " movies\n");
        overAll.write("there were " + duplicatesMovie.size() + " duplicate movies\n");
        overAll.write("there were " + inconsistentMovie.size() + " inconsistent movies\n");


        overAll.write("\nOVERALL DATA FOR CAST: \n");
        System.out.println("\nOVERALL DATA FOR CAST: \n");
        System.out.println("added " + seenCast.size() + " cast members");
        System.out.println("there were " + duplicatesCast.size() + " duplicate cast members");
        System.out.println("there were " + inconsistentCast.size() + " inconsistent cast members");
        overAll.write("added " + seenCast.size() + " cast members");
        overAll.write("there were " + duplicatesCast.size() + " duplicate cast members");
        overAll.write("there were " + inconsistentCast.size() + " inconsistent cast members");

        overAll.write("\nOVERALL DATA FOR GENRE: \n");
        System.out.println("\nOVERALL DATA FOR GENRE: \n");
        System.out.println("there were " + genreIncons.size() + " inconsistent genres");

        overAll.close();
        stars_dup.close();
        mi_file.close();
        md_file.close();
        cast_file.close();
        cast_dup.close();
        genre_file.close();

    }

}
