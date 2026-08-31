package com.psychology.consultation.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("consultation_message")
public class ConsultationMessage {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("session_id")
    private Long sessionId;

    private String role;

    private String content;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
