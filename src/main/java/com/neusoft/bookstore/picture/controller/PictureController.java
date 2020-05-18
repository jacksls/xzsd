package com.neusoft.bookstore.picture.controller;

import com.neusoft.bookstore.picture.model.Picture;
import com.neusoft.bookstore.picture.service.PictureService;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Liang
 * @date 2020/5/11 9:27
 */
@Api("轮播图")
@RequestMapping("picture")
@RestController
public class PictureController {
    @Autowired
    private PictureService pictureService;

    /**
     * 轮播图新增
     */
    @ApiOperation(value ="轮播图新增",notes = "轮播图新增")
    @PostMapping("addPic")
    public ResponseVo addPic(@RequestBody Picture picture){

        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = pictureService.addPic(picture);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    /**
     * 轮播图列表查询（app+pc）
     */
    @ApiOperation(value ="轮播图列表查询",notes = "轮播图列表查询")
    @PostMapping("listPic")
    public ResponseVo listPic(@RequestBody Picture picture){

        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = pictureService.listPic(picture);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }

    /**
     * 轮播图启用、禁用、删除(pc)
     */
    @ApiOperation(value ="轮播图启用、禁用、删除",notes = "轮播图启用、禁用、删除")
    @PostMapping("updatePic")
    public ResponseVo updatePic(@RequestBody Picture picture){

        ResponseVo responseVo = new ResponseVo();
        try {
            responseVo = pictureService.updatePic(picture);
        } catch (Exception e) {
            responseVo.setCode(ErrorCode.SERVER_EXCEPTION_CODE);
            responseVo.setSuccess(false);
            responseVo.setMsg("服务器异常");
            e.printStackTrace();
        }
        return responseVo;
    }
}
