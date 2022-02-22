package com.moshui.mall.controller.admin;

//轮播图

import com.moshui.mall.common.ServiceResultEnum;
import com.moshui.mall.entity.Carousel;
import com.moshui.mall.service.MallCarouselService;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.Result;
import com.moshui.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class MallCarouselController {

    @Resource
    private MallCarouselService mallCarouselService;

    @GetMapping("carousels")
    public String carousels(HttpServletRequest request){
        request.setAttribute("path","mall_carousels");
        return "/admin/mall_carousel";
    }

    //列表
    @GetMapping("/carousels/list")
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params){
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(mallCarouselService.findCarouselList(pageQueryUtil));
    }

    //添加
    @PostMapping("/carousels/save")
    @ResponseBody
    public Result save(@RequestBody Carousel carousel){
        String string = mallCarouselService.insert(carousel);
        if (ServiceResultEnum.SUCCESS.getResult().equals(string)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(string);
    }

    //修改
    @PostMapping("/carousels/update")
    @ResponseBody
    public Result update(@RequestBody Carousel carousel){
        String string = mallCarouselService.update(carousel);
        if (ServiceResultEnum.SUCCESS.getResult().equals(string)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(string);
    }

    //详情
    @GetMapping("/carousels/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Integer id){
        Carousel carousel = mallCarouselService.findById(id);
        if (carousel == null){
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(carousel);
    }

    //删除
    @PostMapping("/carousels/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (mallCarouselService.deleteBatch(ids)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

}
