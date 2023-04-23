package com.uzero.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 套餐
 */
@Data
@ApiModel(description = "套餐")
public class Setmeal implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;


    //分类id
    @ApiModelProperty(value = "分类id")
    private Long categoryId;


    //套餐名称
    @ApiModelProperty(value = "套餐名称")
    private String name;


    //套餐价格
    @ApiModelProperty(value = "套餐价格")
    private BigDecimal price;


    //状态 0:停用 1:启用
    @ApiModelProperty(value = "状态")
    private Integer status;


    //编码
    @ApiModelProperty(value = "编码")
    private String code;


    //描述信息
    @ApiModelProperty(value = "描述信息")
    private String description;


    //图片
    @ApiModelProperty(value = "图片")
    private String image;


    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;


    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人")
    private Long createUser;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "修改人")
    private Long updateUser;


    //是否删除
    @ApiModelProperty(value = "是否删除")
    private Integer isDeleted;
}
