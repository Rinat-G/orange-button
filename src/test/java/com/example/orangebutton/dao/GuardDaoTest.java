package com.example.orangebutton.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class GuardDaoTest {

    @Mock
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() {
        openMocks(this);
    }

    @Test
    void insertSqlGenTest() {
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> paramsCaptor = ArgumentCaptor.forClass(Object.class);

        new GuardDao(jdbcTemplate).insertGuardsForUser(123, List.of("", "2", "3", "IV"));

        verify(jdbcTemplate).update(stringCaptor.capture(), paramsCaptor.capture());

        var sql = stringCaptor.getValue();
        var params = paramsCaptor.getAllValues();
        assertThat(sql).contains("(?, ?), (?, ?), (?, ?), (?, ?)");
        assertThat(params)
                .hasSize(8)
                .isEqualTo(List.of("", 123L, "2", 123L, "3", 123L, "IV", 123L));
    }
}