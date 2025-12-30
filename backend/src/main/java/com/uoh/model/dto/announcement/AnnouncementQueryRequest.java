package com.uoh.model.dto.announcement;

import com.uoh.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询通告管理请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AnnouncementQueryRequest extends PageRequest implements Serializable {


    private String title;

    private Integer type;

    private static final long serialVersionUID = 1L;
}