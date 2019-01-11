package com.rwb.service;

import com.rwb.api.ZKLockTest;
import com.rwb.util.DubbobBean;
import org.springframework.stereotype.Service;

@Service
public class ZKLockTestService {
    private ZKLockTest lockTest;

    {
        synchronized (this){
            if (lockTest == null){
                lockTest = DubbobBean.getBean("lockTest",ZKLockTest.class);
            }
        }
    }

    public void Append(){
        for (int i=0;i<10;i++){
            lockTest.Append();
        }
    }

    public String getNum(){
        return "返回的数值为 ： "+ lockTest.getNum();
    }
}
