package com.mipt.sem2.service;

import com.mipt.sem2.model.Priority;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TaskStatisticsJdbcService {

    private final JdbcTemplate jdbcTemplate;

    public TaskStatisticsJdbcService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class PriorityCountRowMapper implements RowMapper<PriorityCount> {
        @Override
        public PriorityCount mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
            return new PriorityCount(
                    Priority.valueOf(rs.getString("priority")),
                    rs.getLong("count")
            );
        }
    }

    public Map<Priority, Long> getTasksCountByPriority() {
        String sql = "SELECT priority, COUNT(*) as count FROM tasks GROUP BY priority";
        return jdbcTemplate.query(sql, new PriorityCountRowMapper())
                .stream()
                .collect(HashMap::new, (m, pc) -> m.put(pc.priority, pc.count), HashMap::putAll);
    }

    private record PriorityCount(Priority priority, long count) {}
}
