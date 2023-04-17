package com.wms.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Select;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wms
 * @since 2023-03-08
 */
public interface UserService extends IService<User> {

    IPage pageC(IPage<User> page);

    //@Select("select * from user ${wrapper.customSqlSegment}")
    IPage pageCC(IPage<User> page, Wrapper wrapper);
}
