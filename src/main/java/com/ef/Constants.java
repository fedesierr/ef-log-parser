package com.ef;

/**
 * @author fsierra on 2019-07-29
 */
public interface Constants {

    String DATE_FORMAT = "yyyy-MM-dd H:m:s.S";

    String PARAM_DATE_FORMAT = "yyyy-MM-dd.HH:mm:ss";

    String DELIMITER = "|";

    Character QUOTE = '"';

    String[] NAMES = new String[]{"date", "ip", "method", "httpStatus", "userAgent"};

    Integer CHUNK_SIZE = 2000;

    String INSERT_QUERY = "INSERT INTO access_logs (`date`, `ip`, `method`, `http_status`, `user_agent`) " +
                                "VALUES (:date, :ip, :method, :httpStatus, :userAgent)";

        String CHECK_IP_QUERY = "SELECT ip, count(id) as total " +
                                    "FROM access_logs " +
                                    "WHERE `date` BETWEEN :startDate AND :endDate " +
                                    "GROUP BY ip HAVING count(id) > :threshold";

    String INSERT_DETECTED_IP_QUERY = "INSERT INTO detected_ips(ip, total, reason) VALUES (:ip, :total, :reason)";

    String REASON_MESSAGE = "%s send request %d times that exceeds max allowed limit %d";
}
