- # General
    - #### Team#: 
        Oat
    
    - #### Names: 
        Michelle Bui, Estefany Siguantay-Cortez
    
    - #### Project 5 Video Demo Link:
        https://www.youtube.com/watch?v=6S6hm2QKFdA

    - #### Instruction of deployment: 
        web application is deployed and load balancer is configured

    - #### Collaborations and Work Distribution:

        Estefany: Task 1, Task 4
        Michelle: Task 2, Task 3, Task 4


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.

    all in src file
    AddMovieServlet.java, AddStarServlet.java, AlphabetServlet.java, AndroidLogin.java, Autocomplete.java, Confirmation.java, EmployeeLoginServlet.java, GenreServlet.java, HomeServlet.java, LoginServlet.java, MetabaseServlet.java, PaymentServlet.java, SearchServlet.java, SingleMovieServlet.java, SingleStarServlet.java  

    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.

    In the Fabflix code there are multiple connections being opened up in many difference servlets, instead of opening up a new connection each time with the data pooling these connections are instead are opened, closed, and then able to be reused by other servlets throughout the code. This speeds things up because the connections are being reused instead of made new each time.
    
    - #### Explain how Connection Pooling works with two backend SQL.

    In the file /etc/apache2/sites-enabled/000-default.conf the configuration is set so that the load balancer can send requests to either of the 2 backends, connection pooling will continue to reuse connections. 
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

    all in src file
    AddMovieServlet.java, AddStarServlet.java, AlphabetServlet.java, AndroidLogin.java, Autocomplete.java, Confirmation.java, EmployeeLoginServlet.java, GenreServlet.java, HomeServlet.java, LoginServlet.java, MetabaseServlet.java, PaymentServlet.java, SearchServlet.java, SingleMovieServlet.java, SingleStarServlet.java

    - #### How read/write requests were routed to Master/Slave SQL?
    
    The load balancer will route the requests to the master or slave (sessions will stay in the instance they are initially sent to), if it's a write-read request or route to either the slave or master SQL if the request to the db.

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.

    Compile log_processing.java (located inside of src)
  
        "javac log_processing.java"
  
    Run using the command line and include the path of the log file as an argument
  
        ex: "java log_processing /path/to/log/file.txt"
  
            (log files format is "ts, tj\n")

- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTPS/10 threads                       | ![img/SINGLE_HTTPS10.jpeg]   | 153 ms                     |  4.824236 ms                        | 42618394.173928 ms        | overall speed not too slow, not too fasr           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![img/SCALED_HTTP1.jpeg]     | 78 ms                      |  4123651ms                         |  9146532435743 ms         | only faster than 10 threads          |
| Case 2: HTTP/10 threads                        | ![img/SCALED_HTTP10.jpeg]    | 101 ms                     | 3.585120 ms                         | 8121769.369555 ms         |     slower than 1 thread      |
| Case 3: HTTP/10 threads/No connection pooling  | ![img/SCALED-HTTPNOCON.png]   | 10ms                        | 4123651 ms                                  | 9146532435743 ms                       | fastest of the three           |









# Fabflix Project 4
By Michelle Bui and Estefany Siguantay-Cortez, CS 122B Fall 2023

### DEMO URL:
The demo video is located:https://youtu.be/6OyD95aFS3c

### Michelle Bui's Contributions(p4):
Task 1, Readme
### Estefany Siguantay-Cortez's Contributions(p4):
Task 2, AWS Demonstration

### How to run our site on your own machine:
1. Delete database and reinsert movie-data.sql and create-table.sql if neccessary
2. Run the xml-procedure.sql (in terminal:  mysql -u "mytestuser" -p < xml-procedure.sql)
3. Run the stored-procedure.sql (in terminal: mysql -u "mytestuser" -p < stored-procedure.sql)
4. Run the create-index.sql (in terminal:  mysql -u "mytestuser" -p < create_index.sql)
5. Run UpdateSecurePassword.java ONCE
6. Run UpdateEmployeeSecurePassword.java ONCE
7. Run Multithreading.java to parse through the XML.
8. Ready to launch the website
9. Launch the android file on another window! (It builds by gradle).
   
