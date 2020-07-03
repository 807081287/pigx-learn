/**
 * Copyright (C), 2019-2020, 成都房联云码科技有限公司
 * FileName: RemoteDeptService
 * Author:   Arron-wql
 * Date:     2020/7/3 9:51
 * Description: 部门服务
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.pig4cloud.pigx.admin.api.feign;

import com.pig4cloud.pigx.common.core.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 部门服务
 *
 * @author qinglong.wu
 * @create 2020/7/3
 * @Version 1.0.0
 */
@FeignClient(contextId = "remoteDeptService", value = ServiceNameConstants.UPMS_SERVICE)
public interface RemoteDeptService {
	/**
	 * 通过用户名查询用户、角色信息
	 *
	 * @param IdsString 部门iD的字符拼串
	 * @return 部门名称集合
	 */
	@GetMapping("/dept/deptNames/{IdsString}")
	List<Object> deptNames(@PathVariable("IdsString") String IdsString);
}
