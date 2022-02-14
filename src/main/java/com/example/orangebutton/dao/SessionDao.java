package com.example.orangebutton.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SessionDao {

    //language=PostgreSQL
    private static final String SELECT_CURRENT_SESSION_BY_USER_EMAIL = "" +
            "SELECT obs.id\n" +
            "FROM orange_button.ob_session as obs\n" +
            "         INNER JOIN orange_button.ob_user ou on ou.id = obs.user_id\n" +
            "WHERE obs.status = 'open'\n" +
            "  and email = ?";

    //language=PostgreSQL
    private static final String SELECT_CURRENT_SESSION_BY_USER_ID = "" +
            "SELECT obs.id\n" +
            "FROM orange_button.ob_session as obs\n" +
            "WHERE obs.status = 'open'\n" +
            "  and user_id = ?";

    //language=PostgreSQL
    private static final String INSERT_NEW_SESSION = "" +
            "INSERT INTO orange_button.ob_session\n" +
            "VALUES (?, ?, ?)";

    private final JdbcTemplate jdbcTemplate;

    public SessionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getSessionByUserEmail(String email) {
        var sessions = jdbcTemplate.query(
                SELECT_CURRENT_SESSION_BY_USER_EMAIL,
                (rs, rowNum) -> rs.getString("id"),
                email
        );

        return sessions.size() < 1 ? null : sessions.get(0);

    }

    public String getSessionByUserId(long userId) {
        var sessions = jdbcTemplate.query(
                SELECT_CURRENT_SESSION_BY_USER_ID,
                (rs, rowNum) -> rs.getString("id"),
                userId
        );

        return sessions.size() < 1 ? null : sessions.get(0);

    }

    public void createNewSession(UUID sessionId, long userId, String status) {
        jdbcTemplate.update(INSERT_NEW_SESSION, sessionId, userId, status);
    }
}
