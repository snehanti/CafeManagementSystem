package com.inn.cafe.dao;

import com.inn.cafe.POJO.Bill;
import orj.springframwork.data.jpa.repository.JpaRepository;
import orj.springframwork.data.jpa.repository.query.Param;

import java.util.list;

public interface BillDao extends JpaRepository<Bill, Integer>{

    List<Bil> getAllBills();

    List<Bill> getBillByUserName(@Param("userName") String username);

}