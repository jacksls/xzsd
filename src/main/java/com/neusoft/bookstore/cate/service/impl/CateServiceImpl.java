package com.neusoft.bookstore.cate.service.impl;

import com.neusoft.bookstore.cate.mapper.CateMapper;
import com.neusoft.bookstore.cate.model.Cate;
import com.neusoft.bookstore.cate.service.CateService;
import com.neusoft.bookstore.customer.model.Customer;

import com.neusoft.bookstore.util.BaseTree;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Liang
 * @date 2020/4/30 20:33
 */
@Service
public class CateServiceImpl implements CateService {

    @Autowired
    private CateMapper cateMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseVo addCate(Cate cate) {
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "新增失败");
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(cate.getLoginAccount());
        if (customerByRedis != null) {
            // redis 已经保存
            cate.setCreatedBy(customerByRedis.getUserAccount());
        } else {
            cate.setCreatedBy("admin");
//            responseVo.setMsg("请登录后重试！");
//            return responseVo;
        }
        //1: 规定了一级分类的父级分类编码是 0，子级的父级分类编码是父级分类的分类编码
        String frontCateCode = cate.getFrontCateCode();
        if (StringUtils.isEmpty(frontCateCode)) {
            //没有点击创建的一级分类
            cate.setParentCateCode("0");
        } else {
            //非一级分类
            cate.setParentCateCode(frontCateCode);
        }
        //2:保证同一层的分类名称不能重复
        Cate cateByDb = cateMapper.findCateByParentCateCodeAndName(cate);
        if (cateByDb != null) {
            //有重复
            responseVo.setMsg("当前级别的分类下名称重复! ");
            return responseVo;
        }

        //新增
        int result = cateMapper.insertCate(cate);
        if (result == 1) {
            responseVo.setMsg("新增成功! ");
            responseVo.setSuccess(true);
            responseVo.setCode(ErrorCode.SUCCESS);
            return responseVo;
        }
        return responseVo;
    }

    @Override
    public ResponseVo findCateByCode(String cateCode) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功");
        //数据校验
        if (StringUtils.isEmpty(cateCode)) {
            responseVo.setMsg("分类编码不能为空");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }
        //根据分类编码查询分类详情
        Cate cate = cateMapper.findCateByCateCode(cateCode);
        if (cate == null) {
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            responseVo.setMsg("未查询到指定分类信息");
            return responseVo;
        }
        responseVo.setData(cate);
        return responseVo;
    }

    @Override
    public ResponseVo updateCateByCode(Cate cate) {
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "修改失败");
        //校验同一层级分类名称不重复
        Cate cateByDb = cateMapper.findCateByParentCateCodeAndName(cate);
        if (cateByDb != null) {
            //有重复
            responseVo.setMsg("当前级别的分类下名称重复! ");
            return responseVo;
        }
        //更新操作
        int result = cateMapper.updateCateByCode(cate);
        if(result==1){
            responseVo.setMsg("修改成功! ");
            responseVo.setSuccess(true);
            responseVo.setCode(ErrorCode.SUCCESS);
            return responseVo;
        }
        return responseVo;
    }

    @Override
    public ResponseVo deleteCateByCode(String cateCode) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "删除成功");
        //数据校验
        if (StringUtils.isEmpty(cateCode)) {
            responseVo.setMsg("分类编码不能为空");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }
        //需要判断该节点下是否有子节点
        List<Cate> menuLists = cateMapper.findChildCates(cateCode);
        if (menuLists!=null&& menuLists.size()>0){
            //判断是否有子集
            responseVo.setMsg("当前分类下有子级，无法删除!");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }
        //删除
        int result = cateMapper.deleteCateByCode(cateCode);
        if(result!=1){
            responseVo.setMsg("删除失败! ");
            responseVo.setSuccess(false);
            responseVo.setCode(ErrorCode.FAIL);
            return responseVo;
        }
        return responseVo;
    }

    @Override
    public ResponseVo listCateTree() {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功");
        //找出所有的分类
        List<Cate> cateList = cateMapper.listCates();
        //要将分类信息封装成有层级关系的树信息
        if(cateList==null||cateList.size()==0){
            responseVo.setMsg("未查询到任何菜单信息! ");
            return responseVo;
        }
        //先来创建一颗根数
        BaseTree rootTree = new BaseTree();
        //规定根节点树的id为0
        String rootNodeId="0";
        initTree(rootTree,cateList,rootNodeId);
        //3 :树返回
        responseVo.setData(rootTree.getChildNodes());
        return responseVo;
    }

    @Override
    public ResponseVo findCateByCateCode(String cateCode) {
        /*
        级联既要获取一级分类 又要根据一级加载对应二级分类
        加载一级分类: cateCode=null或者=''加载一级分类
        加载二级分类：cateCode=!null或者!''加载二(子集)级分类
        */
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功");
        String parentCateCode=null;
        if (StringUtils.isEmpty(cateCode)) {
            //一级商品分类
            parentCateCode = "0";
        } else {
            //加载 二级分类
            parentCateCode = cateCode;
        }
        //查询
        List<Cate> cateList = cateMapper.findCateByParentCode(parentCateCode);
        //todo 要对应加载分类下的商品
        responseVo.setData(cateList);
        return responseVo;


    }

    private void initTree(BaseTree rootTree, List<Cate> cateList, String rootNodeId) {
        //需要遍历catelist 给里面每一个分类，找位置
        Iterator<Cate> iterator = cateList.iterator();
        while(iterator.hasNext()){
            /*
            找位置:
            需要判断cate的cateCode和rootNodeId :
            1 :层级相同
            2:是一颗新树的开始cate的parentCateCode和rootNodeId的关系
            */

            Cate cate = iterator.next();
            if (cate.getCateCode().equals(rootNodeId)) {
                // 创建根树
                cateToTree(rootTree,cate);
            }else if (cate.getParentCateCode().equals(rootNodeId)) {
                //子节点
                // 创建子树
                BaseTree childTree = new BaseTree();
                cateToTree(childTree,cate);
                //需要将子树加入根数
                //需要判断根节点的子节点是否已经创建
                if(childTree.getNodeId()!=null){
                    //需要将子树加入根数
                    if(rootTree.getChildNodes()==null){
                        //初始化根数的子节点
                        ArrayList<BaseTree> list = new ArrayList<>();
                        rootTree.setChildNodes(list);
                    }
                    //最后往根数中加入子级
                    rootTree.getChildNodes().add(childTree);
                }
                //递归处理
                initTree(childTree,cateList,cate.getCateCode());
            }
        }
    }

    private void cateToTree(BaseTree rootTree, Cate cate) {
        //节点的id存分类编码
        rootTree.setNodeId(cate.getCateCode());
        rootTree.setNodeName(cate.getCateName());
        rootTree.setAttribute(cate);
    }
}
