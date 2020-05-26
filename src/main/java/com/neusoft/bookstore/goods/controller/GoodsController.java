package com.neusoft.bookstore.goods.controller;


import com.neusoft.bookstore.goods.model.Goods;
import com.neusoft.bookstore.goods.service.GoodsService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * @author Liang
 * @date 2020/5/6 20:31
 */

@Api("goods")
@RequestMapping("goods")
@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 查询全部商家
     */
    @ApiOperation(value ="查询全部商家",notes = "查询全部商家")
    @GetMapping("listBusiness")
    public ResponseVo listBusiness(){

        ResponseVo responseVo = new ResponseVo();
        try {
            System.out.println(new Date() +"   调用查询全部商家接口！");
            responseVo = goodsService.listBusiness();
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    /**
     * 图片上传
     */
    @ApiOperation(value ="图片上传",notes = "图片上传")
    @PostMapping("uploadImage")
    public ResponseVo uploadImage(@RequestBody MultipartFile file){

        ResponseVo responseVo = new ResponseVo();
        try {
            System.out.println(new Date() +"   调用图片上传接口！");
            responseVo = goodsService.uploadImage(file);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    /**
     * 商品新增
     */
    @ApiOperation(value ="商品新增",notes = "商品新增")
    @PostMapping("addGoods")
    public ResponseVo addGoods(@RequestBody Goods goods){

        ResponseVo responseVo = new ResponseVo();
        try {
            System.out.println(new Date() +"   调用商品新增接口！");
            responseVo = goodsService.addGoods(goods);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    /**
     * 商品列表查询
     */
    @ApiOperation(value ="商品列表查询",notes = "商品列表查询")
    @PostMapping("listGoods")
    public ResponseVo listGoods(@RequestBody Goods goods){

        ResponseVo responseVo = new ResponseVo();
        try {
            System.out.println(new Date() +"   调用商品列表查询接口！");
            responseVo = goodsService.listGoods(goods);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    /**
     * 商品详情查询
     */
    @ApiOperation(value ="商品详情查询",notes = "商品详情查询")
    @GetMapping("findGoodsBySkuCode")
    public ResponseVo findGoodsBySkuCode(String skuCode){

        ResponseVo responseVo = new ResponseVo();
        try {
            System.out.println(new Date() +"   调用商品详情查询接口！");
            responseVo = goodsService.findGoodsBySkuCode(skuCode);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    /**
     * 商家详情查询
     */
    @ApiOperation(value ="商家详情查询",notes = "商家详情查询")
    @GetMapping("findBusinessByCode")
    public ResponseVo findBusinessByCode(String businessCode){

        ResponseVo responseVo = new ResponseVo();
        try {
            System.out.println(new Date() +"   调用商家详情查询接口！");
            responseVo = goodsService.findBusinessByCode(businessCode);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    /**
     * 商品修改
     */
    @ApiOperation(value ="商品修改",notes = "商品修改")
    @PostMapping("updateGoodsInfo")
    public ResponseVo updateGoodsInfo(@RequestBody Goods goods){

        ResponseVo responseVo = new ResponseVo();
        try {
            System.out.println(new Date() +"   调用商品修改接口！");
            responseVo = goodsService.updateGoodsInfo(goods);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    /**
     * 删除商品信息
     */
    @ApiOperation(value ="删除商品信息",notes = "删除商品信息")
    @GetMapping("deleteGoods")
    public ResponseVo deleteGoods(String skuCode,String loginAccount){

        ResponseVo responseVo = new ResponseVo();
        try {
            System.out.println(new Date() +"   调用商品修改接口！");
            responseVo = goodsService.deleteGoods(skuCode,loginAccount);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }


    /**
     * 商品的上架和下架
     */
    @ApiOperation(value ="商品的上架和下架",notes = "商品的上架和下架")
    @GetMapping("updateGoodStatus")
    public ResponseVo updateGoodStatus(String skuCode, String loginAccount, String skuStatus){

        ResponseVo responseVo = new ResponseVo();
        try {
            System.out.println(new Date() +"   调用商品的上架和下架接口！");
            responseVo = goodsService.updateGoodStatus(skuCode,loginAccount,skuStatus);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }
}