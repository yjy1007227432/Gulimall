package com.dalianpai.gulimall.seckill.service;

public interface DistributedSemaphore {

/**
  * 尝试获取一个信号量
  *
  * @return true 获取成功, false 获取失败
  */
        boolean tryAcquire();

/**
  * 释放自己持有的信号量
  */
        void release();
}

