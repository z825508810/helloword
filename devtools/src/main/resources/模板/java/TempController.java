package #{path}.controller;

import #{path}.domain.#{obj};
import #{path}.service.#{obj}Service;
import #{path}.validator.#{obj}Form;
import com.linln.admin.core.enums.ResultEnum;
import com.linln.admin.core.enums.StatusEnum;
import com.linln.admin.core.exception.ResultException;
import com.linln.admin.core.utils.TimoExample;
import com.linln.core.utils.FormBeanUtil;
import com.linln.core.utils.ResultVoUtil;
import com.linln.core.vo.ResultVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/#{var}")
public class #{obj}Controller {

    @Autowired
    private #{obj}Service #{var}Service;

    /**
     * 列表页面
     * @param pageIndex 页码
     * @param pageSize 获取数据长度
     */
    @GetMapping("/index")
    @RequiresPermissions("/#{var}/index")
    public String index(Model model, #{obj} #{var},
            @RequestParam(value="page",defaultValue="1") int pageIndex,
            @RequestParam(value="size",defaultValue="10") int pageSize){

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("title", match -> match.contains());

        // 获取#{title}列表
        Example<#{obj}> example = TimoExample.of(#{var}, matcher);
        Page<#{obj}> list = #{var}Service.getPageList(example, pageIndex, pageSize);

        // 封装数据
        model.addAttribute("list",list.getContent());
        model.addAttribute("page",list);
        return "/system/#{var}/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("/#{var}/add")
    public String toAdd(){
        return "/system/#{var}/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("/#{var}/edit")
    public String toEdit(@PathVariable("id") Long id, Model model){
        #{obj} #{var} = #{var}Service.getId(id);
        model.addAttribute("#{var}",#{var});
        return "/system/#{var}/add";
    }

    /**
     * 保存添加/修改的数据
     * @param #{var}Form 表单验证对象
     */
    @PostMapping({"/add","/edit"})
    @RequiresPermissions({"/#{var}/add","/#{var}/edit"})
    @ResponseBody
    public ResultVo save(@Validated #{obj}Form #{var}Form){

        // 将验证的数据复制给实体类
        #{obj} #{var} = new #{obj}();
        if(#{var}Form.getId() != null){
            #{var} = #{var}Service.getId(#{var}Form.getId());
        }
        FormBeanUtil.copyProperties(#{var}Form, #{var});

        // 保存数据
        #{var}Service.save(#{var});
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("/#{var}/detail")
    public String toDetail(@PathVariable("id") Long id, Model model){
        #{obj} #{var} = #{var}Service.getId(id);
        model.addAttribute("#{var}",#{var});
        return "/system/#{var}/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("/#{var}/status")
    @ResponseBody
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> idList){
        try {
            // 获取状态StatusEnum对象
            StatusEnum statusEnum = StatusEnum.valueOf(param.toUpperCase());
            // 更新状态
            Integer count = #{var}Service.updateStatus(statusEnum,idList);
            if (count > 0){
                return ResultVoUtil.success(statusEnum.getMessage()+"成功");
            }else{
                return ResultVoUtil.error(statusEnum.getMessage()+"失败，请重新操作");
            }
        } catch (IllegalArgumentException e){
            throw new ResultException(ResultEnum.STATUS_ERROR);
        }
    }
}
