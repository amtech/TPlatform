package org.tplatform.auth.service;

import org.tplatform.auth.entity.SysResource;
import org.tplatform.core.fsm.StatusEnum;
import org.tplatform.core.service.IBaseService;

import java.util.List;

/**
 * Created by Tianyi on 2016/3/18.
 */
public interface ISysResourceService extends IBaseService<SysResource> {

  /**
   * 查询菜单
   *
   * @param pid
   * @return
   */
  List<SysResource> findMenus(Long pid, StatusEnum statusEnum);

  /**
   * 根据角色查询菜单树
   *
   * @param roleId
   * @param status
   * @return
   */
  List<SysResource> findMenuTree(String roleId, StatusEnum status, Long parentCode);

  /**
   * 查询jstree
   *
   * @param pid
   * @param selectedIds
   * @param status
   * @return
   */
  List findForTree(Long pid, StatusEnum status, String selectedIds, Class<?> clazz);

  /**
   * 支持拖拽
   * @param id
   * @param pid
   * @return
   */
  public boolean updatePid(Long id, Long pid);

  /**
   * 保存
   * @param sysResource
   * @param roles
   * @return
   */
  public boolean saveWithRole(SysResource sysResource, Long[] roles);
}