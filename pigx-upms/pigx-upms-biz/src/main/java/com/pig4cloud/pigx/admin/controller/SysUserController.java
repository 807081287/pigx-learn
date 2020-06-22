/*
 *
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the pig4cloud.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: lengleng (wangiegie@gmail.com)
 *
 */

package com.pig4cloud.pigx.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pigx.admin.api.dto.UserDTO;
import com.pig4cloud.pigx.admin.api.entity.SysUser;
import com.pig4cloud.pigx.admin.service.SysUserService;
import com.pig4cloud.pigx.common.core.util.ExcelExport;
import com.pig4cloud.pigx.common.core.util.ExcelReaderUtils;
import com.pig4cloud.pigx.common.core.util.R;
import com.pig4cloud.pigx.common.log.annotation.SysLog;
import com.pig4cloud.pigx.common.security.annotation.Inner;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * @author lengleng
 * @date 2018/12/16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/user")
@Api(value = "user", tags = "用户管理模块")
public class SysUserController {

	private static final Logger logger = LoggerFactory.getLogger(SysUserController.class);

	private final SysUserService userService;

	/**
	 * 获取指定用户全部信息
	 *
	 * @return 用户信息
	 */
	@Inner
	@GetMapping("/info/{username}")
	public R info(@PathVariable String username) {
		SysUser user = userService.getOne(Wrappers.<SysUser>query()
				.lambda().eq(SysUser::getUsername, username));
		if (user == null) {
			return R.failed(null, String.format("用户信息为空 %s", username));
		}
		return R.ok(userService.findUserInfo(user));
	}

	/**
	 * 通过ID查询用户信息
	 *
	 * @param id ID
	 * @return 用户信息
	 */
	@GetMapping("/{id}")
	public R user(@PathVariable Integer id) {
		return R.ok(userService.selectUserVoById(id));
	}

	/**
	 * 根据用户名查询用户信息
	 *
	 * @param username 用户名
	 * @return
	 */
	@GetMapping("/details/{username}")
	public R user(@PathVariable String username) {
		SysUser condition = new SysUser();
		condition.setUsername(username);
		return R.ok(userService.getOne(new QueryWrapper<>(condition)));
	}

	/**
	 * 删除用户信息
	 *
	 * @param id ID
	 * @return R
	 */
	@SysLog("删除用户信息")
	@DeleteMapping("/{id}")
	@PreAuthorize("@pms.hasPermission('sys_user_del')")
	@ApiOperation(value = "删除用户", notes = "根据ID删除用户")
	@ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "int", paramType = "path")
	public R userDel(@PathVariable Integer id) {
		SysUser sysUser = userService.getById(id);
		return R.ok(userService.deleteUserById(sysUser));
	}

	/**
	 * 添加用户
	 *
	 * @param userDto 用户信息
	 * @return success/false
	 */
	@SysLog("添加用户")
	@PostMapping
	@PreAuthorize("@pms.hasPermission('sys_user_add')")
	public R user(@RequestBody UserDTO userDto) {
		return R.ok(userService.saveUser(userDto));
	}

	/**
	 * 更新用户信息
	 *
	 * @param userDto 用户信息
	 * @return R
	 */
	@SysLog("更新用户信息")
	@PutMapping
	@PreAuthorize("@pms.hasPermission('sys_user_edit')")
	public R updateUser(@Valid @RequestBody UserDTO userDto) {
		return R.ok(userService.updateUser(userDto));
	}

	/**
	 * 分页查询用户
	 *
	 * @param page    参数集
	 * @param userDTO 查询参数列表
	 * @return 用户集合
	 */
	@GetMapping("/page")
	public R getUserPage(Page page, UserDTO userDTO) {
		return R.ok(userService.getUsersWithRolePage(page, userDTO));
	}

	/**
	 * 修改个人信息
	 *
	 * @param userDto userDto
	 * @return success/false
	 */
	@SysLog("修改个人信息")
	@PutMapping("/edit")
	public R updateUserInfo(@Valid @RequestBody UserDTO userDto) {
		return userService.updateUserInfo(userDto);
	}

	/**
	 * @param username 用户名称
	 * @return 上级部门用户列表
	 */
	@GetMapping("/ancestor/{username}")
	public R listAncestorUsers(@PathVariable String username) {
		return R.ok(userService.listAncestorUsers(username));
	}

	/**
	 * excel数据  批量插入
	 *
	 * @param file
	 * @param cusId
	 * @return
	 */
	@RequestMapping("/addBatchDevice")
	@ResponseBody
	@CrossOrigin
	public R addBatchDevice(MultipartFile file, @RequestParam String cusId) {
		try {
			//读取excel表格  返回的list是读取出的每一行数据
			List<List<String>> lists = ExcelReaderUtils.readExcel(file.getInputStream());
			//根据每一行的数据封装成javabean
			//mangeService.addBatchFarmDevice(lists, cusId);
		} catch (IOException e) {
			e.printStackTrace();
			return R.failed();
		}
		return R.ok();
	}

	/**
	 * 导出Excel所有
	 *
	 * @param response
	 */
	@GetMapping("/exportForXls")
	@ResponseBody
	@CrossOrigin
	public void exportExcel(HttpServletResponse response, Long companyId) {
		List<SysUser> list = userService.list();
		ExcelExport excelExport = new ExcelExport(response, "数据字典总数据", "sheet1");
		excelExport.writeExcel(new String[]{"code", "name", "enName", "level", "sort"}
				, new String[]{"编号", "名称", "英文名称", "层级", "排序"}
				, new int[]{30, 30, 30, 30, 30}, list);
	}


	/**
	 * excel数据  牧场数据导出xlsx
	 *
	 * @param cusId
	 * @return
	 */
	@GetMapping("/exportForXlsx")
	@ResponseBody
	@CrossOrigin
	public void export(@RequestParam String cusId, HttpServletResponse response) {
		List<SysUser> list = userService.list();
		if (list.size() > 0) {
			ExcelExport excelExport = new ExcelExport();
			String title = "客户数据导出Excel";
			String[] titleColumn = {"farmName", "farmContactName"}; //该牧场名称对应的属性farmName
			String[] titleName = {"牧场名称", "牧场主"}; //表头，列名
			int[] titleSize = {20, 20};//单元格宽高
			excelExport.writeBigExcel(response, title, titleColumn, titleName, titleSize, list);
		} else {
			logger.info("该客户下没有牧场！");
		}

	}



}
