package com.neusoft.bookstore.picture.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.picture.mapper.PictureMapper;
import com.neusoft.bookstore.picture.model.Picture;
import com.neusoft.bookstore.picture.service.PictureService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Liang
 * @date 2020/5/11 9:25
 */
@Service
public class PictureServiceImpl implements PictureService {
    @Autowired
    private PictureMapper pictureMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public ResponseVo addPic(Picture picture) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "新增成功！");
        // 获取登陆人
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(picture.getLoginAccount());
        if (customerByRedis != null) {
            // redis已经保存
            picture.setCreatedBy(customerByRedis.getUserAccount());
        } else {
            //提示需要登录
            responseVo.setMsg("请登录后重试! ");
            responseVo.setSuccess(false);
            responseVo.setCode(ErrorCode.FAIL);
            return responseVo;
        }
        //新增
        int result = pictureMapper.addPic(picture);
        if (result != 1) {
            responseVo.setMsg("新增失败!");
            responseVo.setSuccess(false);
            responseVo.setCode(ErrorCode.FAIL);
        }
        return responseVo;
    }

    @Override
    public ResponseVo listPic(Picture picture) {
        /*
        1:pc轮播图列表查询带分页 模糊查询的条件:图片状态
        2:app轮播图列表查询不带分页   图片状态:启用
        */

        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功！");
        //判断分页
        if (picture.getPageNum() != null && picture.getPageSize() != null) {
            //pc端列表查询必需带分页，封装分页信息
            PageHelper.startPage(picture.getPageNum(), picture.getPageSize());
        }
        //直接查询
        List<Picture> pictureList = pictureMapper.listPic(picture);
        if (picture.getPageNum() != null && picture.getPageSize() != null) {
            // pc端列表查询返回分页信息
            PageInfo<Picture> pageInfo = new PageInfo<>(pictureList);
            responseVo.setData(pageInfo);
        } else {
            //返回给app 不需要带分页
            responseVo.setData(pictureList);
        }
        return responseVo;
    }

    @Override
    public ResponseVo updatePic(Picture picture) {
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "操作失败！");
        // 获取登陆人
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(picture.getLoginAccount());
        if (customerByRedis != null) {
            // redis已经保存
            picture.setUpdatedBy(customerByRedis.getUserAccount());
        } else {
            //提示需要登录
            responseVo.setMsg("请登录后重试! ");
            return responseVo;
        }

        //校验
        Integer status = picture.getPicStatus();
        if (status!=null && status!=1 && status!=2){
            responseVo.setMsg("轮播图状态不正确! ");
            return responseVo;
        }
        int result = pictureMapper.updatePic(picture);
        if(result==1){
            responseVo.setMsg("操作成功！");
            responseVo.setSuccess(true);
            responseVo.setCode(ErrorCode.SUCCESS);

        }
        return responseVo;
    }
}
