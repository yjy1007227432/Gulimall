package com.atguigu.gulimall.member.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.dao.MemberLevelDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.atguigu.gulimall.member.exception.PhoneNumExistException;
import com.atguigu.gulimall.member.exception.UserExistException;
import com.atguigu.gulimall.member.service.MemberService;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberRegisterVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {


    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override // service
    public void register(MemberRegisterVo userRegisterVo)  throws PhoneNumExistException, UserExistException {

        MemberEntity entity = new MemberEntity();
        // 设置默认等级
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();
        entity.setLevelId(memberLevelEntity.getId());

        // 检查手机号 用户名是否唯一
        checkPhone(userRegisterVo.getPhone());
        checkUserName(userRegisterVo.getUserName());

        entity.setMobile(userRegisterVo.getPhone());
        entity.setUsername(userRegisterVo.getUserName());

        // 密码要加密存储
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        entity.setPassword(bCryptPasswordEncoder.encode(userRegisterVo.getPassword()));
        // 其他的默认信息
        entity.setCity("湖南 长沙");
        entity.setCreateTime(new Date());
        entity.setStatus(0);
        entity.setNickname(userRegisterVo.getUserName());
        entity.setBirth(new Date());
        entity.setEmail("xxx@gmail.com");
        entity.setGender(1);
        entity.setJob("JAVA");


        baseMapper.insert(entity);
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


        QueryWrapper<MemberEntity> queryWrapper = new QueryWrapper<>();
        // 去数据库查询
        MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct).or().eq("mobile", loginacct));
        if(entity == null){
            // 登录失败
            return null;
        }else{
            // 前面传一个明文密码 后面传一个编码后的密码
            boolean matches = bCryptPasswordEncoder.matches(vo.getPassword(), entity.getPassword());
            if (matches){
                entity.setPassword(null);
                return entity;
            }else {
                return null;
            }
        }
    }


    public void checkPhone(String phone) throws PhoneNumExistException{
        if(this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone)) > 0){
            throw new PhoneNumExistException();
        }
    }


    public void checkUserName(String name) throws UserExistException{
        if(this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username",name))>0){
            throw new UserExistException();
        }
    }


}