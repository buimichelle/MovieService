use moviedb;

INSERT INTO employees(email, password, fullname) VALUES("classta@email.edu", "classta", "CS122B TA");
CREATE INDEX idx_movie ON movies(title, year, director);
CREATE INDEX idx_starr ON stars(name, birthYear);
CREATE INDEX idx_genres ON genres(name);
CREATE INDEX idx_genres_in_movies ON genres_in_movies(genreId, movieId);
CREATE INDEX idx_stars_in_movies ON stars_in_movies(starId, movieId);


ALTER TABLE movies ADD FULLTEXT(title);
