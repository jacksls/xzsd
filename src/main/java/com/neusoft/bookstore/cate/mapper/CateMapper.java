package com.neusoft.bookstore.cate.mapper;

import com.neusoft.bookstore.cate.model.Cate;
import com.neusoft.bookstore.menu.model.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Liang
 * @date 2020/4/30 20:32
 */
@Mapper
public interface CateMapper {


    Cate findCateByParentCateCodeAndName(Cate cate);

    int insertCate(Cate cate);

    List<Cate> listCates();

    Cate findCateByCateCode(String cateCode);

    int updateCateByCode(Cate cate);

    List<Menu> findChildCates(String cateCode);

    int deleteCateByCode(String cateCode);

    List<Cate> findCateByParentCode(String parentCateCode);
}
