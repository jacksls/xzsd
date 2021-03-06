package com.neusoft.bookstore.cate.controller;

import com.neusoft.bookstore.cate.model.Cate;
import com.neusoft.bookstore.cate.service.CateService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author Liang
 * @date 2020/4/30 20:31
 */

@Api("cate")
@RequestMapping("cate")
@RestController
public class CateController {

    @Autowired
    private CateService cateService;

    /**
     * 新增分类
     * @param  cate 分类用户信息
     * @return
     */
    @ApiOperation(value ="新增分类",notes = "新增分类")
    @PostMapping("addCate")
    public ResponseVo addCate(@RequestBody Cate cate){

        ResponseVo responseVo = new ResponseVo();
        try {
            System.out.println(new Date() +"   调用新增分类接口！");
            responseVo = cateService.addCate(cate);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    /**
     * 分类树查询
     * @return
     */
    @ApiOperation(value ="分类树查询",notes = "分类树查询")
    @GetMapping("listCateTree")
    public ResponseVo listCateTree(){
        ResponseVo responseVo = new ResponseVo();
        try {
            System.out.println(new Date() +"   调用分类树查询接口！");
            responseVo = cateService.listCateTree();
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    /**
     * 分类详情查询
     * @return
     */
    @ApiOperation(value ="分类详情查询",notes = "分类详情查询")
    @GetMapping("findCateByCode")
    public ResponseVo findCateByCode(String cateCode){
        ResponseVo responseVo = new ResponseVo();
        try {
            System.out.println(new Date() +"   调用分类详情查询息接口！");
            responseVo = cateService.findCateByCode(cateCode);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }
    /**
     * 修改分类信息
     * @return
     */
    @ApiOperation(value ="修改分类信息",notes = "修改分类信息")
    @PostMapping ("updateCateByCode")
    public ResponseVo updateCateByCode(@RequestBody Cate cate){
        ResponseVo responseVo = new ResponseVo();
        try {
            System.out.println(new Date() +"   调用修改分类信息接口！");
            responseVo = cateService.updateCateByCode(cate);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    /**
     * 删除分类信息
     * @return
     */
    @ApiOperation(value =" 删除分类信息",notes = "删除分类信息")
    @GetMapping("deleteCateByCode")
    public ResponseVo deleteCateByCode(String cateCode){
        ResponseVo responseVo = new ResponseVo();
        try {
            System.out.println(new Date() +"   调用删除分类信息接口！");
            responseVo = cateService.deleteCateByCode(cateCode);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    /**
     * 级联查询
     */
    @ApiOperation(value ="级联查询",notes = "级联查询")
    @GetMapping("findCateByCateCode")
    public ResponseVo findCateByCateCode(String cateCode){

        ResponseVo responseVo = new ResponseVo();
        try {
            System.out.println(new Date() +"   调用级联查询接口！");
            responseVo = cateService.findCateByCateCode(cateCode);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

}