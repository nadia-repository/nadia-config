package com.nadia.config.user.repo;

import com.nadia.config.user.dao.UserMapper;
import com.nadia.config.user.domain.User;
import com.nadia.config.user.domain.UserCriteria;
import com.nadia.config.user.dto.request.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Repository
@Slf4j
public class UserRepo {

    @Resource
    private UserMapper userMapper;

    public void updateByPrimaryKeySelective(User user) {
        userMapper.updateByPrimaryKeySelective(user);
    }

    public User selectByName(String name) {
        log.info("getUserByName:{}", name);
        UserCriteria example = new UserCriteria();
        UserCriteria.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(name);
        List<User> users = userMapper.selectByExample(example);
        return CollectionUtils.isEmpty(users) ? null : users.get(0);
    }

    public long countByName(String name) {
        UserCriteria example = new UserCriteria();
        UserCriteria.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(name);
        return userMapper.countByExample(example);
    }

    public long countByMail(String mail) {
        UserCriteria example = new UserCriteria();
        UserCriteria.Criteria criteria = example.createCriteria();
        criteria.andEmailEqualTo(mail);
        return userMapper.countByExample(example);
    }

    public User selectByNameOrMail(String nameOrMail){
        User user = userMapper.selectByNameOrMail(nameOrMail);
        return user;
    }

    public User selectByPrimaryKey(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    public List<User> selectByUserRequest(UserRequest userRequest) {

        UserCriteria example = new UserCriteria();
        UserCriteria.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(userRequest.getStatus())) {
            criteria.andStatusEqualTo(userRequest.getStatus());
        }
        if (!StringUtils.isEmpty(userRequest.getName())) {
            criteria.andNameLike(userRequest.getName());
        }
        if (!StringUtils.isEmpty(userRequest.getMail())) {
            criteria.andEmailLike(userRequest.getMail());
        }
        return userMapper.selectByExample(example);
    }

    public int insertSelective(User record){
        return userMapper.insertSelective(record);
    }
}
