//package com.sz.fts.impl.test;
//
//import com.sz.fts.bean.engineer.ZsEngineerOrder;
//import com.sz.fts.dao.engineer.ZsEngineerOrderMapper;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.Resource;
//
///**
// * @author 征华兴
// * @date 上午 10:45  2019/4/10 0010
// * @Copyright 江苏鸿信系统集成有限公司 All rights reserved
// */
//@Service
//public class AccountService {
//
//    @Resource
//    private ZsEngineerOrderMapper zsEngineerOrder;
//
//    // 转账
//    public void transfer(int out, int in, int money) {
//
//        out(out, money);
//        //   int aa =1 /0;
//        in(in, money);
//    }
//
//    // 转账
//    @Transactional
//    public void save1(int out, int in, int money) {
//
//            out(out, money);
//            int aa = 1 / 0;
//            in(in, money);
//
//    }
//
//    // 转账
//    public void out(int out, int money) {
//        System.out.println("----------1111------------");
//        ZsEngineerOrder order = this.zsEngineerOrder.selectByPrimaryKey(out);
//        order.setStatus(order.getStatus() - money);
//        this.zsEngineerOrder.updateByPrimaryKeySelective(order);
//    }
//
//    // 转账
//    public void in(int in, int money) {
//        System.out.println("----------222------------");
//        ZsEngineerOrder order = this.zsEngineerOrder.selectByPrimaryKey(in);
//        order.setStatus(order.getStatus() + money);
//        this.zsEngineerOrder.updateByPrimaryKeySelective(order);
//    }asss
//
//}
