package com.ballistic.batch_report.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class BeanFactory {

    public static final Logger logger = LogManager.getLogger(BeanFactory.class);

    public BeanFactory() { }

    public Object getPojoBean(Object vo) throws Exception {
        String name = vo.getClass().getSimpleName();
        name = name.substring(0, name.length() - 2);
        Object ob = Class.forName("com.ballistic.batch_report.coredel.pojo." + name).newInstance();
        BeanUtils.copyProperties(vo, ob);
        return ob;
    }

    public Object getVOBean(Object pojo) throws Exception {
        String name = pojo.getClass().getSimpleName() + "Vo";
        Object ob = Class.forName("com.ballistic.batch_report.model.vo." + name).newInstance();
        BeanUtils.copyProperties(pojo, ob);
        return ob;
    }
}
