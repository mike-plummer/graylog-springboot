package com.objectpartners.plummer.stockmarket.graylog;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.graylog2.rest.models.dashboards.responses.DashboardList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
