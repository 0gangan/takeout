 package com.lzg.takeout.service.impl;

import com.lzg.takeout.entity.Merchant;
import com.lzg.takeout.entity.User;
import com.lzg.takeout.repository.MerchantRepository;
import com.lzg.takeout.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 /**
  * 作者：lzg
  * 日期：2025-12-28
  *
  * UserCrudServiceImpl 的单元测试类
  * 主要用于验证用户管理模块中
  * 用户的增删改查及关联商家查询等核心业务逻辑。
  */
 @ExtendWith(MockitoExtension.class)
 class UserCrudServiceImplTest {

     @Mock
     private UserRepository userRepository;

     @Mock
     private MerchantRepository merchantRepository;

     @InjectMocks
     private UserCrudServiceImpl userService;

     /**
      * 测试场景：
      * 查询所有用户
      *
      * 预期结果：
      * 返回的数据列表与仓库层返回的数据一致
      */
     @Test
     void findAll_shouldReturnListFromRepo() {
         User u1 = new User();
         u1.setId(1L);
         User u2 = new User();
         u2.setId(2L);
         when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

         List<User> all = userService.findAll();
         assertEquals(2, all.size());
     }

     /**
      * 测试场景：
      * 根据用户 ID 查询用户，且用户存在
      *
      * 预期结果：
      * 返回包含用户信息的 Optional 对象
      */
     @Test
     void findById_found() {
         User u = new User();
         u.setId(5L);
         when(userRepository.findById(5L)).thenReturn(Optional.of(u));

         Optional<User> opt = userService.findById(5L);
         assertTrue(opt.isPresent());
         assertEquals(5L, opt.get().getId());
     }

     /**
      * 测试场景：
      * 创建新用户
      *
      * 预期结果：
      * 用户信息被成功保存并返回
      */
     @Test
     void create_shouldSave() {
         User u = new User();
         u.setUsername("a");
         when(userRepository.save(u)).thenReturn(u);

         User saved = userService.create(u);
         assertEquals("a", saved.getUsername());
         verify(userRepository, times(1)).save(u);
     }

     /**
      * 测试场景：
      * 更新已存在的用户信息
      *
      * 预期结果：
      * 用户信息被正确修改并保存
      */
     @Test
     void update_existing_shouldModifyAndSave() {
         User existing = new User();
         existing.setId(7L);
         existing.setUsername("old");
         when(userRepository.findById(7L)).thenReturn(Optional.of(existing));
         when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

         User updated = new User();
         updated.setUsername("new");
         Optional<User> res = userService.update(7L, updated);

         assertTrue(res.isPresent());
         assertEquals("new", res.get().getUsername());
     }

     /**
      * 测试场景：
      * 删除不存在的用户
      *
      * 预期结果：
      * 删除失败并返回 false
      */
     @Test
     void deleteById_notExists_returnsFalse() {
         when(userRepository.existsById(9L)).thenReturn(false);

         boolean r = userService.deleteById(9L);
         assertFalse(r);
     }

     /**
      * 测试场景：
      * 判断用户名是否存在
      *
      * 预期结果：
      * 当用户名存在时返回 true
      */
     @Test
     void existsByUsername_shouldReturnTrueWhenPresent() {
         when(userRepository.findByUsername("u"))
                 .thenReturn(Optional.of(new User()));

         assertTrue(userService.existsByUsername("u"));
     }

     /**
      * 测试场景：
      * 根据用户 ID 查询其关联的商家信息
      *
      * 预期结果：
      * 返回对应的商家信息
      */
     @Test
     void findMerchantByUserId_shouldDelegate() {
         Merchant m = new Merchant();
         m.setId(11L);
         when(merchantRepository.findByUserId(3L))
                 .thenReturn(Optional.of(m));

         Optional<Merchant> opt = userService.findMerchantByUserId(3L);
         assertTrue(opt.isPresent());
         assertEquals(11L, opt.get().getId());
     }
 }

