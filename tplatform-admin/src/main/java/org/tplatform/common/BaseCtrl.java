package org.tplatform.common;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.theme.AbstractThemeResolver;
import org.springframework.web.servlet.theme.SessionThemeResolver;
import org.tplatform.util.DateUtil;
import org.tplatform.util.Logger;
import org.tplatform.util.PropertyUtil;
import org.tplatform.util.StringUtil;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础业务控制器
 * Created by Tianyi on 2014/11/20.
 */
public abstract class BaseCtrl<E extends BaseEntity> {
  /**
   * 模版路径名称
   */
  protected final String dir;
  private final String moduleName;

  @Autowired
  protected HttpServletRequest request;
  @Autowired
  protected HttpSession session;
  @Autowired
  protected AbstractThemeResolver themeResolver;
  @Autowired
  protected BaseRepo<E> baseService;

  protected BaseCtrl() {
    Type type = this.getClass().getGenericSuperclass();
    if (type instanceof ParameterizedType) {
      String typeName = ((ParameterizedType) type).getActualTypeArguments()[0].getTypeName();
      String[] basePackages = PropertyUtil.getProInfo("config", "basePackage").split(",");
      for (String str : basePackages) {
        typeName = typeName.replaceAll(str, "");
      }
      String[] pack = typeName.split("\\.");
      dir = pack[pack.length - 2];
      moduleName = StringUtils.uncapitalize(pack[pack.length - 1]);
    } else {
      dir = "/";
      moduleName = "";
    }
  }

  /**
   * 默认列表页
   *
   * @return
   */
  @RequestMapping
  public String init(ModelMap modelMap) {
    request.setAttribute(GlobalConstant.APP_MODULE_NAME, moduleName);
    this.list(modelMap);
    return getDir() + "/" + this.moduleName + ".jsp";
  }


  //******************************** CURD公共方法 *******************************

  /**
   * Ajax加载数据
   *
   * @param e      业务实体
   * @param start  页码
   * @param length 每页显示记录数
   * @return
   */
  @RequestMapping("/load")
  @ResponseBody
  public RespBody load(E e, Integer start, Integer length) {
    Page page;
    if (StringUtil.isNotEmpty(e.getQ()) && StringUtil.isNotEmpty(e.getQNames())) {
      page = baseService.findAll((root, query, cb) -> {
        List<Predicate> whereList = new ArrayList<>();
        String q = "%" + e.getQ() + "%";
        String[] qNames = e.getQNames().split(",");
        for (String qName : qNames) {
          whereList.add(cb.like(root.get(qName), q));
        }
        return cb.or(whereList.toArray(new Predicate[whereList.size()]));
      }, new PageRequest(start / length, length));
    } else {
      page = baseService.findAll(Example.of(e), new PageRequest(start / length, length));
    }
    return RespBody.ok(page);
  }

  /**
   * 默认列表页
   *
   * @return
   */
  @RequestMapping("/list")
  public String list(ModelMap modelMap) {
    request.setAttribute(GlobalConstant.APP_MODULE_NAME, moduleName);
    return getDir() + "/" + this.moduleName + "List.jsp";
  }

  /**
   * 编辑、详情页
   *
   * @param id 主键
   * @return
   */
  @RequestMapping("/edit")
  public String edit(@RequestParam(value = "id", required = false) Serializable id, ModelMap modelMap) {
    request.setAttribute(GlobalConstant.APP_MODULE_NAME, moduleName);
    if (id != null) modelMap.put("data", baseService.findOne(id));
    return getDir() + "/" + this.moduleName + "Edit.jsp";
  }

  /**
   * 保存数据
   *
   * @param e 实体对象
   * @return
   */
  @RequestMapping(value = "/save", method = RequestMethod.POST)
  @ResponseBody
  public RespBody save(E e) {
    try {
      BeanWrapper bw = new BeanWrapperImpl(e);
      bw.setPropertyValue("createTime", DateUtil.getCurrentDate());
      e = (E) bw.getWrappedInstance();
    } catch (org.springframework.beans.BeansException ex) {
      Logger.w(ex.getMessage());
    }
    baseService.save(e);
    return RespBody.ok();
  }

  /**
   * 删除数据
   *
   * @param id 主键
   * @return
   */
  @RequestMapping("/delete/{id}")
  @ResponseBody
  public RespBody delete(@PathVariable(value = "id") Serializable id) {
    baseService.delete(id);
    return RespBody.ok("删除成功！");
  }

  protected String getDir() {
    return getDir(true);
  }

  protected String getDir(boolean useTheme) {
    return useTheme ? "/" + getThemeName() + "/" + dir : dir;
  }

  protected String getThemeName() {
    String themeName = (String) session.getAttribute(SessionThemeResolver.THEME_SESSION_ATTRIBUTE_NAME);
    return StringUtil.isEmpty(themeName) ? themeResolver.getDefaultThemeName() : themeName;
  }
}
