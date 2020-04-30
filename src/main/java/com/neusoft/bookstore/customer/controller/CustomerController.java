package com.neusoft.bookstore.customer.controller;

import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.customer.service.CustomerService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Liang
 * @date 2020/4/23 10:54
 */
@Api("customer")
@RequestMapping("customer")
@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;


    @ApiOperation(value = "注册新增用户", notes = "app端和pc端新用户注册")
    @PostMapping("addCustomer")
    private ResponseVo addCustomer(Customer customer){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = customerService.addCustomer(customer);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "用户登录", notes = "app端和pc端用户登录")
    @PostMapping("login")
    private ResponseVo login(Customer customer){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = customerService.login(customer);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "PC端用户退出", notes = "PC端用户退出")
    @GetMapping("loginOut")
    private ResponseVo loginOut(String userAccount){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = customerService.loginOut(userAccount);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }


    @ApiOperation(value = "用户列表查询", notes = "用户列表查询")
    @PostMapping("listCustomers")
    private ResponseVo listCustomers(Customer customer){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = customerService.listCustomers(customer);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "根据用户id查询单个用户信息", notes = "根据用户id查询单个用户信息")
    @GetMapping("findCustomerById")
    private ResponseVo findCustomerById(Integer id){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = customerService.findCustomerById(id);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "根据用户id修改用户信息", notes = "根据用户id修改用户信息")
    @PostMapping("updateCustomerById")
    private ResponseVo updateCustomerById(Customer customer){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = customerService.updateCustomerById(customer);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "根据用户id删除用户信息", notes = "根据用户id删除用户信息")
    @GetMapping("deleteCustomerById")
    private ResponseVo deleteCustomerById(Integer id){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = customerService.deleteCustomerById(id);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "根据用户id修改用户密码", notes = "根据用户id修改用户密码")
    @GetMapping("updatePwdById")
    private ResponseVo updatePwdById(String originPwd, String newPwd, Integer userId, String userAccount){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = customerService.updatePwdById(originPwd, newPwd, userId, userAccount);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    @ApiOperation(value = "修改用户积分金额", notes = "修改用户积分金额")
    @GetMapping("updateScore")
    private ResponseVo updateScore(String frontScore, Integer userId, String userAccount){
        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = customerService.updateScore(frontScore, userId, userAccount);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

}
