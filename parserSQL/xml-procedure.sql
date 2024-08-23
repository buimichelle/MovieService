use moviedb;
DROP PROCEDURE IF EXISTS movie_to_db;
DROP PROCEDURE IF EXISTS stars_to_db;
DROP PROCEDURE IF EXISTS genres_to_db;
DROP PROCEDURE IF EXISTS starsToTable;


DELIMITER $$
CREATE PROCEDURE movie_to_db ( IN id VARCHAR(10), IN title VARCHAR(100), IN releaseYear INT, IN director VARCHAR(100), OUT added INT)
BEGIN
    DECLARE titleExists INT;
    DECLARE temp INT;
    SET added = 0;
    START TRANSACTION;
    -- Check if the movie already exists using id
SELECT COUNT(*) INTO titleExists FROM movies m WHERE  m.id = id LIMIT 1;
    IF titleExists = 0 THEN
        -- Insert the movie if it doesn't exist
        INSERT INTO movies(id, title, year, director) VALUES(id, title, releaseYear, director);
        INSERT INTO ratings(movieId, rating, numVotes) VALUES (id, 0.0, 0);
        SET added = 1;
    ELSE
        -- Set added to 2 if the movie already exists
        SET added = 2;
    END IF;
    COMMIT;
END
$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE stars_to_db (IN id VARCHAR(10), IN starName VARCHAR(100), IN birthYear INT, OUT added INT)
BEGIN
    DECLARE starExists INT;
    SET added = 0;
    START TRANSACTION;
    -- Check if the star already exists
    SELECT COUNT(*) INTO starExists FROM stars s where s.name = starName and s.birthYear = birthYear LIMIT 1;
    IF starExists = 0 THEN
        -- Insert the star if it doesn't exist
        INSERT INTO stars(id, name, birthYear) VALUES(id, starName, birthYear);
        SET added = 1;
    ELSE
        -- Set added to 2 if the star already exists
        SET added = 2;
    END IF;
    COMMIT;
END
$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE genres_to_db (IN gName VARCHAR(32), IN movieID VARCHAR(10))
BEGIN
    DECLARE genreEXISTS INT;
    DECLARE gID INT;
	DECLARE MovieExist INT;



    START TRANSACTION;
    -- Check if the star already exists
    SELECT COUNT(*) INTO genreEXISTS FROM genres g WHERE g.name = gName LIMIT 1;
    IF genreEXISTS = 0 THEN
        -- Insert the star if it doesn't exist
        INSERT INTO genres(name) VALUES(gName);
        --  get id
        SELECT id INTO gID FROM genres g WHERE g.name = gName;
	
        -- add genre to the genres_to_movie table
        INSERT INTO genres_in_movies( genreId, movieId) VALUES(gID, movieID);
        -- Set added to 1 if the star does not  exists

    ELSE
        --  get id
        SELECT id INTO gID FROM genres g WHERE g.name = gName;
        -- add genre to the genres_to_movie table
# 		SELECT COUNT(*) INTO MovieExist FROM movies m where m.id = movieID Limit 1;
# 		IF MovieExist = 1 THEN
		INSERT INTO genres_in_movies( genreId, movieId) VALUES(gID, movieID);
# 		END IF;
        -- Set added to 2 if the star already exists

    END IF;
    COMMIT;
END
$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE starsToTable (
    IN movId VARCHAR(10),
    IN starName VARCHAR(100),
    IN title VARCHAR(100),
    OUT added VARCHAR(10),
    OUT added1 VARCHAR(10)
)
BEGIN
    DECLARE movieEXIST INT;
    DECLARE starEXIST INT;
    DECLARE starID VARCHAR(10);
    DECLARE movieId2 VARCHAR(10);

    SET added = '0';
    SET added1 = '1';

    START TRANSACTION;

    SELECT COUNT(*) INTO movieEXIST FROM movies m WHERE m.id = movId AND m.title = title;
    SELECT COUNT(*) INTO starEXIST FROM stars s WHERE s.name = starName;

    IF movieEXIST > 0 THEN
        IF starEXIST > 0 THEN
            SELECT id INTO starID FROM stars s WHERE s.name = starName LIMIT 1;
            SELECT id INTO movieId2 FROM movies m WHERE m.id = movId AND m.title = title LIMIT 1;

            IF movId = movieId2 THEN
                INSERT INTO stars_in_movies(starId, movieId) VALUES(starID, movId);
                SET added = '1';
            ELSE
                SET added = '2';
                SET added1 = movieId2;
            END IF;
        ELSE
            SET added = '3';
        END IF;
    ELSE
        SET added = '4';
    END IF;

    COMMIT;
END
$$

DELIMITER ;


