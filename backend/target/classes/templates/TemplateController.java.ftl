package ${packageName}.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${packageName}.annotation.AuthCheck;
import ${packageName}.common.BaseResponse;
import ${packageName}.common.DeleteRequest;
import ${packageName}.common.ErrorCode;
import ${packageName}.common.ResultUtils;
import ${packageName}.constant.UserConstant;
import ${packageName}.exception.BusinessException;
import ${packageName}.exception.ThrowUtils;
import ${packageName}.model.dto.${dataKey}.${upperDataKey}AddRequest;
import ${packageName}.model.dto.${dataKey}.${upperDataKey}EditRequest;
import ${packageName}.model.dto.${dataKey}.${upperDataKey}QueryRequest;
import ${packageName}.model.dto.${dataKey}.${upperDataKey}UpdateRequest;
import ${packageName}.model.entity.${upperDataKey};
import ${packageName}.model.entity.User;
import ${packageName}.model.vo.${upperDataKey}VO;
import ${packageName}.service.${upperDataKey}Service;
import ${packageName}.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * ${dataName}接口
 *
 */
@RestController
@RequestMapping("/${dataKey}")
@Slf4j
public class ${upperDataKey}Controller {

    @Resource
    private ${upperDataKey}Service ${dataKey}Service;


    // region 增删改查

    /**
     * 创建${dataName}
     *
     * @param ${dataKey}AddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> add${upperDataKey}(@RequestBody ${upperDataKey}AddRequest ${dataKey}AddRequest) {
        ThrowUtils.throwIf(${dataKey}AddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        ${upperDataKey} ${dataKey} = new ${upperDataKey}();
        BeanUtils.copyProperties(${dataKey}AddRequest, ${dataKey});
        // 数据校验
        ${dataKey}Service.valid${upperDataKey}(${dataKey}, true);
        // 写入数据库
        boolean result = ${dataKey}Service.save(${dataKey});
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long new${upperDataKey}Id = ${dataKey}.getId();
        return ResultUtils.success(new${upperDataKey}Id);
    }

    /**
     * 删除${dataName}
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> delete${upperDataKey}(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        ${upperDataKey} old${upperDataKey} = ${dataKey}Service.getById(id);
        ThrowUtils.throwIf(old${upperDataKey} == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = ${dataKey}Service.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新${dataName}
     *
     * @param ${dataKey}UpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> update${upperDataKey}(@RequestBody ${upperDataKey}UpdateRequest ${dataKey}UpdateRequest) {
        if (${dataKey}UpdateRequest == null || ${dataKey}UpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        ${upperDataKey} ${dataKey} = new ${upperDataKey}();
        BeanUtils.copyProperties(${dataKey}UpdateRequest, ${dataKey});
        // 数据校验
        ${dataKey}Service.valid${upperDataKey}(${dataKey}, false);
        // 判断是否存在
        long id = ${dataKey}UpdateRequest.getId();
        ${upperDataKey} old${upperDataKey} = ${dataKey}Service.getById(id);
        ThrowUtils.throwIf(old${upperDataKey} == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = ${dataKey}Service.updateById(${dataKey});
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取${dataName}（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<${upperDataKey}VO> get${upperDataKey}VOById(@RequestParam ("id") long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        ${upperDataKey} ${dataKey} = ${dataKey}Service.getById(id);
        ThrowUtils.throwIf(${dataKey} == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(${dataKey}Service.get${upperDataKey}VO(${dataKey}));
    }



    /**
     * 分页获取${dataName}列表（封装类）
     *
     * @param ${dataKey}QueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<${upperDataKey}VO>> list${upperDataKey}VOByPage(@RequestBody ${upperDataKey}QueryRequest ${dataKey}QueryRequest) {
        long current = ${dataKey}QueryRequest.getCurrentPage();
        long size = ${dataKey}QueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<${upperDataKey}> ${dataKey}Page = ${dataKey}Service.page(new Page<>(current, size),
                ${dataKey}Service.getQueryWrapper(${dataKey}QueryRequest));
        // 获取封装类
        return ResultUtils.success(${dataKey}Service.get${upperDataKey}VOPage(${dataKey}Page));
    }

    // endregion
}
