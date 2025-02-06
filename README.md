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










