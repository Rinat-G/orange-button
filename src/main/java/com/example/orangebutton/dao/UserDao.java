package com.example.orangebutton.dao;

import com.example.orangebutton.model.UserDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserDao {

    //language=PostgreSQL
    private static final String SELECT_USER_BY_EMAIL = "" +
            "SELECT *\n" +
            "FROM orange_button.ob_user\n" +
            "WHERE email = ?";

    //language=PostgreSQL
    private static final String INSERT_NEW_USER = "" +
            "INSERT INTO orange_button.ob_user (email, pin_hash)\n" +
            "VALUES (?, ?)\n" +
            "RETURNING id";

    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserDto getUserByEmail(String email) {
        var users = jdbcTemplate.query(
                SELECT_USER_BY_EMAIL,
                (rs, rowNum) -> new UserDto(
                        rs.getLong("id"),
                        rs.getString("email"),
                        rs.getString("pin_hash")
                ),
                email
        );

        return users.size() < 1 ? null : users.get(0);
    }

    public Long insertNewUser(UserDto user) {
        return jdbcTemplate.queryForObject(INSERT_NEW_USER, Long.class, user.getUserEmail(), user.getPinHash());
    }

}
