package com.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Menu;
import com.wms.entity.User;
import com.wms.service.MenuService;
import com.wms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wms
 * @since 2023-03-08
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;

    @GetMapping("/List")
    public List<User>list(){
        return userService.list();
    }
    @GetMapping("/findByNo")
    public Result findByNo(@RequestParam String no){
       List list = userService.lambdaQuery().eq(User::getNo,no).list();
       return list.size()>0?Result.suc(list):Result.fail();
    }

    //新增
    @PostMapping("/save")
    public Result save(@RequestBody User user){
        return userService.save(user)?Result.suc():Result.fail();
    }
    //更新
    @PostMapping("/update")
    public Result update(@RequestBody User user){
        return userService.updateById(user)?Result.suc():Result.fail();
    }
    //删除
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return  userService.removeById(id)?Result.suc():Result.fail();
    }
    //登录
    @PostMapping("/login")
    public Result login(@RequestBody User user){
        List list = userService.lambdaQuery()
                .eq(User::getNo,user.getNo())
                .eq(User::getPassword,user.getPassword()).list();

        if(list.size()>0){
            User user1 = (User)list.get(0);
            List menuList = menuService.lambdaQuery().like(Menu::getMenuright,user1.getRoleId()).list();
            HashMap res = new HashMap();
            res.put("user",user1);
            res.put("menu",menuList);
            return Result.suc(res);
        }
        return  Result.fail();
        //return list.size()>0?Result.suc(list.get(0)):Result.fail();
    }
    //修改
    @PostMapping("/mod")
    public boolean mod(@RequestBody User user){
        return userService.updateById(user);
    }
    //新增或修改
    @PostMapping("/saveOrMod")
    public boolean saveOrMod(@RequestBody User user){
        return userService.saveOrUpdate(user);
    }
    //删除
    @GetMapping("/delete")
    public boolean delete(Integer id){
        return userService.removeById(id);
    }
    //查询（模糊、匹配）
    @PostMapping("/ListP")
    public Result listP(@RequestBody User user){
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper();
        //lambdaQueryWrapper.eq(User::getName,user.getName());
        if(StringUtils.isNotBlank(user.getName())){
            lambdaQueryWrapper.like(User::getName,user.getName());
        }

        return Result.suc(userService.list(lambdaQueryWrapper));
    }

    @PostMapping("/listPage")
    //public List<User> ListPage(@RequestBody HashMap map){
      public List<User> ListPage(@RequestBody QueryPageParam query){
        //System.out.println(query);

        //System.out.println("size==="+ query.getPageSize());
        //System.out.println("num==="+ query.getPageSize());

        HashMap param =query.getParam();
        String name = (String) param.get("name");
        System.out.println("name==="+ param.get("name"));
       // System.out.println("no==="+ param.get("no"));

        Page<User> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(User::getName,name);

        IPage result = userService.page(page,lambdaQueryWrapper);

        System.out.println("total=="+result.getTotal());

        return result.getRecords();
    }


   /* @PostMapping("/listPageC")
    //public List<User> ListPage(@RequestBody HashMap map){
    public List<User> ListPageC(@RequestBody QueryPageParam query){
        HashMap param =query.getParam();
        String name = (String) param.get("name");
        System.out.println("name==="+ param.get("name"));
        // System.out.println("no==="+ param.get("no"));

        Page<User> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());


        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.like(User::getName,name);

        IPage result = userService.pageCC(page,lambdaQueryWrapper);

        System.out.println("total=="+result.getTotal());

        return result.getRecords();
    }*/


    /*@PostMapping("/listPageCC")
    //public List<User> ListPage(@RequestBody HashMap map){
    public List<User> ListPageCC(@RequestBody QueryPageParam query){
        HashMap param =query.getParam();
        String name = (String) param.get("name");
        System.out.println("name==="+ param.get("name"));
        // System.out.println("no==="+ param.get("no"));

        Page<User> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(User::getName,name);

        IPage result = userService.pageCC(page,lambdaQueryWrapper);

        System.out.println("total=="+result.getTotal());

        return result.getRecords();
    }*/

    @PostMapping("/listPageCC1")
    //public List<User> ListPage(@RequestBody HashMap map){
    public Result ListPageCC1(@RequestBody QueryPageParam query ){
        HashMap param =query.getParam();
        String name = (String) param.get("name");
        String sex=(String)param.get("sex");

        String roleId = (String) param.get("roleId");
        //System.out.println("name==="+ param.get("name"));
        // System.out.println("no==="+ param.get("no"));

        Page<User> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
        if(StringUtils.isNotBlank(name) && !"null".equals(name)) {
            lambdaQueryWrapper.like(User::getName, name);
        }

        if(StringUtils.isNotBlank(sex)){
            lambdaQueryWrapper.eq(User::getSex,sex);
        }

        if(StringUtils.isNotBlank(roleId)){
            lambdaQueryWrapper.eq(User::getRoleId,roleId);
        }

        IPage result = userService.pageCC(page,lambdaQueryWrapper);

        System.out.println("total=="+result.getTotal());

        return Result.suc(result.getRecords(),result.getTotal());
    }
}
