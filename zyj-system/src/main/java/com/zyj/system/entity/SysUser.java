package com.zyj.system.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;

@Schema(title = "用户表")
@Data
@Entity
@Table(name = "sys_user")
public class SysUser implements Serializable {

    @Id
    @Column(length = 32)
    public String id;

    @Schema(title = "用户名")
    @Column(length = 50)
    public String username;

    @Schema(title = "密码")
    @Column(length = 50)
    public String password;

    @Schema(title = "账户是否过期")
    public boolean accountNonExpired;

    @Schema(title = "账户是否锁定")
    public boolean accountNonLocked;

    @Schema(title = "凭证（密码）是否过期")
    public boolean credentialsNonExpired;

    @Schema(title = "账户是否可用")
    public boolean enabled;


}
