package net.letuu.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Iterator;
import java.util.Map;

public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> T getBeanByType(Class<T> clazz){
	      Map<Object,T> beans = applicationContext.getBeansOfType(clazz);
	      
	      if(beans != null && !beans.isEmpty()){
	         Iterator<Object> iter = beans.keySet().iterator();
	         while(iter.hasNext()){
	            return beans.get(iter.next());
	         }
	      }
	      
	      return null;
	   }

}
