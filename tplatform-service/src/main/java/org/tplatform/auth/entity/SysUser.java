package org.tplatform.auth.entity;

import lombok.Data;
import org.tplatform.common.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

/**
 * 系统管理员
 * Created by Tianyi on 2014/11/21.
 */
@Data
@Entity
@Table(name = "SYS_AUTH_USER")
public class SysUser extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(length = 10)
  protected Long id;

  private String username;// 登录名，唯一，不可修改
  private String nickname;// 昵称
  private String password;// 登录密码
  private String mobile;  // 手机号
  private String email;   // 邮箱
  private String avatarUrl;  // 头像

  @ManyToMany(targetEntity = SysRole.class, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinTable(name = "SYS_AUTH_USER_ROLE", joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<SysRole> roles;

}