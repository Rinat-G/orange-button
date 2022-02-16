package com.example.orangebutton.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Component
public class GuardDao {

    //language=PostgreSQL
    private static final String INSERT_GUARDS = "" +
            "INSERT INTO orange_button.ob_guard (email, user_id)\n" +
            "VALUES ";

    private static final String VALUES_STATEMENT_TEMPLATE = "(?, ?)";

    private final JdbcTemplate jdbcTemplate;

    public GuardDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertGuardsForUser(long userId, List<String> guards) {
        var params = new ArrayList<>(guards.size());
        guards.forEach(s -> {
            params.add(s);
            params.add(userId);
        });
        var sqlSuffix = guards.stream().map((s) -> VALUES_STATEMENT_TEMPLATE).collect(joining(", "));
        jdbcTemplate.update(INSERT_GUARDS + sqlSuffix, params.toArray());
    }
}
