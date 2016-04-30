package com.objectpartners.plummer.graylog.graylog;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.graylog2.rest.models.dashboards.responses.DashboardList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Graylog rest classes don't supply a DashboardList implementation that can be easily used with Jackson deserialization.
 * Create one here to make it easier to pull and deserialize JSON from Graylog rest services.
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ConcreteDashboardList extends DashboardList {
    private int total;
    private List<Map<String, Object>> dashboards;

    @Override
    public int total() {
        return total;
    }

    @Override
    public List<Map<String, Object>> dashboards() {
        if (dashboards == null) {
            dashboards = new ArrayList<>(0);
        }
        return dashboards;
    }
}
