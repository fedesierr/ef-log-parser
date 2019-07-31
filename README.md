# ef-log-parser

The goal is to write a parser in Java that parses web server access log file, loads the log to MySQL and checks if a given IP makes more than a certain number of requests for the given duration. 


#### DB Schema

    drop table if exists access_logs;
    
    CREATE TABLE IF NOT EXISTS access_logs (
        id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
        date DATETIME NOT NULL,
        ip VARCHAR(45) NOT NULL,
        method VARCHAR(20) NOT NULL,
        http_status VARCHAR(40) NOT NULL,
        user_agent VARCHAR(255) NOT NULL
    );
    
    CREATE INDEX id_access_logs_date ON access_logs (date);
    
    drop table if exists detected_ips;
    
    CREATE TABLE IF NOT EXISTS detected_ips (
        id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
        ip VARCHAR(45) NOT NULL,
        total INT(10) NOT NULL,
        reason VARCHAR(255) NOT NULL
    );
    
#### Query to find IPs that mode more than a certain number of requests for a given time period.

    SELECT 
        ip, COUNT(id) AS total
    FROM
        access_logs
    WHERE
        `date` BETWEEN '2017-01-01 15:00:00' AND '2017-01-01.16:00:00'
    GROUP BY ip
    HAVING COUNT(id) > 200
    
##### Query to find requests made by a given IP.

    SELECT 
        *
    FROM
        access_logs
    WHERE
        ip = '192.168.11.231'