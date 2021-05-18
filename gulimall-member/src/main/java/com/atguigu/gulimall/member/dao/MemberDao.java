package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author yaojunyi
 * @email yao_junyi@qq.com
 * @date 2021-05-18 16:56:12
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
