package com.neusoft.bookstore.customer.mapper;

import com.neusoft.bookstore.customer.model.Customer;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Liang
 * @date 2020/4/23 11:01
 */
@Mapper
public interface CustomerMapper {


    Customer findCustomerByPhoneAndAccount(Customer customer);

    int addCustomer(Customer customer);


    Customer selectLoginCustomer(Customer customer);

    List<Customer> listCustomers(Customer customer);

    Customer findCustomerById(Integer id);

    Customer findCustomerByPhoneAndAccountExOwn(Customer customer);

    int updateCustomerById(Customer customer);

    int deleteCustomerById(Integer id);

    int updatePwdById(HashMap<Object, Object> map);

    int updateScore(Map<Object, Object> map);
}
