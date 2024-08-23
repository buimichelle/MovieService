DROP DATABASE IF EXISTS moviedb;
CREATE DATABASE moviedb;

USE moviedb;

CREATE TABLE movies (
    id VARCHAR(10) PRIMARY KEY NOT NULL,
    title VARCHAR(100) NOT NULL,
    year INT NOT NULL,
    director VARCHAR(100) NOT NULL
);

CREATE TABLE stars (
    id VARCHAR(10) PRIMARY KEY NOT NULL,
    name VARCHAR(100) NOT NULL,
    birthYear INT
);

CREATE TABLE stars_in_movies (
    starId VARCHAR(10) NOT NULL,
    movieId VARCHAR(10) NOT NULL,
    FOREIGN KEY (starId) REFERENCES stars(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE genres (
    id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    name VARCHAR(32) NOT NULL
);

CREATE TABLE genres_in_movies (
    genreId INT NOT NULL,
    movieId VARCHAR(10) NOT NULL,
    FOREIGN KEY (genreId) REFERENCES genres(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);
CREATE TABLE creditcards(
    id VARCHAR(20) PRIMARY KEY NOT NULL,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    expiration date NOT NULL
);
CREATE TABLE customers (
    id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    ccId VARCHAR(20) NOT NULL,
    address VARCHAR(200) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    FOREIGN KEY (ccId) REFERENCES creditcards(id)
);

CREATE TABLE sales (
    id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    customerId INT NOT NULL,
    movieId VARCHAR(10) NOT NULL,
    saleDate date NOT NULL,
    FOREIGN KEY (customerId) REFERENCES customers(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);


CREATE TABLE ratings(
    movieId VARCHAR(10) NOT NULL,
    rating float NOT NULL,
    numVotes INT NOT NULL,
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE employees(
    email varchar(50) primary key,
    password varchar(20) not null,
    fullname varchar(100)
  );
