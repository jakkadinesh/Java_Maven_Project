package com.taskmanager.api.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Custom health indicator that verifies database connectivity
 * and reports application-level health details.
 */
@Component("taskManagerHealth")
public class HealthCheckConfig implements HealthIndicator {

    private final DataSource dataSource;

    public HealthCheckConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(3)) {
                return Health.up()
                        .withDetail("service", "Task Manager API")
                        .withDetail("database", "MySQL — reachable")
                        .withDetail("database_product", connection.getMetaData().getDatabaseProductName())
                        .withDetail("database_version", connection.getMetaData().getDatabaseProductVersion())
                        .build();
            }
            return Health.down()
                    .withDetail("service", "Task Manager API")
                    .withDetail("database", "MySQL — connection invalid")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("service", "Task Manager API")
                    .withDetail("database", "MySQL — unreachable")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
