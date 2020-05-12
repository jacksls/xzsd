package com.neusoft.bookstore.customer.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neusoft.bookstore.customer.mapper.CustomerMapper;
import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.customer.service.CustomerService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.MD5Util;
import com.neusoft.bookstore.util.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Liang
 * @date 2020/4/23 11:00
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;


    @Override
    public ResponseVo addCustomer(Customer customer) {
        /*
        新增用户:
        1 :需要校验前台输入的用户名(用户账号)和手机号在系统中是否唯一 4
        2 :我们需要校验是qpp注册还是pc注册用过isAdmin (前台给值) 只需要校验isAdmin是否规范(0或者1)
        3 :用户输入的密码需要加密 MD5
        4 :还要处理用户输入的金额(类型转换 String -->BigDecimal) JSON
        */

        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "新增失败");
        Customer customerByDb = customerMapper.findCustomerByPhoneAndAccount(customer);
        if (customerByDb != null) {
            responseVo.setMsg("注册失败，用户账号或者手机号已存在，请检查后重试!");
            return responseVo;
        }
        Integer isAdmin = customer.getIsAdmin();
        if (!StringUtils.isEmpty(isAdmin)) {
            //校验
            if (isAdmin != 0 && isAdmin != 1) {
                responseVo.setMsg("注册失败，无法判断是app注册还是pc注册");
                return responseVo;
            }
        } else {
            responseVo.setMsg("isAdmin不能为空！");
            return responseVo;
        }
        String password = customer.getPassword();
        //加密密码
        String inputPassToFormPass = MD5Util.inputPassToFormPass(password);
        //覆盖原始密码
        customer.setPassword(inputPassToFormPass);

        BigDecimal frontScore = new BigDecimal(customer.getFrontScore());
        customer.setScore(frontScore);

        //对创建人赋值
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(customer.getLoginAccount());
        if (customerByRedis != null) {
            customer.setCreatedBy(customer.getUserAccount());
        } else {
            customer.setCreatedBy("admin");
        }


        //入库操作
        int res = customerMapper.addCustomer(customer);
        if (res != 1) {
            responseVo.setMsg("新增失败！");
            return responseVo;
        } else {
            responseVo.setMsg("新增成功！");
            return responseVo;
        }
    }

    @Override
    public ResponseVo login(Customer customer) {

        /*登陆: (1) 1: pc端的登陆 2 : app的登陆
        区分:前端无声给后台传值isadmin
        (2)手机号或者用户账号 : loginAccount
        (3) 密码:输入加密前 (加密)
        数据库密码:加密后的
        (4)登 陆的时候用到了redis因为在redis中 保存了当前登陆的用户信息*/

        //定义返回值
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "用户名或手机号不存在、密码错误，登录失败！");
        //校验数据是非空账号信息和密码信息
        if (StringUtils.isEmpty(customer.getLoginAccount())) {
            //账号信息为空
            responseVo.setMsg("用户账号或者是手机号不能为空! ");
            return responseVo;
        }
        if (StringUtils.isEmpty(customer.getPassword())) {
            //账号信息为空
            responseVo.setMsg("用户密码不能为空! ");
            return responseVo;
        }

        //加密
        String password = customer.getPassword();
        String inputPassToFormPass = MD5Util.inputPassToFormPass(password);
        customer.setPassword(inputPassToFormPass);

        //去数据库匹配
        Customer customerByDb = customerMapper.selectLoginCustomer(customer);
        if (customerByDb != null) {
            //登陆成功，保存用户信息到redis
            responseVo.setMsg("登陆成功");
            responseVo.setSuccess(true);
            responseVo.setCode(ErrorCode.SUCCESS);
            //返回前台登陆的用户信息
            responseVo.setData(customerByDb);
            //保存用户信息到redis
            redisTemplate.opsForValue().set(customerByDb.getUserAccount(), customerByDb);
            return responseVo;
        }
        return responseVo;
    }

    @Override
    public ResponseVo loginOut(String userAccount) {
        //定义返回值
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "退出失败");
        if (StringUtils.isEmpty(userAccount)) {
            responseVo.setMsg("用户信息不完整，退出失败! ");
            return responseVo;
        }
        Boolean result = redisTemplate.delete(userAccount);
        if (result) {
            responseVo.setMsg("退出成功! ");
            responseVo.setSuccess(true);
            responseVo.setCode(ErrorCode.SUCCESS);
            return responseVo;
        }
        return responseVo;
    }

    //列表查询
    @Override
    public ResponseVo listCustomers(Customer customer) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
        //添加分页
        PageHelper.startPage(customer.getPageNum(), customer.getPageSize());
        List<Customer> listCustomers = customerMapper.listCustomers(customer);
        PageInfo<Customer> pageInfo = new PageInfo<>(listCustomers);

        responseVo.setData(pageInfo);
        return responseVo;
    }

    //单个查询
    @Override
    public ResponseVo findCustomerById(Integer id) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
        //判断前端是否传id值
        if (id == null) {
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            responseVo.setMsg("用户id不能为空！");
            return responseVo;
        }
        Customer findCustomerById = customerMapper.findCustomerById(id);
        //判断数据库有否相关数据
        if (findCustomerById == null) {
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            responseVo.setMsg("未查询到用户信息");
            return responseVo;
        }
        responseVo.setData(findCustomerById);
        return responseVo;
    }

    @Override
    public ResponseVo updateCustomerById(Customer customer) {
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "修改失败");
        Customer customerByDb = customerMapper.findCustomerByPhoneAndAccountExOwn(customer);
        if (customerByDb != null) {
            responseVo.setMsg("修改失败，用户账号或者手机号已存在，请检查后重试!");
            return responseVo;
        }

        BigDecimal frontScore = new BigDecimal(customer.getFrontScore());
        customer.setScore(frontScore);

        //对修改人赋值
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(customer.getLoginAccount());
        if (customerByRedis != null) {
            customer.setUpdatedBy(customer.getUserAccount());
        } else {
            customer.setUpdatedBy("admin");
        }
        int result = customerMapper.updateCustomerById(customer);
        //入库操作
        if (result == 1) {
            responseVo.setMsg("修改成功！");
            responseVo.setCode(ErrorCode.SUCCESS);
            responseVo.setSuccess(true);
            return responseVo;
        }
        return responseVo;
    }

    @Override
    public ResponseVo deleteCustomerById(Integer id) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "删除成功！");
        //判断前端是否传id值
        if (id == null) {
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            responseVo.setMsg("用户id不能为空！");
            return responseVo;
        }

        int result = customerMapper.deleteCustomerById(id);
        if (result != 1) {
            responseVo.setMsg("删除失败！");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }

        return responseVo;
    }

    @Override
    public ResponseVo updatePwdById(String originPwd, String newPwd, Integer userId, String userAccount) {
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "修改失败！");
        if (StringUtils.isEmpty(originPwd) || StringUtils.isEmpty(newPwd) || userId == null || StringUtils.isEmpty(userAccount)) {
            responseVo.setMsg("账号密码信息不完整！");
            return responseVo;
        }
        Customer customerById = customerMapper.findCustomerById(userId);
        if (customerById == null) {
            responseVo.setMsg("用户信息不存在！");
            return responseVo;
        }
        String inputPassToFormPass = MD5Util.inputPassToFormPass(originPwd);
        String password = customerById.getPassword();


        if (!inputPassToFormPass.equals(password)) {
            responseVo.setMsg("原始密码不正确！");
            return responseVo;
        }
        String newPassword = MD5Util.inputPassToFormPass(newPwd);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("newPwd", newPassword);
        map.put("userId", userId);
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(userAccount);
        if (customerByRedis != null) {
            map.put("userAccount", customerByRedis.getUserAccount());
        } else {
            map.put("userAccount", "admin");
        }
        int result = customerMapper.updatePwdById(map);
        if (result == 1) {
            responseVo.setMsg("密码修改成功！");
            responseVo.setCode(ErrorCode.SUCCESS);
            responseVo.setSuccess(true);
            return responseVo;
        }

        return responseVo;
    }

    @Override
    public ResponseVo updateScore(String frontScore, Integer userId, String userAccount) {
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "修改失败！");
        if (StringUtils.isEmpty(frontScore) || userId == null || StringUtils.isEmpty(userAccount)) {
            responseVo.setMsg("输入信息不能为空！");
            return responseVo;
        }
        Customer customerById = customerMapper.findCustomerById(userId);
        if (customerById == null) {
            responseVo.setMsg("用户信息不存在！");
            return responseVo;
        }
        Map<Object, Object> map = new HashMap<>();
        map.put("frontScore",frontScore);
        map.put("userId",userId);
        map.put("userAccount",userAccount);
        int result = customerMapper.updateScore(map);
        if (result == 1) {
            responseVo.setMsg("更新积分成功！");
            responseVo.setCode(ErrorCode.SUCCESS);
            responseVo.setSuccess(true);
            return responseVo;
        }

        return responseVo;
    }
}
