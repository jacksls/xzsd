package com.neusoft.bookstore.util;

import com.neusoft.bookstore.customer.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Liang
 * @date 2020/4/28 9:37
 */
@Component
public class MethodUtils {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    public String getCreatedBy(String loginAccount) {
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(loginAccount);
        if (customerByRedis != null) {
            // redis已经保存
            return customerByRedis.getUserAccount();
        } else {
            return "admin";
        }
    }

    public void setLoginInfo(String loginAccount, Object object) {
        redisTemplate.opsForValue().set(loginAccount, object);
    }

    public boolean deleteKey(String loginAccount) {
        return redisTemplate.delete(loginAccount);
    }


}
