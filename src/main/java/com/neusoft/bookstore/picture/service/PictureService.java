package com.neusoft.bookstore.picture.service;

import com.neusoft.bookstore.picture.model.Picture;
import com.neusoft.bookstore.util.ResponseVo;

/**
 * @author Liang
 * @date 2020/5/11 9:26
 */
public interface PictureService {
    ResponseVo addPic(Picture picture);

    ResponseVo listPic(Picture picture);

    ResponseVo updatePic(Picture picture);
}
