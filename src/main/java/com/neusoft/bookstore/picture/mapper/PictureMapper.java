package com.neusoft.bookstore.picture.mapper;

import com.neusoft.bookstore.picture.model.Picture;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Liang
 * @date 2020/5/11 9:27
 */
@Mapper
public interface PictureMapper {

    int addPic(Picture picture);

    List<Picture> listPic(Picture picture);

    int updatePic(Picture picture);
}
