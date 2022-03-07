package com.moshui.mall.controller.admin;

import com.moshui.mall.entity.AdminUser;
import com.moshui.mall.service.AdminUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminUserService adminUserService;

    @GetMapping("/indexAll")
    public String indexAll(){
        return "admin/index-all";
    }

    @GetMapping("/index")
    public String index(){
        return "admin/index";
    }

    //登录Get
    @GetMapping("/login")
    public String login(){
        return "admin/login";
    }

    //登录Post
    @PostMapping("/login")
    public String login(String userName, String password, String verifyCode, HttpSession session){
        if (userName == null || userName.equals("") || password == null || password.equals("")){
            session.setAttribute("errorMsg","用户名或密码不能为空");
            return "admin/login";
        }
        if (verifyCode == null || verifyCode.equals("")){
            session.setAttribute("errorMsg","验证码不能为空");
            return "admin/login";
        }

        String sessionVerifyCode = (String) session.getAttribute("verifyCode");
        if (sessionVerifyCode == null || sessionVerifyCode.equals("") || !sessionVerifyCode.equals(verifyCode)){
            session.setAttribute("errorMsg","验证码错误");
            return "admin/login";
        }

        AdminUser adminUser = adminUserService.login(userName, password);
        if (adminUser != null){
            session.setAttribute("loginUser",adminUser.getNickName());
            session.setAttribute("loginUserId",adminUser.getAdminUserId());

            return "redirect:/admin/index";
        }

        session.setAttribute("errorMsg","登录失败");
        return "admin/login";
    }

    //根据用户id获取用户信息
    @GetMapping("/profile")
    public String profile(HttpServletRequest request){
        Integer userId = (Integer) request.getSession().getAttribute("loginUserId");
        AdminUser adminUser = adminUserService.getUserDetailById(userId);
        if (adminUser != null){
            request.setAttribute("path","profile");
            request.setAttribute("loginUserName",adminUser.getLoginUserName());
            request.setAttribute("nickName",adminUser.getNickName());
            return "admin/profile";
        }

        return "admin/login";
    }

    //修改用户名
    @PostMapping("/profile/name")
    @ResponseBody
    public String updateName(String loginUserName,String nickName,HttpServletRequest request){
        if (loginUserName == null || nickName == null || loginUserName.equals("") || nickName.equals("")){
            return "参数不能为空";
        }

        Integer userId = (Integer) request.getSession().getAttribute("loginUserId");
        if (adminUserService.updateNameById(userId,loginUserName,nickName)){
            return "success";
        }
        return "修改失败";
    }

    //修改密码
    @PostMapping("/profile/password")
    @ResponseBody
    public String updatePassword(String originalPassword,String newPassword,HttpServletRequest request){
        if (originalPassword == null || newPassword == null || originalPassword.equals("") || newPassword.equals("")){
            return "参数不能为空";
        }

        Integer userId = (Integer) request.getSession().getAttribute("loginUserId");
        if (adminUserService.updatePasswordById(userId,originalPassword,newPassword)){
            //修改成功后清空Session中的数据，前端控制跳转至登录页
            HttpSession session = request.getSession();
            session.removeAttribute("loginUserId");
            session.removeAttribute("loginUser");
            session.removeAttribute("errorMsg");
            return "success";
        }
        return "修改失败";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.removeAttribute("loginUserId");
        session.removeAttribute("loginUser");
        session.removeAttribute("errorMsg");

        return "admin/login";
    }

}
