package com.neusoft.bookstore.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Liang
 * @date 2020/4/29 9:05
 */
@Data
public class BaseTree {
    @ApiModelProperty("树节点名称")
    private String nodeName;

    @ApiModelProperty("节点id:唯一标识树的节点信息")
    private String nodeId;

    @ApiModelProperty("节点:路轻(针对菜单)")
    private String nodeUrl;

    @ApiModelProperty("该节点所有的属性信息")
    private Object attribute;

    @ApiModelProperty("该节点下所有的子节点信息")
    private List<BaseTree> childNodes;


}
