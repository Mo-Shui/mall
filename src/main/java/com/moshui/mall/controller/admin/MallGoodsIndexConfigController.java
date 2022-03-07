package com.moshui.mall.controller.admin;

import com.moshui.mall.common.Constants;
import com.moshui.mall.common.IndexConfigTypeEnum;
import com.moshui.mall.common.ServiceResultEnum;
import com.moshui.mall.entity.Goods;
import com.moshui.mall.entity.IndexConfig;
import com.moshui.mall.service.MallIndexConfigService;
import com.moshui.mall.util.PageQueryUtil;
import com.moshui.mall.util.Result;
import com.moshui.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class MallGoodsIndexConfigController {

    @Resource
    private MallIndexConfigService mallIndexConfigService;

    //首页配置管理
    @RequestMapping("/indexConfigs")
    public String indexConfigs(HttpServletRequest request, @RequestParam("configType") int configType){
        request.setAttribute("path", IndexConfigTypeEnum.getIndexConfigTypeEnumByType(configType));
        request.setAttribute("configType",configType);
        return "admin/mall_index_config";
    }

    //列表
    @RequestMapping("/indexConfigs/list")
    @ResponseBody
    public Result list(@RequestParam Map params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }

        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(mallIndexConfigService.getConfigsPage(pageQueryUtil));
    }

    //添加
    @RequestMapping("/indexConfigs/save")
    @ResponseBody
    public Result save(@RequestBody IndexConfig indexConfig){
        if (Objects.isNull(indexConfig.getConfigType())
                || StringUtils.isEmpty(indexConfig.getConfigName())
                || Objects.isNull(indexConfig.getConfigRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = mallIndexConfigService.saveIndexConfig(indexConfig);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }

    //修改
    @RequestMapping("/indexConfigs/update")
    @ResponseBody
    public Result update(@RequestBody IndexConfig indexConfig){
        if (Objects.isNull(indexConfig.getConfigType())
                || Objects.isNull(indexConfig.getConfigId())
                || StringUtils.isEmpty(indexConfig.getConfigName())
                || Objects.isNull(indexConfig.getConfigRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = mallIndexConfigService.updateIndexConfig(indexConfig);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }

    //删除
    @RequestMapping("/indexConfigs/delete")
    @ResponseBody
    public Result delete(@RequestBody Long[] ids){
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (mallIndexConfigService.deleteBatch(ids)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

}
