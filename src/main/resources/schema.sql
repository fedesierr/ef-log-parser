-- noinspection SqlNoDataSourceInspectionForFile

-- noinspection SqlDialectInspectionForFile
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