package com.neusoft.bookstore.menu.service.impl;

import com.neusoft.bookstore.customer.model.Customer;
import com.neusoft.bookstore.util.BaseTree;
import com.neusoft.bookstore.util.ErrorCode;
import com.neusoft.bookstore.util.ResponseVo;
import com.neusoft.bookstore.menu.mapper.MenuMapper;
import com.neusoft.bookstore.menu.model.Menu;
import com.neusoft.bookstore.menu.service.MenuService;
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
 * @date 2020/4/27 11:00
 */
@Service
public class MenuServiceImpl implements MenuService {
    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseVo addMenu(Menu menu) {
        /*
        1:规定了一级菜单的父级菜单编码是。，子级的父级菜单编码是父级菜单的菜单编码
        2:保证同一层的菜单名称不能重复
        3:当某一级有子级菜单的时候， 需要变动当前级别类型为目录
        4:获取登陆人
        5:新增
        */

        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "新增失败");
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(menu.getLoginAccount());
        if (customerByRedis != null) {
            // redis 已经保存
            menu.setCreatedBy(customerByRedis.getUserAccount());
        } else {
            responseVo.setMsg("请登录后重试！");
            return responseVo;
        }
        //1: 规定了一级菜单的父级菜单编码是 0，子级的父级菜单编码是父级菜单的菜单编码
        String frontMenuCode = menu.getFrontMenuCode();
        if (StringUtils.isEmpty(frontMenuCode)) {
            //没有点击创建的一级菜单.
            menu.setParentMenuCode("0");
        } else {
            //非一级菜单
            menu.setParentMenuCode(frontMenuCode);
        }
        //2:保证同一层的菜单名称不能重复
        Menu menuByDb = menuMapper.findMenuByParentMenuCodeAndName(menu);
        if (menuByDb != null){
            //有重复
            responseVo.setMsg("当前级别的菜单下名称重复! ");
            return responseVo;
        }

        /*当某一级有子级菜单的时候， 需要变动当前級别类型为目录
        如果是一级菜单frontMenuCode 不需要做任何操作，当parentMenuCode !=0的时候*/

        if (!"0".equals(menu.getParentMenuCode())){
            //更新操作
            //查询父級菜单类型
            Menu menuByMenuCode = menuMapper.findMenuByMenuCode(menu.getParentMenuCode());
            if (menuByMenuCode != null) {
                if (menuByMenuCode.getType() == 2) {
                    //更新修改为目录，清空menu_url
                    menuMapper.updateTypeAndUrlByCode(menu.getParentMenuCode());
                }
            }
        }
        //新增
        int result = menuMapper.insertMenu(menu);
        if (result == 1) {
            responseVo.setMsg("新增成功! ");
            responseVo.setSuccess(true);
            responseVo.setCode(ErrorCode.SUCCESS);
            return responseVo;
        }
        return responseVo;
    }

    @Override
    public ResponseVo listMenuTree() {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功");
        //找出所有的菜单(type为 菜单和目录)
        List<Menu> menuList = menuMapper.listMenus();
        //要将菜单信息封装成有层级关系的树信息
        if(menuList==null||menuList.size()==0){
            responseVo.setMsg("未查询到任何菜单信息! ");
            return responseVo;
        }
        //先来创建一颗根数
        BaseTree rootTree = new BaseTree();
        //规定根节点树的id为0
        String rootNodeId="0";
        initTree(rootTree,menuList,rootNodeId);
        //3 :树返回
        responseVo.setData(rootTree.getChildNodes());
        return responseVo;
    }

    @Override
    public ResponseVo findMenuByCode(String menuCode) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "查询成功");
        //数据校验
        if (StringUtils.isEmpty(menuCode)) {
            responseVo.setMsg("菜单编码不能为空");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }
        //根据菜单编码查询菜单详情
        Menu menu = menuMapper.findMenuByMenuCode(menuCode);
        if (menu == null) {
            responseVo.setMsg("未查询到指定菜单信息");
            return responseVo;
        }
        responseVo.setData(menu);
        return responseVo;
    }

    @Override
    public ResponseVo updateMenuByCode(Menu menu) {
        ResponseVo responseVo = new ResponseVo(false, ErrorCode.FAIL, "修改失败");
        Customer customerByRedis = (Customer) redisTemplate.opsForValue().get(menu.getLoginAccount());
        if (customerByRedis != null) {
            // redis 已经保存
            menu.setUpdatedBy(customerByRedis.getUserAccount());
        } else {
            responseVo.setMsg("请登录后重试！");
            return responseVo;
        }
        //校验同一层级菜单名称不重复
        Menu menuByDb = menuMapper.findMenuByParentMenuCodeAndName(menu);
        if(menuByDb!=null){
            //证明当前层级有重复的菜单名称
            responseVo.setMsg("当前级别的菜单下名称重复! ");
            return responseVo;
        }
        //更新操作
        int result = menuMapper.updateMenuByCode(menu);
        if(result==1){
            responseVo.setMsg("修改成功! ");
            responseVo.setSuccess(true);
            responseVo.setCode(ErrorCode.SUCCESS);
            return responseVo;
        }
        return responseVo;
    }

    @Override
    public ResponseVo deleteMenuByCode(String menuCode) {
        ResponseVo responseVo = new ResponseVo(true, ErrorCode.SUCCESS, "删除成功");
        //数据校验
        if (StringUtils.isEmpty(menuCode)) {
            responseVo.setMsg("菜单编码不能为空");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }
        //需要判断该节点下是否有子节点

        List<Menu> menuLists = menuMapper.findChildMenus(menuCode);
        if (menuLists!=null&& menuLists.size()>0){
            //判断是否有子集
            responseVo.setMsg("当前菜单下有子级，无法删除!");
            responseVo.setCode(ErrorCode.FAIL);
            responseVo.setSuccess(false);
            return responseVo;
        }
        //删除
        int result = menuMapper.deleteMenuByCode(menuCode);
        if(result!=1){
            responseVo.setMsg("删除失败! ");
            responseVo.setSuccess(false);
            responseVo.setCode(ErrorCode.FAIL);
            return responseVo;
        }
        return responseVo;
    }


    private void initTree(BaseTree rootTree, List<Menu> menuList, String rootNodeId) {
        //需要遍历menulist 给里面每一个菜单，找位置
        Iterator<Menu> iterator = menuList.iterator();
        while(iterator.hasNext()){
            /*
            找位置:
            需要判断menu的menuCode和rootNodeId :
            1 :层级相同
            2:是一颗新树的开始  menu的parentMenuCode和rootNodeId的关系
            */

            Menu menu = iterator.next();
            if (menu.getMenuCode().equals(rootNodeId)) {
                // 创建根树
                menuToTree(rootTree,menu);
            }else if (menu.getParentMenuCode().equals(rootNodeId)) {
                //子节点
                // 创建子树
                BaseTree childTree = new BaseTree();
                menuToTree(childTree,menu);
                //需要将子树加入根数
                //许需要判断根节点的子节点是否已经创建
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
                initTree(childTree,menuList,menu.getMenuCode());
            }
        }
    }

    private void menuToTree(BaseTree rootTree, Menu menu) {
        //节点的id存菜单编码
        rootTree.setNodeId(menu.getMenuCode());
        rootTree.setNodeName(menu.getMenuName());
        rootTree.setNodeUrl(menu.getMenuUrl());
        rootTree.setAttribute(menu);

    }
}
