package com.uoh.model.dto.announcement;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddReading implements Serializable {

    /**
     * 公告ID
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
