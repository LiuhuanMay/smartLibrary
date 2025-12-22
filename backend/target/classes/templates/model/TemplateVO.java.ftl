package ${packageName}.model.vo;

import cn.hutool.json.JSONUtil;
import ${packageName}.model.entity.${upperDataKey};
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * ${dataName}视图
 *
 */
@Data
public class ${upperDataKey}VO implements Serializable {


    /**
     * 封装类转对象
     *
     * @param ${dataKey}VO
     * @return
     */
    public static ${upperDataKey} voToObj(${upperDataKey}VO ${dataKey}VO) {
        if (${dataKey}VO == null) {
            return null;
        }
        ${upperDataKey} ${dataKey} = new ${upperDataKey}();
        BeanUtils.copyProperties(${dataKey}VO, ${dataKey});
        return ${dataKey};
    }

    /**
     * 对象转封装类
     *
     * @param ${dataKey}
     * @return
     */
    public static ${upperDataKey}VO objToVo(${upperDataKey} ${dataKey}) {
        if (${dataKey} == null) {
            return null;
        }
        ${upperDataKey}VO ${dataKey}VO = new ${upperDataKey}VO();
        BeanUtils.copyProperties(${dataKey}, ${dataKey}VO);
        return ${dataKey}VO;
    }
}