### File Names with Prepared Statements:
SearchServlet.java, 

SingleStarServlet.java, 

SingleMovieServlet.java,

MetabaseServlet.java, 

LoginServlet.java, 

HomeServlet.java, 

GenreServlet.java, 

EmployeeLoginServlet.java, 

Confirmation.java, 

AlphabetServlet.java, 

AddStarServlet.java, 

AddMovieServlet.java

### Two parsing time optimization strategies compared with the naive approach.
Multithreading & Stored Procedures with indexing. Both strategies allowed us to go down drastically from over 15 minutes to ~1-6 minutes. Our intrepretation of the naive approach is inserting each statement one-by-one, which could takes a lot of times. We know that the xml files in total have over 10,000 possible statements to insert. Multi-threading, as mentioned in class, allows us to write multiple insert statements at once. We were able to run adding stars and adding movies at the same time. Afterward, we were able to do the same thing with the inserting cast members alone. We were also able to utilize stored procedures with indexing. Stored procedure helps reduce network traffics since everything is done in the database. Indexing allows us to not do a full table scan, which saves us a lot of time. Most of the time when we are adding something to the table, we are basically only checking if the id exists so just indexing works. We don't need to check the whole table!


### Inconsistent Data Reports
Actual datas can be found in the following files: Stars_Dups, Movie_Inconsistent, Movie_Dups, Cast_Inconsistent, Cast_Dups. The files are created after the parser and inserter are finished.

How we distingush inconsistency datas is based off what we found in the XML files. We found that the MovieID for the same movie title are different in the cast124.xml and mains243.xml. They differ between only a few letters and therefore, it is inconsistent and the actors for that movie does not get added in. Also, during the parsing of the xml files, if any of the elements are missing important information like movie title, it is considered inconsistent. 


#### This is our overall data.

OVERALL DATA FOR STARS: 

added 6839 actors
found 24 duplicate actors

OVERALL DATA FOR MOVIES: 

added 8729 movies
there were 35 duplicate movies
there were 3351 inconsistent movies

OVERALL DATA FOR CAST: 

added 17550 cast members
there were 2282 duplicate cast members
there were 9317 inconsistent cast members

OVERALL DATA FOR GENRE: 

there were 4 inconsistent genres


### Michelle Bui's Contributions(p3):
Task 3, Task 4, Task 5, Task 6
### Estefany Siguantay-Cortez's Contributions(p3):
Task 1, Task 2, Task 3 for SearchServlet, Task 4 for AWS, Task 6

### Michelle Bui's Contributions(p2):
Both Michelle and Estefany worked on the log-in tasks. However Michelle focused on the query side. Michelle created the browsing codes (by letter/genre) and the home page where the links for those belong. For the results pages/movie list pages, Michelle created the pagnitation and sorting. Michelle did majority of task 3 with Estefany helping with some jump functionality. Michelle only worked on creating the check out page with the quantitiy editing and deleting.
### Estefany Siguantay-Cortez's Contributions(p2):
Both Michelle and Estefany worked on the log-in tasks. However Estefany focused on the coding/file side. Estefany worked on the searching codes, like the query. Estefany worked on majority of the last task 4 and some CSS styling.

### Michelle Bui's Contributions(p1):
Michelle set up the project on Github and on Intellj. She created the skeletons, based off the API class example. To split up the work evenly, Michelle's goal was to finish the single-movie pages. She also took some time to quickly learn CSS styling to create the minimal decorations (blue backgrounds, page layouts, etc.). They split up the work for the index pages, but Estefany did majority of the SQL queries while Michelle created the rest of the JS files. She created the demo video.

### Estefany Siguantay-Cortez's Contributions(p1):
Estefany set up the project on AWS and figured out the mechanics on how AWS works.  To split up the work evenly, Estefany's goal was to finish the single-star pages. They split up the work for the index pages, but Estefany did majority of the SQL queries while Michelle created the rest of the JS files. She helped with the demo video and setting up the AWS instance to create the demo video.






