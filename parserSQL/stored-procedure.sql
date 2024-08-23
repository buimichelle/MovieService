use moviedb;
DROP PROCEDURE IF EXISTS add_movie;

DELIMITER $$

CREATE PROCEDURE add_movie (
    IN title VARCHAR(100),
    IN releaseYear INT,
    IN director VARCHAR(100),
    IN star VARCHAR(100),
    IN genre VARCHAR(32),
    OUT movieAdded INT,
    OUT MovieID VARCHAR(10),
    OUT GenreID INT,
    OUT StarID VARCHAR(10)
)
BEGIN
    
    DECLARE titleExists INT;

    -- Check if the title exists
    SELECT COUNT(*) INTO titleExists FROM movies m WHERE m.title = title AND m.year = releaseYear AND m.director = director;

    IF titleExists = 0 THEN
        SET movieAdded = 1;

        -- Create the new movieID
        SELECT concat(LEFT(MAX(id), REGEXP_INSTR(MAX(id), '[^A-Za-z]') - 1), REGEXP_REPLACE(MAX(id), '[^0-9]', '')+1) INTO MovieID FROM movies;

        -- Add new movie
        INSERT INTO movies(id, title, year, director) VALUES(MovieID, title, releaseYear, director);
        INSERT INTO ratings(movieId, rating, numVotes) VALUES (MovieID, 0.0, 0);


        -- Add new and existing stars
        SELECT id INTO StarID FROM stars s WHERE s.name = star LIMIT 1;
        IF StarID IS NULL THEN
            SELECT concat(LEFT(MAX(id), REGEXP_INSTR(MAX(id), '[^A-Za-z]') - 1), REGEXP_REPLACE(MAX(id), '[^0-9]', '')+1) INTO StarID FROM stars;
            INSERT INTO stars(id, name) VALUES(StarID, star);
            INSERT INTO stars_in_movies(starId, movieId) VALUES(StarID, MovieID);
        ELSE
            INSERT INTO stars_in_movies(starId, movieId) VALUES(StarID, MovieID);
        END IF;

        -- Add new and existing genres
        SELECT id INTO GenreID FROM genres g WHERE g.name = genre LIMIT 1;
        IF GenreID IS NULL THEN
            INSERT INTO genres(name) VALUES(genre);
            SELECT id INTO GenreID FROM genres g where g.name = genre;
            INSERT INTO genres_in_movies(genreId, movieId) VALUES(GenreID, MovieID);
        ELSE
            INSERT INTO genres_in_movies(genreId, movieId) VALUES(GenreID, MovieID);
        END IF;

        SET movieAdded = 2;
    ELSE
        SET movieAdded = 0;
    END IF;
END
$$
DELIMITER ; 

