package com.inn.cafe.rest;

import com.inn.cafe.rest.DashboardRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public interface DesfboardRestImpl implements DashboardRest{

    @Autowired
    DashboardService dashboardService;

    @Override
    public ResponseEntity<Map<String, Object>> getCount()  {
        return dashboardService.getCount();
    }
}